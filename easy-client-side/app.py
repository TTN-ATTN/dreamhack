from flask import Flask, request, render_template
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
import os

app = Flask(__name__)
app.secret_key = os.urandom(32)

FLAG = open("./flag.txt").read()

def urlread(url, cookie={"name": "name", "value": "value"}):
    cookie.update({"domain": "127.0.0.1"})
    driver = None
    try:
        service = Service(executable_path="/usr/bin/chromedriver")
        options = webdriver.ChromeOptions()
        for _ in [
            "headless",
            "window-size=1920x1080",
            "disable-gpu",
            "no-sandbox",
            "disable-dev-shm-usage",
        ]:
            options.add_argument(_)
        driver = webdriver.Chrome(service=service, options=options)
        driver.implicitly_wait(3)
        driver.set_page_load_timeout(3)
        driver.get("http://127.0.0.1:8000/")
        driver.add_cookie(cookie)
        driver.get(url)
    except Exception as e:
        if driver:
            driver.quit()
        return False
    driver.quit()
    return True

def check(payload, cookie={"name": "name", "value": "value"}):
    return urlread(payload, cookie)


@app.route("/")
def index():
    return render_template("index.html")


@app.route("/xss")
def xss():
    return render_template("xss.html")

@app.route("/report", methods=["GET", "POST"])
def report():
    if request.method == "GET":
        return render_template("report.html")
    elif request.method == "POST":
        payload = request.form.get("payload")
        if not check(payload, {"name": "flag", "value": FLAG.strip()}):
            return '<script>alert("nope!");history.go(-1);</script>'
        return '<script>alert("nice!");history.go(-1);</script>'

app.run(host="0.0.0.0", port=8000, load_dotenv=False)