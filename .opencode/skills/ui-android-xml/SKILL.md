---
name: ui-android-xml
description: Kỹ năng chuyên biệt để xây dựng giao diện bằng Android XML Layout, quản lý resources và tuân thủ Android best practices.
---
# android-xml-ui

Kỹ năng này chuyên dùng cho việc xây dựng (implement) giao diện người dùng (UI) bằng XML Layout trong Android.
Kỹ năng này tập trung vào cách tổ chức layout XML, quản lý View hierarchy, binding, lifecycle, performance, accessibility và preview bằng `tools:` namespace. Khi cần xử lý string, color, icon, font, drawable hoặc resource Android, áp dụng thêm skill `android-resource-policy`.

## 1. Luồng làm việc (Workflow)

Khi nhận yêu cầu xây dựng UI cho Android bằng XML:
1. **Phân tích Design**: Xem xét Design System, layout, màu sắc, typography và trạng thái UI cần hỗ trợ.
2. **Cấu trúc Layout**: Phân rã giao diện thành layout chính, item layout và reusable layout. Chọn ViewGroup phù hợp (`ConstraintLayout`, `LinearLayout`, `FrameLayout`, `RecyclerView`) và ưu tiên flat hierarchy.
3. **Quản lý Resources**: Dùng `android-resource-policy` để kiểm tra và tái sử dụng resources hiện có trước khi thêm mới.
4. **Implement UI**: Viết XML layout, style, theme overlay, drawable shape/selector theo convention project.
5. **Tích hợp logic**: Đặt `android:id` rõ ràng cho View cần binding, truyền dữ liệu từ Fragment/Activity/ViewModel, không chứa logic nghiệp vụ trong custom View hoặc adapter binding.
6. **Tạo Preview**: Dùng namespace `tools:` (`tools:text`, `tools:visibility`, `tools:listitem`, `tools:src`) để hiển thị trạng thái preview trong Android Studio mà không ảnh hưởng runtime.

## 2. Android XML Best Practices

| Rule | Do | Don't |
|------|----|----- |
| **Flat Hierarchy** | Dùng `ConstraintLayout` cho layout phức tạp để giảm nested ViewGroup. Dùng `LinearLayout`/`FrameLayout` khi layout đơn giản hơn. | Lồng nhiều `LinearLayout`/`RelativeLayout` chỉ để căn chỉnh cơ bản. |
| **ConstraintLayout** | Dùng constraint rõ ràng, bias, chain, guideline/barrier khi phù hợp. | Dùng constraint thiếu chiều ngang/dọc hoặc abuse `layout_margin` để thay constraint đúng. |
| **Reusable Layout** | Dùng `<include>`, `<merge>` hoặc style cho phần UI lặp lại nhiều nơi. | Copy-paste block XML giống nhau sang nhiều layout. |
| **ViewStub** | Dùng `ViewStub` cho UI phức tạp hiếm khi hiển thị như error detail hoặc optional panel. | Inflate sẵn view phức tạp rồi để `android:visibility="gone"` nếu hiếm dùng. |
| **RecyclerView Items** | Giữ item layout gọn, ổn định kích thước khi có thể, có `tools:` preview data. | Tạo item layout nested sâu hoặc thay đổi size bất thường gây jank khi scroll. |
| **Binding** | Dùng ViewBinding/DataBinding theo convention project; adapter chỉ bind UI state và emit events. | Đưa network/database/business logic vào Fragment, custom View hoặc adapter binding. |
| **Lifecycle** | Observe UI state theo lifecycle owner (`viewLifecycleOwner` trong Fragment) và clear binding đúng lifecycle. | Giữ reference tới ViewBinding sau `onDestroyView()` hoặc observe bằng Fragment lifecycle sai scope. |
| **Click & States** | Dùng selector/ripple (`?attr/selectableItemBackground`, `android:foreground`) cho View click được. | Để button/card/list item click được nhưng không có visual feedback. |
| **Accessibility** | Đặt `contentDescription` cho ảnh có ý nghĩa, dùng `importantForAccessibility`, label và focus order khi cần. | Đọc trùng nội dung, bỏ mô tả icon action hoặc hardcode content description. |
| **Localization & Scale** | Thiết kế chịu được text dài, font scale lớn, RTL. Dùng `start/end` thay vì `left/right` khi phù hợp. | Cố định width/height làm text bị cắt hoặc khóa layout theo trái/phải không cần thiết. |

## 3. Pre-Delivery Checklist (Dành riêng cho XML)

Trước khi hoàn thành code XML Layout, hãy kiểm tra:

### Resources & Assets
- [ ] Đã áp dụng `android-resource-policy` cho string, color, icon, drawable, font và dimension.
- [ ] Text tĩnh dùng `@string/...`; text động từ state/API/user input không đưa vào `strings.xml`.
- [ ] Màu dùng theme/design token/resource theo convention project và hỗ trợ Light/Dark mode.
- [ ] Icon/drawable đã kiểm tra trong `res/drawable` hoặc design system trước khi dùng default resource.
- [ ] UI không bị vỡ với text dài, font scale lớn, và RTL nếu app hỗ trợ.

### Kiến trúc Layout & Hiệu năng
- [ ] Layout không lồng nhau quá mức không cần thiết; đã dùng `ConstraintLayout`, `<merge>` hoặc ViewGroup đơn giản đúng chỗ.
- [ ] Các View cần tương tác từ code có `android:id` rõ ràng, theo convention project.
- [ ] Phần layout dùng chung đã tách bằng `<include>`/`<merge>` hoặc style nếu tái sử dụng thật sự.
- [ ] Item layout cho `RecyclerView` gọn, có preview data bằng `tools:` khi cần.
- [ ] Fragment/Activity observe state đúng lifecycle và không giữ binding sau khi View bị destroy.

### Giao diện (UI/UX)
- [ ] Ripple effect và click/tap states hoạt động chính xác (`android:background="?attr/selectableItemBackground"` hoặc `android:foreground="?attr/selectableItemBackground"`).
- [ ] Namespace `tools:` được dùng cho preview (`tools:text`, `tools:visibility`, `tools:listitem`, `tools:src`) và không thay thế dữ liệu runtime.
- [ ] Accessibility ổn: content description đúng cho ảnh/action có ý nghĩa, touch target đủ lớn, focus order hợp lý.
