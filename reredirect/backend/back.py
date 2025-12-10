import threading
import re
import time
import requests
import os
from flask import Flask, request, redirect

app = Flask(__name__)

CODE = 302
FRONT_IP = "http://frontend:81/re"
RESET_KEY = os.urandom(4).hex()
MAIN_URL = "http://example.com"
TARGET_URL = "http://example.com"
INTERVAL = 10
DATA = {"status": "alive", "flag": "B1N4RY{**redacted**}"}

def send_periodic_post_requests():
    session = requests.Session()
    
    while True:
        try:
            response = session.post(TARGET_URL, json=DATA, timeout=5, verify=True)
            print(f"[DEBUG] POST to {TARGET_URL} - status code: {response.status_code}")
        except requests.exceptions.RequestException as e:
            print(f"[ERROR] Request failed: {e}")
        
        time.sleep(INTERVAL)

@app.route("/reset")
def reset():
    global TARGET_URL
    global MAIN_URL
    req_key = request.headers.get("X-Reset-Key")

    if str(req_key) != str(RESET_KEY):
        return "Unauthorized: Invalid reset key", 401
    
    TARGET_URL = MAIN_URL
    return f"Host reset to default: {TARGET_URL}"


@app.route("/settings")
def settings():
    global TARGET_URL
    global MAIN_URL
    global CODE

    new_host = request.args.get("host")
    new_path = request.args.get("path")
    new_code = request.args.get("code")
    
    if new_host is None:
        return f"Current host: {TARGET_URL}"

    if len(new_host) > 6:
        new_host = MAIN_URL

    if new_host.startswith("http://") or new_host.startswith("https://"):
        host_str = new_host.split("://", 1)[1]
    else:
        host_str = new_host

    if not re.fullmatch(r"[a-z.]+", host_str):
        new_host = MAIN_URL
    else:
        if not (new_host.startswith("http://") or new_host.startswith("https://")):
            new_host = "http://" + new_host

    if new_path is None:
        new_path = ""

    if len(new_path) > 2:
        new_path = ""

    if not re.fullmatch(r"[a-z]*", new_path):
        new_path = ""

    if new_path and not new_path.startswith("/"):
        new_path = "/" + new_path

    new_url = new_host + new_path
    TARGET_URL = new_url

    if new_code is not None:
        try:
            CODE = int(new_code)
        except ValueError:
            CODE = 302

    return f"Host updated to: {TARGET_URL}, code={CODE}"

@app.route("/re", methods=["GET", "POST"])
def go_redirect():
    return redirect(FRONT_IP, code=CODE)

def start_background_thread():
    thread = threading.Thread(target=send_periodic_post_requests, daemon=True)
    thread.start()