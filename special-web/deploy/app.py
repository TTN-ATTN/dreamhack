from flask import Flask, request, make_response, render_template
import bleach
from selenium.webdriver.chrome.service import Service
from selenium import webdriver
import time
import os

app = Flask(__name__)

key = os.urandom(32).hex()

def clean(msg):
    allowed_tags = ['a', 'b', 'br', 'font', 'h1', 'i', 'math', 'p', 'span', 'strong', 'style', 'u']

    allowed_attrs = {
        'a': ['href', 'title'],
        'font': ['color', 'size']
    }

    cleaned = bleach.clean(msg, tags=allowed_tags, attributes=allowed_attrs, strip_comments=False)
    
    return cleaned

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

@app.route('/event')
def event():
    msg = request.args.get('msg', '')

    clean_msg = clean(msg)
    
    return render_template('event.html', 
                         cookie_name=msg, 
                         clean_name=clean_msg)

@app.route('/send')
def send():
    name = request.args.get('name')
    
    url = f'http://127.0.0.1:5000/event?msg={name}'
    
    result = read_url(url)
    
    if result:
        return '<script>alert("Submission received!"); history.back();</script>'
    else:
        return '<script>alert("An error occurred!"); history.back();</script>'

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000,debug=False)