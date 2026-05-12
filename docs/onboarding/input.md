# Onboarding Screen - Input Requirements

Dựa trên 4 hình ảnh thiết kế được cung cấp, dưới đây là tổng hợp yêu cầu giao diện và tính năng cho luồng Onboarding:

## Tổng quan
Luồng Onboarding bao gồm 4 trang (pages) vuốt ngang (Horizontal Pager). Dưới mỗi trang có một hàng chứa chỉ báo (Pager Indicators) và một nút hành động.

## Cấu trúc từng trang

### Trang 1
* **Hình ảnh minh họa:** Ứng dụng hiển thị trên điện thoại trong bối cảnh phòng khách.
* **Tiêu đề (Title):** `Smart IPTV Player`
* **Mô tả (Subtitle):** `Stream your favorite playlists and media seamlessly on any device.`
* **Chỉ báo trang (Indicators):** Gồm 4 điểm. Điểm số 1 ở dạng viên nang dài (capsule) màu đen (active). Các điểm 2, 3, 4 là hình tròn màu xám (inactive).
* **Nút bấm:** Nút viền (Outline button), bo góc tròn (pill shape), có chữ `NEXT`.

### Trang 2
* **Hình ảnh minh họa:** Người đàn ông đang xem TV với các nội dung nổi lên như không gian 3D.
* **Tiêu đề (Title):** `Diverse Content Universe`
* **Mô tả (Subtitle):** `Access a massive library of movies, shows, and games instantly.`
* **Chỉ báo trang (Indicators):** Điểm số 2 là viên nang màu đen (active). Các điểm 1, 3, 4 là hình tròn màu xám (inactive).
* **Nút bấm:** Nút viền, có chữ `NEXT`.

### Trang 3
* **Hình ảnh minh họa:** Tay cầm điện thoại dùng làm điều khiển để cast video lên TV.
* **Tiêu đề (Title):** `Seamless Cast & Control`
* **Mô tả (Subtitle):** `Cast to your TV with one tap and manage everything from your phone.`
* **Chỉ báo trang (Indicators):** Điểm số 3 là viên nang màu đen (active). Các điểm 1, 2, 4 là hình tròn màu xám (inactive).
* **Nút bấm:** Nút viền, có chữ `NEXT`.

### Trang 4
* **Hình ảnh minh họa:** Cắt ghép nhiều poster phim ảnh/chương trình TV.
* **Tiêu đề (Title):** `Personalized Entertainment`
* **Mô tả (Subtitle):** `Discover and watch live IPTV shared from around the word.` (Có thể sửa lỗi chính tả "word" thành "world").
* **Chỉ báo trang (Indicators):** Điểm số 4 là viên nang màu đen (active). Các điểm 1, 2, 3 là hình tròn màu xám (inactive).
* **Nút bấm:** Nút viền, có chữ `START`.

## Yêu cầu tương tác (UX)
1. Người dùng có thể vuốt ngang để chuyển đổi giữa các trang.
2. Bấm nút `NEXT` sẽ tự động chuyển sang trang tiếp theo.
3. Bấm nút `START` ở trang cuối cùng sẽ hoàn thành Onboarding và điều hướng vào màn hình chính của ứng dụng.
