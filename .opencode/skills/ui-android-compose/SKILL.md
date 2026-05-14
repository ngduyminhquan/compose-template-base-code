---
name: ui-android-compose
description: Kỹ năng chuyên biệt để xây dựng giao diện bằng Jetpack Compose, quản lý resources và tuân thủ Android best practices.
---
# jetpack-compose-ui

Kỹ năng này chuyên dùng cho việc xây dựng (implement) giao diện người dùng (UI) bằng Jetpack Compose trong Android.
Nó kế thừa các nguyên tắc thiết kế UI/UX từ `ui-ux-pro-max` nhưng tập trung vào cách tổ chức code, quản lý resources và best practices đặc thù của Jetpack Compose.

## 1. Luồng làm việc (Workflow)

Khi nhận yêu cầu xây dựng UI cho Android bằng Compose:
1. **Phân tích Design**: Xem xét Design System, layout, màu sắc, typography (thường được cung cấp từ kỹ năng ui-ux-pro-max).
2. **Cấu trúc Composable**: Phân rã giao diện thành các hàm `@Composable` nhỏ, có thể tái sử dụng. Tuân thủ nguyên lý *Stateless by default* (hoist state lên cha).
3. **Quản lý Resources**: Kiểm tra và tái sử dụng resources hiện có (màu, chuỗi, icon). Rút trích các giá trị mới vào file cấu hình.
4. **Implement UI**: Sử dụng Modifier và các layout chuẩn (`Column`, `Row`, `Box`, `LazyColumn`) cùng với các component cơ bản của Compose.
5. **Tích hợp logic**: Kết nối với ViewModel/Data layer thông qua event callbacks, không chứa logic nghiệp vụ trong Composable.

## 2. Resource Management (Quy tắc KHÔNG Hardcode)

Đây là quy tắc tối quan trọng khi làm việc với Android UI:

| Rule | Do                                                                                                                                                   | Don't |
|------|------------------------------------------------------------------------------------------------------------------------------------------------------|----- |
| **Không hardcode giá trị** | Rút trích strings, colors, fonts, và dimensions ra các file tương ứng (`res/values/strings.xml`, `res/font/`, `Color.kt`, hoặc file constant).           | Viết cứng string (`"Hello"`), màu sắc (`Color(0xFF0000)`), font, hoặc kích thước trực tiếp trong UI code. |
| **Tái sử dụng Assets & Resource** | **HẠN CHẾ TỐI ĐA VIỆC SỬ DỤNG ICON/STRING/COLOR/DIMEN CÓ SẴN CỦA ANDROID. DUYỆT QUA CÁC THƯ MỤC RES TRONG PROJECT ĐỂ TÌM TÀI NGUYÊN PHÙ HỢP TRƯỚC. NẾU CÓ THÌ DÙNG, KHÔNG CÓ THÌ MỚI ĐƯỢC DÙNG CỦA ANDROID.** | Dùng thẳng icon/color/string của Android/Material mà không thèm kiểm tra tài nguyên project. |
| **Sử dụng màu xác định (Không dùng MaterialTheme)** | Define rõ ràng các màu sắc (có thể tạo file chứa constant màu) và set trực tiếp vào Composable.                                                      | Phụ thuộc vào MaterialTheme hoặc hardcode mã hex rải rác khắp nơi. |
| **Sử dụng Icons** | **BẮT BUỘC** duyệt tìm icon phù hợp trong thư mục `drawable` trước. Chỉ khi nào THẬT SỰ KHÔNG CÓ mới dùng đến `Icons` của compose material/Android mặc định. | Luôn dùng `Icons.Default...` hoặc `@android:drawable/...` mà không kiểm tra `drawable`. |

## 3. Jetpack Compose Best Practices

| Rule | Do | Don't |
|------|----|----- |
| **State Hoisting** | Đẩy state lên component cha, truyền giá trị (`value`) xuống và nhận event (`onValueChange`) từ con. | Giữ local state `remember { mutableStateOf() }` trong các component cần tái sử dụng hoặc chia sẻ. |
| **Modifiers** | Sử dụng Modifier để căn chỉnh, padding. Ưu tiên thứ tự Modifier (vd: padding trước background vs background trước padding). | Bọc thêm các layout (`Box`, `Column`) dư thừa chỉ để thêm padding. |
| **Lazy Layouts** | Dùng `LazyColumn`/`LazyRow` cho danh sách dài hoặc động. | Dùng `Column` + `verticalScroll` cho list quá dài làm giảm performance. |
| **Recomposition** | Dùng `derivedStateOf` khi tính toán state thường xuyên (như `listState.firstVisibleItemIndex`). | Tính toán lại giá trị nặng nề trong mỗi vòng lặp recomposition. |
| **Side Effects** | Sử dụng đúng side effects: `LaunchedEffect` (chạy suspend), `DisposableEffect` (cần dọn dẹp), `rememberCoroutineScope` (chỉ dùng cho UI events). | Khởi tạo coroutine bừa bãi trong thân hàm Composable. |
| **Lifecycle** | Thu thập flow từ ViewModel sử dụng `collectAsStateWithLifecycle()`. | Dùng `collectAsState()` có thể gây rò rỉ bộ nhớ hoặc app vẫn thu thập dữ liệu ở background. |
| **Previews** | Bắt buộc tạo hàm `@Preview` cho mỗi màn hình (Screen) hoặc component chính để hiển thị UI trực quan (khuyến khích truyền mock data). | Xây dựng UI mà không tạo Preview, bắt buộc phải run app mới xem được thiết kế. |

## 4. Pre-Delivery Checklist (Dành riêng cho Compose)

Trước khi hoàn thành code Compose, hãy kiểm tra:

### Resources & Assets
- [ ] KHÔNG CÓ bất kỳ chuỗi text (string) nào bị hardcode. Mọi text đều dùng `stringResource()`.
- [ ] KHÔNG CÓ mã màu (color) nào bị hardcode. Mọi màu đều lấy từ file định nghĩa màu sắc.
- [ ] Kích thước (dp, sp) đã được tham chiếu từ resources nếu cần thiết.
- [ ] Font chữ đã được tham chiếu từ hệ thống Typography hoặc resource chuẩn, không khởi tạo font trực tiếp bừa bãi.
- [ ] Đã kiểm tra thư mục `res/drawable` để tìm icon phù hợp trước khi dùng `Icons` của Compose.
- [ ] Đã kiểm tra thư mục `res/values/` và `res/font/` để tái sử dụng ảnh/chuỗi/font đã có.

### Kiến trúc & Hiệu năng
- [ ] Các màn hình chính (Screen level) chỉ nhận UI State và truyền events cho ViewModel.
- [ ] Các Component con đều là Stateless (nhận properties, trả events).
- [ ] Không có layout nào bị lồng (nested) quá mức không cần thiết.
- [ ] Danh sách luôn sử dụng `LazyColumn` / `LazyRow` với `key` phù hợp cho các items.

### Giao diện (UI/UX)
- [ ] Ripple effect và Hover/Click states hoạt động chính xác (đảm bảo clickable/tappable elements có feedback).
- [ ] Giao diện hỗ trợ tốt cả Dark Mode và Light Mode (kiểm tra màu sắc ở 2 mode).
- [ ] Hỗ trợ khả năng truy cập (Accessibility): Sử dụng `contentDescription` cho mọi hình ảnh, icon có ý nghĩa.
- [ ] Đã có sẵn hàm `@Preview` cho mỗi màn hình (Screen) và các Component quan trọng, có chứa mock data.
