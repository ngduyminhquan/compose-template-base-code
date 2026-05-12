---
name: Feature Development Workflow
description: Workflow chuẩn dành cho Antigravity để xây dựng một tính năng mới trong dự án.
---

# Workflow: Xây dựng tính năng mới

**Mục đích:** Hướng dẫn các bước chuẩn để Antigravity thực hiện khi nhận yêu cầu xây dựng một tính năng mới từ User.

## QUY TẮC CỐT LÕI (BẮT BUỘC)
- **TẤT CẢ CÁC BƯỚC** đều phải TUÂN THỦ NGHIÊM NGẶT theo tài liệu: `@[.agent/rules/code-style-guide.md]`.
- Sau khi hoàn thành MỖI BƯỚC, phải đánh dấu bước đó là **HOÀN THÀNH** trong file theo dõi tiến độ, thông báo cho User và **CHỜ USER XÁC NHẬN** mới được phép chuyển sang bước tiếp theo.
- Nếu User yêu cầu sửa đổi ở một bước, phải tiếp tục thực hiện và chỉnh sửa ở bước đó cho đến khi User xác nhận mới được đi tiếp.

---

## CÁC BƯỚC THỰC HIỆN

### Bước 1: Khởi tạo File Theo Dõi Tiến Độ
1. Tạo một file markdown (ví dụ: `docs/<feature_name>/tracking.md`) tổng hợp list ra từng bước của workflow này.
2. Đánh dấu hoàn thành Bước 1 trong file tiến độ (Ví dụ: từ `[ ]` thành `[x]`).
3. Thông báo cho User rằng file tiến độ đã được tạo và yêu cầu User xác nhận để bắt đầu Bước 2.

### Bước 2: Nhận Thông Tin Đầu Vào (Design / Requirement)
1. Hỏi User cung cấp design (ảnh / thiết kế Figma) và/hoặc văn bản mô tả màn hình.
2. Tổng hợp lại tất cả thông tin đầu vào.
3. Nếu User cung cấp ảnh: Lưu file ảnh vào một thư mục thích hợp (ví dụ: `docs/<feature_name>/images/`) và nhúng (embed) ảnh đó vào file input bằng cú pháp markdown (VD: `![Design](đường_dẫn_ảnh)`). Các bước sau sẽ sử dụng chính ảnh này.
4. Nếu User cung cấp link Figma: Chèn trực tiếp link Figma vào file input.
5. Tạo file `input.md` tại folder `docs/<feature_name>/` và lưu toàn bộ nội dung yêu cầu, mô tả, hình ảnh và link của User vào file này.
6. Đánh dấu hoàn thành Bước 2 trong file tiến độ.
7. Thông báo cho User nội dung file input và chờ User xác nhận.

### Bước 3: Tạo Tài Liệu Đặc Tả Yêu Cầu (SRS)
1. Đọc nội dung file `input.md` đã tạo ở Bước 2.
2. Sử dụng kỹ năng `@[.agent/skills/srs-generator/SKILL.md]` để tạo tài liệu Đặc tả Yêu cầu Phần mềm (SRS).
3. Lưu kết quả dưới dạng file markdown tại `docs/<feature_name>/srs.md`.
4. Đánh dấu hoàn thành Bước 3 trong file tiến độ.
5. Thông báo cho User và chờ User duyệt nội dung SRS.

### Bước 4: Thiết Kế Kiến Trúc (Architecture)
1. Kết hợp file SRS (từ Bước 3) và yêu cầu hệ thống.
2. Sử dụng kỹ năng `@[.agent/skills/architecture-designer/SKILL.md]` để tạo bản thiết kế kiến trúc hệ thống (UI Layer, Data Layer, Domain, Interfaces...).
3. Lưu tài liệu kiến trúc tại `docs/<feature_name>/architecture.md`.
4. Đánh dấu hoàn thành Bước 4 trong file tiến độ.
5. Thông báo cho User và chờ User duyệt thiết kế kiến trúc.

### Bước 5: Triển Khai Code Tính Năng
1. Dựa vào cấu trúc project hiện tại, file `input.md` (Bước 2), bản Architecture (Bước 4) và SRS (Bước 3).
2. Tùy theo công nghệ sử dụng để dựng UI mà User yêu cầu, áp dụng các kỹ năng chuyên biệt:
   - `@[.agent/skills/ui-android-xml/SKILL.md]` (nếu dùng XML Layout)
   - `@[.agent/skills/ui-android-compose/SKILL.md]` (nếu dùng Jetpack Compose)
3. Code các thành phần theo đúng cấu trúc thiết kế.
4. Đánh dấu hoàn thành Bước 5 trong file tiến độ.
5. Thông báo cho User hoàn tất việc code và chờ xác nhận.

### Bước 6: Build Dự Án và Khắc Phục Lỗi (Fix Bugs)
1. Tiến hành chạy lệnh build project (`./gradlew assembleDebug` hoặc tương tự) để kiểm tra lỗi compile/lint.
2. Nếu xuất hiện lỗi, tự động đọc error log và tiến hành fix lỗi cho đến khi quá trình build thành công hoàn toàn.
3. Đánh dấu hoàn thành Bước 6 trong file tiến độ.
4. Thông báo với User rằng tính năng đã được xây dựng, build thành công và hoàn tất workflow.
