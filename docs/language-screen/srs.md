# Language Screen SRS

## Overview

Màn Language cho phép người dùng chọn ngôn ngữ hiển thị của ứng dụng và xác nhận lựa chọn.

## Screen Components

- Tiêu đề `Language` ở phía trên bên trái.
- Action `DONE` ở phía trên bên phải, có biểu tượng dấu check phía trước.
- Danh sách ngôn ngữ gồm `English`, `German`, `Arabic`, `Hindi`, `Spanish`, `Mandarin`, `French`.
- Mỗi ngôn ngữ hiển thị trong một thẻ bo tròn.
- Mỗi thẻ có radio button thể hiện trạng thái chọn.
- Ngôn ngữ đang chọn được nhấn mạnh bằng viền đen và radio được chọn.
- Ngôn ngữ chưa chọn có nền trắng, không viền, radio chưa chọn.

## User Actions

- Người dùng chạm vào một ngôn ngữ trong danh sách.
- Người dùng chạm vào action `DONE`.

## System Responses

- Khi người dùng chạm vào một ngôn ngữ, màn hình cập nhật trạng thái chọn sang ngôn ngữ đó.
- Chỉ một ngôn ngữ được chọn tại một thời điểm.
- Khi người dùng chạm vào `DONE`, hệ thống lưu ngôn ngữ đang chọn.
- Sau khi lưu, người dùng vẫn ở lại màn Language.

## Layout Requirements

- Nền màn hình màu xám rất nhạt.
- Nội dung bắt đầu sát vùng an toàn phía trên.
- Danh sách có khoảng cách đều giữa các thẻ.
- Thẻ ngôn ngữ có chiều rộng gần đầy màn hình với khoảng cách ngang nhỏ.
- Giao diện bám theo ảnh thiết kế trên màn hình dọc.

## Acceptance Criteria

- Màn hình hiển thị đủ tiêu đề, action `DONE`, và 7 ngôn ngữ.
- Trạng thái chọn ban đầu hiển thị rõ bằng radio và viền item.
- Chạm vào ngôn ngữ khác làm radio và viền chuyển đúng sang item mới.
- Chạm `DONE` lưu lựa chọn và không rời khỏi màn hình.
- Không có nội dung trống, placeholder, hoặc trạng thái mơ hồ.
