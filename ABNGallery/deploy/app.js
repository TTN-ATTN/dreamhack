const express = require('express');
const fs = require('fs');
const dns = require('dns').promises;
const path = require('path');
const ip = require('ip');

const app = express();
const PORT = process.env.PORT || 3000;

const LOG_DIR = path.resolve(__dirname, 'logs');
const IMAGE_DIR = path.resolve(__dirname, 'images');

const LOCAL_FILES = {
  amo: 'amo.png',
  boko: 'boko.png',
  nando: 'nando.png',
};

function parseIncomingUrl(raw) {
  try {
    const u = new URL(raw);
    if (!/^https?:$/.test(u.protocol)) throw new Error('Only http/https allowed');
    return u;
  } catch {
    const err = new Error('Invalid URL');
    err.status = 400;
    throw err;
  }
}

async function fetchImage(urlObj, signal) {
    const res = await fetch(urlObj.toString(), {
      redirect: 'manual',
      signal,
      headers: {
        'User-Agent': 'ImageProxy/1.0',
        'Accept': 'image/*,*/*;q=0.8',
      },
    });

    if (res.ok) return res;
  
    const err = new Error(
      res.status >= 300 && res.status < 400
        ? `Redirect not allowed (status ${res.status})`
        : `Upstream responded with status ${res.status}`
    );
    err.status = 502;
    throw err;  
}

function extractHost(url) {
  const schemeSplit = url.indexOf("://");
  let rest = schemeSplit !== -1 ? url.slice(schemeSplit + 3) : url;

  const slashIndex = rest.indexOf("/");
  if (slashIndex !== -1) {
    rest = rest.slice(0, slashIndex);
  }

  const colonIndex = rest.indexOf(":");
  if (colonIndex !== -1) {
    rest = rest.slice(0, colonIndex);
  }

  return rest;
}

async function hostIsPublic(hostname) {
  try {
    return ip.isPublic(hostname);
  } catch {
    const addrs = await dns.lookup(hostname, { all: true }).catch(() => []);
    if (!addrs.length) return false;
    return addrs.every(({ address }) => ip.isPublic(address));
  }
}


app.get('/gallery/:id', (req, res) => {
  try {
    const id = req.params.id;
    const filename = LOCAL_FILES[id];

    const absPath = path.join(IMAGE_DIR, filename);

    res.setHeader('Content-Type', 'image/png');
    const stream = fs.createReadStream(absPath);
    stream.on('error', () => res.status(500).end());
    stream.pipe(res);
  } catch (e) {
    res.status(404).json({ error: 'Image not found' });
  }

});

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

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
