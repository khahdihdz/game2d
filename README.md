# Sniper Assassin — Operation Nightfall

Game giải đố bắn tỉa 2D chạy trực tiếp trên trình duyệt, lấy cảm hứng từ game Flash cổ điển. Đây là dự án độc lập; nhân vật, bối cảnh và nhiệm vụ đều hư cấu.

## Tính năng

- Chiến dịch 10 nhiệm vụ với hồ sơ và mục tiêu riêng.
- Đồ họa 2D Canvas, tự vẽ bằng mã nguồn, không phụ thuộc tài nguyên hình ảnh bên ngoài.
- Điều khiển bằng chuột hoặc cảm ứng, tâm ngắm và chế độ ống ngắm.
- Đạn giới hạn, tính điểm, độ chính xác, thắng/thua và mở khóa nhiệm vụ.
- Lưu tiến trình bằng `localStorage`.
- Giao diện responsive cho máy tính và thiết bị di động.
- Không cần backend, API key hoặc dependency runtime.

## Chạy local

Có thể mở `index.html` trực tiếp. Khuyến nghị dùng máy chủ tĩnh:

```bash
python -m http.server 8000
```

Truy cập http://localhost:8000.

## Deploy GitHub Pages

Workflow tại `.github/workflows/deploy.yml` tự động deploy khi push lên nhánh `main`.

1. Vào **Settings → Pages** của repository.
2. Trong **Build and deployment → Source**, chọn **GitHub Actions**.
3. Mở tab **Actions** và kiểm tra workflow deploy.
4. Khi workflow thành công, game dự kiến có tại https://khahdihdz.github.io/game2d/.

Có thể chạy thủ công từ tab Actions bằng **Run workflow**.

## Khắc phục sự cố

- **404:** xác nhận Pages dùng nguồn GitHub Actions, workflow đã chạy thành công và URL đúng là `https://khahdihdz.github.io/game2d/`.
- **Workflow không chạy:** kiểm tra nhánh `main`, trạng thái Actions và quyền deploy Pages.
- **Tiến trình không đồng bộ:** dữ liệu chỉ lưu trong trình duyệt hiện tại, không tự đồng bộ giữa các thiết bị.
- **Không lưu được:** kiểm tra cài đặt quyền lưu dữ liệu của trình duyệt.

## Công nghệ

HTML5, CSS3, JavaScript và Canvas 2D. Không có bước build hoặc thư viện runtime bên ngoài.

## Giấy phép

Mã nguồn phát hành theo MIT. Không bao gồm tài nguyên độc quyền từ trò chơi khác.
