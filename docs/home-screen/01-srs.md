# Home Screen SRS

## Overview

Man hinh Home Screen hien thi trang chinh cua ung dung IPTV Smart Player, cho phep nguoi dung xem trang thai playlist, loc playlist theo loai, xem cac nhom video, danh dau yeu thich, mo menu thao tac, va mo layout de them playlist.

## Screen States

- Empty state: hien thi khi chua co playlist.
- Trang thai co playlist: hien thi danh sach nguon playlist va cac section video.
- Trang thai mo menu 3 cham: hien thi popup thao tac cho the nguon hoac item video.
- Trang thai mo layout de tu nut `+`: hien thi cac lua chon them hoac mo playlist, kem overlay toi phia sau.

## Common Components

- Thanh tren cung hien thi icon vuong mien ben trai, tieu de `IPTV Smart Player` o giua, va icon tro giup `?` ben phai.
- Banner hien thi anh lon ben duoi thanh tren cung.
- Banner tu dong chuyen anh sau moi 5 giay.
- Anh banner la cac anh co san trong ung dung.
- Hang tab nhanh gom `ALL`, `URL`, `STREAM`, `FILE`, `DEVICE`.
- Bottom navigation gom `Home`, `Channel`, nut `+`, `Favorite`, `Settings`.
- `Home` la muc dang duoc chon tren bottom navigation.

## Common User Actions

| User action | System action |
|---|---|
| Cham icon tro giup `?` | Khong co hanh dong |
| Cho 5 giay tai banner | Banner tu dong chuyen sang anh tiep theo |
| Cham tab `ALL` | Hien thi tat ca playlist |
| Cham tab `URL` | Loc danh sach playlist theo loai URL |
| Cham tab `STREAM` | Loc danh sach playlist theo loai STREAM |
| Cham tab `FILE` | Loc danh sach playlist theo loai FILE |
| Cham tab `DEVICE` | Loc danh sach playlist theo loai DEVICE |
| Cham `Channel` | Khong co hanh dong |
| Cham `Favorite` | Khong co hanh dong |
| Cham `Settings` | Khong co hanh dong |

## Empty State

Empty state hien thi khi chua co playlist.

### Components

- The `Tutorial` mau xanh.
- The `Tutorial` gom tieu de `Tutorial`, mo ta `Learn how to use IPTV`, nut `Get started`, va anh minh hoa.
- Khu vuc chua co playlist hien thi icon TV, text `No playlist found`, va mo ta `Add new playlist by click + button`.
- Bottom navigation van hien thi o cuoi man hinh, voi nut `+` o giua.

### User Actions

| User action | System action |
|---|---|
| Cham nut `Get started` | Khong co hanh dong |
| Cham nut `+` | Mo layout de gom 4 button |
| Cham `Channel` | Khong co hanh dong |
| Cham `Favorite` | Khong co hanh dong |
| Cham `Settings` | Khong co hanh dong |

## State With Playlists

Trang thai co playlist hien thi khi nguoi dung da co it nhat mot playlist.

### Components

- Khu vuc the nguon playlist gom 4 the: `File`, `Device`, `URL`, `Stream`.
- Moi the nguon playlist hien thi icon, ten nguon, so luong channels, va menu 3 cham.
- Section `Recent` hien thi tieu de `Recent`, so luong trong ngoac, nut `View All`, va danh sach item video.
- Section `Recent` hien thi toi da 10 item.
- Section `Favorite` hien thi tieu de `Favorite`, so luong trong ngoac, nut `View All`, va danh sach item video.
- Section `Favorite` hien thi toi da 10 item.
- Tat ca playlist hien co duoc hien thi thanh cac section rieng.
- Moi section playlist hien thi ten playlist, so luong trong ngoac, nut `View All`, va danh sach item video.
- Moi section playlist hien thi toi da 10 item.
- Moi item video hien thi thumbnail, ten video, icon tim, va menu 3 cham.

### User Actions

| User action | System action |
|---|---|
| Cham than the `File`, `Device`, `URL`, hoac `Stream` | Khong co hanh dong |
| Cham `View All` cua bat ky section nao | Khong co hanh dong |
| Cham item video | Khong co hanh dong |
| Cham icon tim tren item video | Them hoac bo item khoi `Favorite`, dong thoi cap nhat trang thai icon tim |
| Cham menu 3 cham tren the nguon playlist | Hien thi popup gom `Edit Playlist` va `Delete Playlist` |
| Cham menu 3 cham tren item video | Hien thi popup gom `Edit Channel` va `Delete Channel` |

## Popup Menu

### Playlist Source Popup

Popup menu cua the nguon playlist hien thi khi nguoi dung cham menu 3 cham tren the `File`, `Device`, `URL`, hoac `Stream`.

Popup gom 2 lua chon:

- `Edit Playlist`
- `Delete Playlist`

### Video Item Popup

Popup menu cua item video hien thi khi nguoi dung cham menu 3 cham tren item video.

Popup gom 2 lua chon:

- `Edit Channel`
- `Delete Channel`

### Popup User Actions

| User action | System action |
|---|---|
| Cham `Edit Playlist` | Khong co hanh dong |
| Cham `Delete Playlist` | Khong co hanh dong |
| Cham `Edit Channel` | Khong co hanh dong |
| Cham `Delete Channel` | Khong co hanh dong |

## Plus Overlay Layout

Layout de tu nut `+` hien thi khi nguoi dung cham nut `+` tren bottom navigation.

### Components

- Nen phia sau toi lai khi layout de mo.
- Khi nen phia sau toi lai, nguoi dung khong thao tac duoc noi dung phia sau.
- Layout de hien thi 4 button noi phia tren nut `+`.
- 4 button gom `Play Single URL / Stream URL`, `Import Playlist URL`, `Import from Device`, `Upload M3U File`.
- Cac button slide fade nhe tu duoi len khi xuat hien.
- Cac button slide fade xuong duoi khi mat di.

### User Actions

| User action | System action |
|---|---|
| Cham nut `+` | Mo layout de gom 4 button |
| Cham vung toi ben ngoai layout de | Dong layout de |
| Cham `Play Single URL / Stream URL` | Khong co hanh dong |
| Cham `Import Playlist URL` | Khong co hanh dong |
| Cham `Import from Device` | Khong co hanh dong |
| Cham `Upload M3U File` | Khong co hanh dong |

## Acceptance Criteria

- Home Screen hien thi dung 4 trang thai: empty state, co playlist, mo menu 3 cham, va mo layout de tu nut `+`.
- Thanh tren cung hien thi icon vuong mien, tieu de `IPTV Smart Player`, va icon tro giup `?`.
- Banner hien thi anh lon va tu dong chuyen anh sau moi 5 giay.
- Hang tab nhanh hien thi du `ALL`, `URL`, `STREAM`, `FILE`, `DEVICE`.
- Cham tung tab nhanh loc danh sach playlist theo loai tuong ung; `ALL` hien thi tat ca playlist.
- Empty state hien thi the `Tutorial` va khu vuc `No playlist found` dung noi dung da mo ta.
- Khi co playlist, man hinh hien thi 4 the nguon playlist va cac section `Recent`, `Favorite`, cung tat ca section playlist hien co.
- Moi section chi hien thi toi da 10 item.
- Cham icon tim tren item video them hoac bo item khoi `Favorite` va cap nhat trang thai icon tim.
- Menu 3 cham tren the nguon playlist hien thi `Edit Playlist` va `Delete Playlist`.
- Menu 3 cham tren item video hien thi `Edit Channel` va `Delete Channel`.
- Cham cac lua chon trong popup menu khong tao hanh dong nao.
- Cham nut `+` mo layout de voi 4 button noi phia tren nut `+`.
- Khi layout de mo, nen phia sau toi lai va khong thao tac duoc noi dung phia sau.
- Cham vung toi ben ngoai layout de dong layout de.
- Cham 4 button trong layout de khong tao hanh dong nao.
- Khong co placeholder hoac trang thai mo ho.
