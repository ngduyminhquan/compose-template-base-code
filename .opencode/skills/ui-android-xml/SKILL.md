---
name: ui-android-xml
description: Kỹ năng chuyên biệt để xây dựng giao diện bằng Android XML Layout, quản lý resources và tuân thủ Android best practices.
---
# android-xml-ui

Kỹ năng này chuyên dùng cho việc xây dựng (implement) giao diện người dùng (UI) bằng XML Layout trong Android.
Nó kế thừa các nguyên tắc thiết kế UI/UX từ `ui-ux-pro-max` nhưng tập trung vào cách tổ chức code XML, quản lý resources và best practices đặc thù của hệ thống View truyền thống trên Android.

## 1. Luồng làm việc (Workflow)

Khi nhận yêu cầu xây dựng UI cho Android bằng XML:
1. **Phân tích Design**: Xem xét Design System, layout, màu sắc, typography.
2. **Cấu trúc Layout**: Phân rã giao diện, lựa chọn ViewGroup phù hợp (`ConstraintLayout`, `LinearLayout`, `FrameLayout`...). Ưu tiên tạo các view phẳng (flat hierarchy). Sử dụng `<include>` để tái sử dụng layout.
3. **Quản lý Resources**: Kiểm tra và tái sử dụng resources hiện có (màu, chuỗi, icon, styles, themes). Rút trích các giá trị mới vào file `res/values/*`.
4. **Implement UI**: Viết code XML cho layout, styles, drawable (shape, selector).
5. **Tích hợp logic**: Đảm bảo các thành phần (View) cần tương tác có ID hợp lý, rõ ràng, phục vụ tốt cho việc binding (ViewBinding / DataBinding).

## 2. Resource Management (Quy tắc KHÔNG Hardcode)

Đây là quy tắc tối quan trọng khi làm việc với Android UI:

| Rule | Do                                                                                                                                                   | Don't |
|------|------------------------------------------------------------------------------------------------------------------------------------------------------|----- |
| **Không hardcode giá trị** | Rút trích strings (`@string/...`), colors (`@color/...`), dimensions (`@dimen/...`), fonts (`@font/...`) ra các file XML tương ứng trong thư mục `res/values/` và `res/font/`.           | Viết cứng string (`android:text="Hello"`), màu sắc (`android:textColor="#FF0000"`), hoặc kích thước (`android:layout_width="100dp"`) trực tiếp trong XML. |
| **Tái sử dụng Assets & Resource** | **HẠN CHẾ TỐI ĐA VIỆC SỬ DỤNG ICON/STRING/COLOR/DIMEN CÓ SẴN CỦA ANDROID. DUYỆT QUA CÁC THƯ MỤC RES TRONG PROJECT ĐỂ TÌM TÀI NGUYÊN PHÙ HỢP TRƯỚC. NẾU CÓ THÌ DÙNG, KHÔNG CÓ THÌ MỚI ĐƯỢC DÙNG CỦA ANDROID.** | Tạo duplicate resources hoặc dùng thẳng tài nguyên của Android (`@android:color/...`, v.v.) thay vì tìm kiếm trong project. |
| **Sử dụng Styles & Themes** | Gom nhóm các thuộc tính chung (font, màu chữ, background) thành Style (`<style>`) và áp dụng cho View (`style="@style/..."`).                               | Lặp lại cùng một tập hợp thuộc tính cho nhiều View giống nhau trong layout. |
| **Sử dụng Drawable/Icons** | **BẮT BUỘC** tìm kiếm icon/drawable phù hợp trong thư mục `res/drawable` của project trước. Chỉ khi THẬT SỰ KHÔNG CÓ mới dùng icon của Android. Sử dụng Vector Drawable thay vì ảnh bitmap. | Dùng thẳng icon của Android mà không kiểm tra, hoặc thêm resource ảnh bitmap mới khi đã có Vector Drawable. |

## 3. Android XML Best Practices

| Rule | Do | Don't |
|------|----|----- |
| **Flat Hierarchy** | Sử dụng `ConstraintLayout` để xây dựng các layout phức tạp, giữ cho cây View phẳng (ít lồng ghép) để tối ưu performance. | Lồng ghép quá nhiều `LinearLayout` hoặc `RelativeLayout` (Deeply nested layouts). |
| **Tái sử dụng Layout** | Sử dụng thẻ `<include layout="@layout/..."/>` để nhúng các phần layout được dùng chung ở nhiều nơi. | Copy-paste cùng một đoạn XML sang nhiều file layout khác nhau. |
| **ViewStub** | Sử dụng `ViewStub` cho các phần giao diện hiếm khi hiển thị (như thông báo lỗi, empty state) để giảm chi phí inflate ban đầu. | Inflate sẵn và dùng `android:visibility="gone"` cho các view phức tạp và ít dùng. |
| **Hiệu năng list** | Thiết kế item layout cho `RecyclerView` càng đơn giản càng tốt. Hạn chế dùng `ConstraintLayout` trong item nếu chỉ là list đơn giản (dùng `LinearLayout` sẽ nhanh hơn). | Để item layout quá phức tạp và lồng ghép sâu làm giảm frame rate khi cuộn. |
| **Click & States** | Sử dụng `StateListDrawable` (selector) cho các trạng thái `pressed`, `focused`, `disabled` để tạo phản hồi trực quan. Dùng `?attr/selectableItemBackground` cho ripple effect. | Bỏ qua việc thêm feedback khi người dùng tương tác (nhấn, chạm) vào các phần tử có thể click. |

## 4. Pre-Delivery Checklist (Dành riêng cho XML)

Trước khi hoàn thành code XML Layout, hãy kiểm tra:

### Resources & Assets
- [ ] KHÔNG CÓ bất kỳ chuỗi text nào bị hardcode. Mọi text đều dùng `@string/...` (hoặc `tools:text` cho mục đích preview).
- [ ] KHÔNG CÓ mã màu (hex) nào bị hardcode. Mọi màu đều dùng `@color/...`.
- [ ] Kích thước (dp, sp) đã được tham chiếu từ `@dimen/...` (hoặc định nghĩa trong style chung).
- [ ] Font chữ đã được tham chiếu từ `@font/...` (ưu tiên định nghĩa trong `<style>` chung).
- [ ] Đã ưu tiên sử dụng resource của project (icon, color, dimen) thay vì dùng resource mặc định của Android (`@android:drawable/...`, `@android:color/...`).
- [ ] Đã kiểm tra thư mục `res/drawable`, `res/values/` và `res/font/` để tái sử dụng assets/styles/fonts đã có.

### Kiến trúc Layout & Hiệu năng
- [ ] Layout không lồng nhau quá 3-4 lớp. Đã ưu tiên sử dụng `ConstraintLayout` khi cần thiết để làm phẳng layout.
- [ ] Các View cần tương tác từ code đã được đặt `android:id` rõ ràng, theo quy ước đặt tên (VD: `tv_title`, `btn_submit`).
- [ ] Đã trích xuất các phần layout chung ra các file riêng và dùng `<include>`.
- [ ] Sử dụng thuộc tính namespace `tools:` (`tools:text`, `tools:visibility`, `tools:listitem`) để hiển thị preview trong Android Studio mà không ảnh hưởng code thực tế.

### Giao diện (UI/UX)
- [ ] Ripple effect và Hover/Click states hoạt động chính xác (`android:background="?attr/selectableItemBackground"` hoặc `android:foreground="?attr/selectableItemBackground"`).
- [ ] Giao diện hỗ trợ tốt cả Dark Mode và Light Mode (sử dụng tài nguyên màu từ theme hoặc thư mục `values-night`).
- [ ] Hỗ trợ khả năng truy cập (Accessibility): Sử dụng `android:contentDescription` cho mọi `ImageView` và `ImageButton`.
