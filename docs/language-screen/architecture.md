# Language Screen Architecture

## Current Project Context

- UI hiện có package `ui/screen/language`.
- Language data hiện có trong `LanguageUtils`.
- Ngôn ngữ hiện tại được lưu bằng `SharedPreferences`.
- Project có Hilt, nhưng màn Language hiện chưa có ViewModel hoặc Repository riêng.

## Selected Design

Giữ cơ chế lưu ngôn ngữ hiện có qua `LanguageUtils` và `SharedPreferences`.

Vì phạm vi yêu cầu chỉ dựng UI và lưu lựa chọn bằng logic đã có, chưa tạo thêm Data Layer mới để tránh thừa code.

## Folder Structure

```text
app/src/main/java/com/project/composeproject/
├── ui/
│   └── screen/
│       └── language/
│           └── LanguageScreen.kt
└── utils/
    └── LanguageUtils.kt
```

## UI Layer

### Screen

`LanguageScreen` chịu trách nhiệm:

- Hiển thị top bar `Language` và action `DONE`.
- Hiển thị danh sách ngôn ngữ từ `LanguageUtils.displayLanguages`.
- Giữ trạng thái ngôn ngữ đang chọn trên màn hình.
- Cập nhật trạng thái chọn khi người dùng chạm vào item.
- Lưu ngôn ngữ đang chọn khi người dùng chạm `DONE`.

### MVI Mapping

Do không tạo ViewModel riêng cho phạm vi hiện tại, MVI được ánh xạ ở mức thiết kế như sau nếu cần mở rộng sau này:

```kotlin
data class LanguageUiState(
    val languages: List<LanguageItem>,
    val selectedLanguageCode: String
)

sealed class LanguageIntent {
    data class SelectLanguage(val languageCode: String) : LanguageIntent()
    data object ConfirmLanguage : LanguageIntent()
}

sealed class LanguageSideEffect
```

## Data Layer

### Data Source

Data source hiện dùng `SharedPreferences` thông qua `LanguageUtils`.

### Repository Interface

Không tạo Repository mới trong bước triển khai hiện tại vì người dùng chọn giữ `LanguageUtils` hiện có.

Nếu cần tách Data Layer sau này, interface tối thiểu sẽ là:

```kotlin
interface LanguageRepository {
    fun getDisplayLanguages(): DataResult<List<LanguageItem>>
    fun getCurrentLanguage(): DataResult<LanguageItem>
    suspend fun setCurrentLanguage(languageCode: String): DataResult<Unit>
}
```

## Implementation Decision

- Không thêm ViewModel mới.
- Không thêm Repository mới.
- Không thêm DataSource mới.
- Chỉ cập nhật `LanguageScreen.kt` để dựng UI theo SRS và gọi logic ngôn ngữ hiện có.

## Review Checklist

- Architecture bám SRS.
- Không thêm tính năng ngoài yêu cầu.
- Data source khớp lựa chọn người dùng.
- Interface tham khảo mapping đúng system actions nếu cần tách layer sau này.
