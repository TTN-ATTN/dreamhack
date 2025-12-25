const express = require('express');
const session = require('express-session');
const puppeteer = require('puppeteer-core');
const crypto = require('node:crypto');

const PORT = process.env.PORT || 3000;
const FLAG = process.env.FLAG || 'DH{FAKE_FLAG}';

function getRandomHex(size) {
  return crypto.randomBytes(size).toString('hex');
}

function isLocalhost(ip) {
  return ip === '::1' || ip === '::ffff:127.0.0.1';
}

const users = new Map();

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(session({
  secret: process.env.SESSION_SECRET || 'secret',
  resave: false,
  saveUninitialized: false,
}));
app.use((req, res, next) => {
  res.setHeader('X-Frame-Options', 'DENY');
  res.setHeader('Content-Security-Policy', "default-src 'self'; script-src 'unsafe-inline'; frame-ancestors 'none';");
  next();
});

app.get('/', (req, res) => {
  return res.send('Hello');
});

app.get('/login', (req, res) => {
  if (isLocalhost(req.ip) &&
    (req.headers['sec-fetch-mode'] !== 'navigate' || req.headers['sec-fetch-dest'] !== 'document')) {
    return res.send('Top level navigation required');
  }

  req.session.csrfToken = getRandomHex(16);
  return res.send(`
    <form method="post">
      Username: <input type="text" name="username"><br>
      Password: <input type="password" name="password"><br>
      <input type="hidden" name="csrfToken" value="${req.session.csrfToken}">
      <input type="submit" value="Login">
    </form>
  `);
});

app.post('/login', (req, res) => {
  const { username, password, csrfToken } = req.body;

  if (!username || typeof username !== 'string' ||
    !password || typeof password !== 'string') {
    return res.send('<script>alert("Invalid input");history.back();</script>');
  }

  if (!req.session.csrfToken || csrfToken !== req.session.csrfToken) {
    return res.send('<script>alert("CSRF token mismatch");history.back();</script>');
  }

  if (!users.has(username) || users.get(username).password !== password) {
    return res.send('<script>alert("Invalid credentials");history.back();</script>');
  }

  req.session.username = username;
  req.session.isAdmin = isLocalhost(req.ip);
  return res.redirect('/');
});

app.get('/register', (req, res) => {
  if (isLocalhost(req.ip) &&
    (req.headers['sec-fetch-mode'] !== 'navigate' || req.headers['sec-fetch-dest'] !== 'document')) {
    return res.send('Top level navigation required');
  }

  req.session.csrfToken = getRandomHex(16);
  return res.send(`
    <form method="post">
      Username: <input type="text" name="username"><br>
      Password: <input type="password" name="password"><br>
      <input type="hidden" name="csrfToken" value="${req.session.csrfToken}">
      <input type="submit" value="Register">
    </form>
  `);
});

app.post('/register', (req, res) => {
  const { username, password, csrfToken } = req.body;

  if (!username || typeof username !== 'string' ||
    !password || typeof password !== 'string') {
    return res.send('<script>alert("Invalid input");history.back();</script>');
  }

  if (!req.session.csrfToken || csrfToken !== req.session.csrfToken) {
    return res.send('<script>alert("CSRF token mismatch");history.back();</script>');
  }

  if (users.has(username)) {
    return res.send('<script>alert("User already exists");history.back();</script>');
  }

  users.set(username, { password: password });
  return res.send('<script>alert("Registration successful");location="/login";</script>');
});

app.get('/flag', (req, res) => {
  if (!req.session.isAdmin || !isLocalhost(req.ip) || req.headers['sec-fetch-mode'] !== 'navigate') {
    return res.send('<script>alert("Access denied");history.back();</script>');
  }

  if (req.headers['sec-fetch-dest'] === 'document') {
    return res.send(FLAG.slice(0, 8));
  } else {
    return res.send(FLAG.slice(8));
  }
});

app.get('/report', (req, res) => {
  return res.send(`
    <form method="post">
      Report URL: <input type="text" name="url"><br>
      <input type="submit" value="Report">
    </form>
  `);
});

app.post('/report', async (req, res) => {
  const { url } = req.body;

  if (!url || typeof url !== 'string') {
    return res.send('<script>alert("Invalid input");history.back();</script>');
  }

  if (!url.startsWith('http://') && !url.startsWith('https://')) {
    return res.send('<script>alert("Invalid URL");history.back();</script>');
  }

  visit(url);
  return res.send('<script>alert("URL reported successfully");history.go(-1);</script>');
});

async function visit(url) {
  const browser = await puppeteer.launch({
    headless: true,
    ignoreDefaultArgs: ['--disable-popup-blocking'],
    args: [
      '--no-sandbox',
      '--disable-gpu',
      '--disable-dev-shm-usage',
      `--unsafely-treat-insecure-origin-as-secure=${new URL(url).origin}`, // to always send fetch metadata
    ],
    executablePath: '/usr/bin/google-chrome-stable',
  });
  try {
    const page = await browser.newPage();
    await page.setBypassServiceWorker(true);
    await page.goto(url, { waitUntil: 'networkidle0', timeout: 15000 });
  } catch (error) {
    console.error(error);
  } finally {
    await browser.close();
  }
};

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});