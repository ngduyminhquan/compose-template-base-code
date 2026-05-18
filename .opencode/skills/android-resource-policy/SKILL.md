---
name: android-resource-policy
description: Use when managing Android resources for UI work - strings, colors, dimensions, fonts, drawables, icons, localization, RTL, and resource reuse policy.
---
# android-resource-policy

Kỹ năng này quản lý resource Android dùng chung cho Compose và XML UI. Áp dụng khi tạo hoặc sửa string, color, dimension, font, drawable, icon, theme token hoặc resource liên quan localization/accessibility.

## 1. Nguyên tắc ưu tiên

1. Tuân thủ convention resource hiện có trong project trước.
2. Tái sử dụng resource/design token hiện có trước khi thêm mới.
3. Dùng theme/design token của project cho màu, typography, spacing nếu project có hệ thống này.
4. Không hardcode giá trị tái sử dụng hoặc giá trị ảnh hưởng design system.
5. Chỉ giữ local constant/value khi giá trị one-off, rõ nghĩa, không lặp lại và extract ra resource làm code nhiễu hơn.

## 2. Resource Management

| Rule | Do                                                                                                                                                                                        | Don't                                                                                     |
|------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| **Strings** | Text tĩnh dùng `res/values/strings.xml` và `stringResource()`/`@string`. Text có số lượng dùng plurals. Text format dùng placeholder.                                                     | Hardcode text hiển thị với user hoặc ghép chuỗi thủ công gây lỗi dịch thuật.              |
| **Dynamic Text** | Text từ API, database, user input hoặc UI state truyền trực tiếp qua state/model.                                                                                                         | Đưa text động vào `strings.xml`.                                                          |
| **Colors** | Dùng theme/design token hiện có như `MaterialTheme.colorScheme`, `Color.kt`, XML color resource hoặc token tương đương theo convention project.                                           | Hardcode hex rải rác trong UI code hoặc né theme làm mất Light/Dark mode.                 |
| **Dimensions** | Dùng spacing/dimen token hiện có cho giá trị lặp lại hoặc thuộc design system. One-off `dp/sp` có thể giữ local nếu rõ nghĩa.                                                             | Extract mọi `dp/sp` máy móc hoặc để magic number lặp lại nhiều nơi.                       |
| **Fonts & Typography** | Dùng typography system hoặc font trong `res/font`. Kiểm tra font scale và line height.                                                                                                    | Khởi tạo font trực tiếp bừa bãi hoặc cố định text size làm hỏng accessibility.            |
| **Icons & Drawables** | Kiểm tra `res/drawable`, asset/design system hiện có trước. Nếu không có, dùng tạm 1 cái khi không có asset tương ứng, TUYỆT ĐỐI KHÔNG DÙNG `Icons.Default...`, Material/Android default. | DÙNG `Icons.Default...`. Dùng `@android:drawable/...` mà không kiểm tra resource project. |
| **Localization & RTL** | Dùng `start/end` thay vì `left/right` khi phù hợp. Dùng plural, placeholder và string resource để hỗ trợ dịch.                                                                            | Ghép chuỗi theo thứ tự cố định hoặc layout khóa hướng trái/phải không cần thiết.          |

## 3. Kiểm tra thư mục trước khi thêm resource

- `res/values/`: strings, colors, dimens, themes, styles, plurals.
- `res/drawable/`: icon, vector, bitmap, shape drawable.
- `res/font/`: font file và font family.
- `res/mipmap/`: launcher icon hoặc asset launcher-specific.
- Theme/token Kotlin hiện có: ví dụ `Color.kt`, `Theme.kt`, `Type.kt`, `Dimens.kt`, `Spacing.kt` hoặc package design system tương đương.

## 4. Checklist

- [ ] Đã tìm resource tương ứng trong project.
- [ ] Text tĩnh hiển thị với user dùng string resource.
- [ ] Text động từ state/API/user input không bị đưa sai vào `strings.xml`.
- [ ] String có số lượng dùng plural khi cần.
- [ ] String format dùng placeholder, không ghép chuỗi thủ công.
- [ ] Màu dùng theme/design token/resource theo convention project và hỗ trợ Light/Dark mode.
- [ ] Icon/drawable đã kiểm tra trong `res/drawable` hoặc design system trước khi dùng default icon.
- [ ] Font dùng typography system hoặc `res/font`, không tạo font bừa bãi tại UI.
- [ ] Layout dùng `start/end` khi cần hỗ trợ RTL.
- [ ] UI chịu được text dài và font scale lớn.
