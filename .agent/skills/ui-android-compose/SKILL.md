---
name: ui-android-compose
description: Kỹ năng chuyên biệt để xây dựng giao diện bằng Jetpack Compose, tổ chức Composable, quản lý state và tuân thủ Android best practices. Dùng cùng android-resource-policy khi cần quản lý resource.
---
# jetpack-compose-ui

Kỹ năng này chuyên dùng cho việc xây dựng (implement) giao diện người dùng (UI) bằng Jetpack Compose trong Android.
Kỹ năng này tập trung vào cách tổ chức code Compose, quản lý state, lifecycle, performance, accessibility và preview. Khi cần xử lý string, color, icon, font, drawable hoặc resource Android, áp dụng thêm skill `android-resource-policy`.

## 1. Luồng làm việc (Workflow)

Khi nhận yêu cầu xây dựng UI cho Android bằng Compose:
1. **Phân tích Design**: Xem xét Design System, layout, màu sắc, typography và trạng thái UI cần hỗ trợ.
2. **Cấu trúc Composable**: Phân rã giao diện thành các hàm `@Composable` nhỏ, có thể tái sử dụng. Tuân thủ nguyên lý *Stateless by default* (hoist state lên cha).
3. **Quản lý Resources**: Dùng `android-resource-policy` để kiểm tra và tái sử dụng resources hiện có trước khi thêm mới.
4. **Implement UI**: Sử dụng Modifier và các layout chuẩn (`Column`, `Row`, `Box`, `LazyColumn`) cùng với các component cơ bản của Compose.
5. **Tích hợp logic**: Kết nối với ViewModel/Data layer thông qua event callbacks, không chứa logic nghiệp vụ trong Composable.
6. **Tạo Preview**: Tạo hàm `@Preview` cho mỗi màn hình (Screen) và các Component quan trọng, có chứa mock data.

## 2. Jetpack Compose Best Practices

| Rule | Do | Don't |
|------|----|----- |
| **State Hoisting** | Đẩy state lên component cha, truyền giá trị (`value`) xuống và nhận event (`onValueChange`) từ con. | Giữ local state `remember { mutableStateOf() }` trong các component cần tái sử dụng hoặc chia sẻ. |
| **Saveable State** | Dùng `rememberSaveable` cho state UI cần sống qua configuration change hoặc process recreation. | Dùng `remember` cho state quan trọng như input đang nhập, tab đang chọn, filter đang bật nếu cần restore. |
| **Stable UI State** | Truyền UI state immutable/stable, ưu tiên data class bất biến và collection bất biến hoặc không mutate trực tiếp. | Truyền mutable list/map/state object rồi mutate trong Composable. |
| **Modifiers** | Sử dụng Modifier để căn chỉnh, padding. Ưu tiên thứ tự Modifier (vd: padding trước background vs background trước padding). | Bọc thêm các layout (`Box`, `Column`) dư thừa chỉ để thêm padding. |
| **Lazy Layouts** | Dùng `LazyColumn`/`LazyRow` cho danh sách dài hoặc động. | Dùng `Column` + `verticalScroll` cho list quá dài làm giảm performance. |
| **Recomposition** | Dùng `derivedStateOf` khi tính toán state thường xuyên (như `listState.firstVisibleItemIndex`). | Tính toán lại giá trị nặng nề trong mỗi vòng lặp recomposition. |
| **Side Effects** | Sử dụng đúng side effects: `LaunchedEffect` (chạy suspend), `DisposableEffect` (cần dọn dẹp), `rememberCoroutineScope` (chỉ dùng cho UI events). | Khởi tạo coroutine bừa bãi trong thân hàm Composable. |
| **Lifecycle** | Thu thập flow từ ViewModel sử dụng `collectAsStateWithLifecycle()`. | Dùng `collectAsState()` có thể gây rò rỉ bộ nhớ hoặc app vẫn thu thập dữ liệu ở background. |
| **Previews** | Bắt buộc tạo hàm `@Preview` cho mỗi màn hình (Screen) hoặc component chính để hiển thị UI trực quan (khuyến khích truyền mock data). | Xây dựng UI mà không tạo Preview, bắt buộc phải run app mới xem được thiết kế. |
| **Localization & Scale** | Thiết kế UI chịu được text dài, font scale lớn, RTL khi app hỗ trợ nhiều ngôn ngữ. Dùng plural/format string đúng khi cần. | Cố định width/height làm text bị cắt hoặc ghép chuỗi thủ công gây lỗi dịch thuật. |

## 3. Pre-Delivery Checklist (Dành riêng cho Compose)

Trước khi hoàn thành code Compose, hãy kiểm tra:

### Resources & Assets
- [ ] Đã áp dụng `android-resource-policy` cho string, color, icon, drawable, font và dimension.
- [ ] Text tĩnh dùng resource; text động từ state/API/user input không đưa vào `strings.xml`.
- [ ] UI không bị vỡ với text dài, font scale lớn, và RTL nếu app hỗ trợ.

### Kiến trúc & Hiệu năng
- [ ] Các màn hình chính (Screen level) chỉ nhận UI State và truyền events cho ViewModel.
- [ ] Các Component con đều là Stateless (nhận properties, trả events).
- [ ] State cần restore dùng `rememberSaveable` hoặc được hoist lên ViewModel/UI state.
- [ ] UI state truyền vào Composable là immutable/stable; không mutate collection trực tiếp trong Composable.
- [ ] Không có layout nào bị lồng (nested) quá mức không cần thiết.
- [ ] Danh sách luôn sử dụng `LazyColumn` / `LazyRow` với `key` phù hợp cho các items.

### Giao diện (UI/UX)
- [ ] Ripple effect và click/tap states hoạt động chính xác (đảm bảo clickable/tappable elements có feedback).
- [ ] Đã có sẵn hàm `@Preview` cho mỗi màn hình (Screen) và các Component quan trọng, có chứa mock data.
