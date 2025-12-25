from flask import Flask, request, render_template, Response
import os, subprocess

app = Flask(__name__)

PORT = int(os.getenv("PORT", "8080"))
JAR_PATH = os.getenv("JAR_PATH", "/app/swing.jar")

@app.get("/")
def index():
    return render_template("index.html")

@app.post("/submit")
def submit():
    t1 = request.form.get("text1", "")
    t2 = request.form.get("text2", "")

    try:
        p = subprocess.run(
            ["java", "-Djava.awt.headless=true", "-jar", JAR_PATH, t1, t2],
            capture_output=True,
            text=True,
            timeout=15,
        )
        result = "success" if p.returncode == 0 else "fail"
    except Exception:
        result = "error"

    return Response(
        f"""<!doctype html><html><body>
<script>alert("{result}");location.replace("/");</script>
</body></html>""",
        mimetype="text/html",
    )

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=PORT)
