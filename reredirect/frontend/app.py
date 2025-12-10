from flask import Flask, redirect, request, render_template
import socket

app = Flask(__name__)

DEFAULT_CODE = "302"
BACKEND_IP = "backend"
BACKEND_PORT = 80
URL = "http://example.com"

@app.route("/")
def index():
    return render_template(
        "index.html",
        current_url=URL,
        current_code=DEFAULT_CODE
    )

@app.route("/re", methods=["GET", "POST"])
def go_redirect():
    return redirect(URL, code=int(DEFAULT_CODE))

@app.route("/setting_re")
def set_redirect():
    global URL, DEFAULT_CODE

    URL = request.args.get("url", "http://example.com")
    DEFAULT_CODE = request.args.get("code", "302")

    return render_template(
        "setting_re.html",
        url=URL,
        code=DEFAULT_CODE
    )

@app.route("/trigger_reset")
def trigger_reset():
    user_key = request.args.get("key")

    if not user_key:
        return render_template("error_no_key.html"), 400

    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((BACKEND_IP, BACKEND_PORT))

        request_packet = (
            f"GET /reset HTTP/1.1\r\n"
            f"Host: {BACKEND_IP}\r\n"
            f"Connection: keep-alive\r\n"
            f"X-Reset-Key: {user_key}\r\n\r\n"
        )

        s.sendall(request_packet.encode())

        response = b""
        s.settimeout(1.5)

        try:
            while True:
                data = s.recv(4096)
                if not data:
                    break
                response += data
        except socket.timeout:
            pass

        s.close()

        return render_template("reset_response.html", response=response.decode(errors="ignore"))

    except Exception as e:
        return render_template("socket_error.html", error=str(e)), 500
    
if __name__ == '__main__':
    app.run(host="0.0.0.0", debug=True, port=81)
