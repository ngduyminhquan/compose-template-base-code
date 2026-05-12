# Đặc tả Yêu cầu Phần mềm (SRS) - Màn hình Onboarding

## 1. Giới thiệu
Màn hình Onboarding được hiển thị lần đầu tiên khi người dùng mở ứng dụng, nhằm giới thiệu các tính năng nổi bật của hệ thống.

## 2. Các thành phần giao diện (UI Components)
Màn hình Onboarding là một thanh trượt ngang (Horizontal Pager) bao gồm 4 trang nội dung.
Mỗi trang bao gồm các thành phần bố cục chung sau:
- **Hình ảnh minh họa (Image):** Chiếm phần lớn không gian phía trên màn hình.
- **Tiêu đề (Title):** Văn bản in đậm mô tả tính năng chính.
- **Mô tả (Subtitle):** Văn bản chi tiết giải thích cho tiêu đề.
- **Chỉ báo trang (Pager Indicators):** Nằm ở phía dưới bên trái, bao gồm 4 điểm tương ứng với 4 trang. Điểm của trang hiện tại có dạng viên nang (capsule) màu đen, các điểm còn lại có dạng hình tròn màu xám.
- **Nút hành động (Action Button):** Nằm ở phía dưới bên phải, dạng nút viền bo góc tròn (pill-shaped outline button).

### Nội dung chi tiết từng trang
*   **Trang 1:**
    *   Title: "Smart IPTV Player"
    *   Subtitle: "Stream your favorite playlists and media seamlessly on any device."
    *   Button Text: "NEXT"
*   **Trang 2:**
    *   Title: "Diverse Content Universe"
    *   Subtitle: "Access a massive library of movies, shows, and games instantly."
    *   Button Text: "NEXT"
*   **Trang 3:**
    *   Title: "Seamless Cast & Control"
    *   Subtitle: "Cast to your TV with one tap and manage everything from your phone."
    *   Button Text: "NEXT"
*   **Trang 4:**
    *   Title: "Personalized Entertainment"
    *   Subtitle: "Discover and watch live IPTV shared from around the world."
    *   Button Text: "START"

## 3. Tương tác người dùng và Phản hồi hệ thống (User Actions & System Responses)

### Hành động 1: Vuốt ngang (Swipe)
- **User Action:** Người dùng vuốt màn hình sang trái hoặc sang phải.
- **System Response:** 
  - Chuyển tiếp sang trang tương ứng với hướng vuốt.
  - Cập nhật trạng thái của Chỉ báo trang (Pager Indicators) để làm nổi bật trang hiện tại.
  - Cập nhật văn bản của nút hành động ("NEXT" cho trang 1-3, "START" cho trang 4).

### Hành động 2: Nhấn nút "NEXT" (Trang 1, 2, 3)
- **User Action:** Người dùng chạm vào nút "NEXT".
- **System Response:**
  - Tự động cuộn mượt mà (smooth scroll) sang trang tiếp theo (ví dụ: từ trang 1 sang trang 2).
  - Cập nhật Chỉ báo trang và văn bản nút.

### Hành động 3: Nhấn nút "START" (Trang 4)
- **User Action:** Người dùng chạm vào nút "START".
- **System Response:**
  - Kết thúc luồng Onboarding.
  - Điều hướng (navigate) người dùng vào màn hình tiếp theo của ứng dụng (VD: Home Screen).
