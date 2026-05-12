# Thiết Kế Kiến Trúc (Architecture) - Màn hình Onboarding

Dựa trên yêu cầu tối giản hóa kiến trúc và tham khảo từ `LanguageScreen.kt`, tính năng Onboarding sẽ không sử dụng Data Layer và ViewModel. Toàn bộ UI và logic sẽ được xử lý trực tiếp bên trong Jetpack Compose.

## 1. Cấu trúc thư mục dự kiến

```text
app/src/main/java/com/project/composeproject/
└── ui/
    └── screen/
        └── onboarding/
            └── OnboardingScreen.kt        (Giao diện Jetpack Compose, tự quản lý state)
```

## 2. Thiết kế UI Layer

UI Layer sử dụng Jetpack Compose với cấu trúc tối giản:

*   **Quản lý dữ liệu tĩnh:** Tạo một data class `OnboardingItem` để chứa thông tin hiển thị của mỗi trang (hình ảnh, tiêu đề, nội dung).
*   **Quản lý State:** Sử dụng `rememberPagerState` để quản lý trạng thái hiển thị và vuốt của các trang Onboarding.
*   **Xử lý Logic:** 
    *   Sự kiện click nút NEXT: Gọi hàm `animateScrollToPage` của `pagerState` để chuyển sang trang tiếp theo.
    *   Sự kiện click nút START: Tạm thời chưa xử lý logic lưu trạng thái hay chuyển màn hình, chỉ để lại callback trống hoặc log.

### Phác thảo cấu trúc OnboardingScreen.kt

```kotlin
// Data class chứa thông tin mỗi trang
data class OnboardingItem(
    @DrawableRes val imageResId: Int,
    val title: String,
    val content: String
)

@Composable
fun OnboardingScreen() {
    // 1. Dữ liệu các trang
    val onboardingItems = remember { 
        listOf(
            OnboardingItem(...),
            OnboardingItem(...),
            OnboardingItem(...),
            OnboardingItem(...)
        )
    }

    // 2. Quản lý State trang hiện tại
    val pagerState = rememberPagerState(pageCount = { onboardingItems.size })
    val coroutineScope = rememberCoroutineScope()
    
    // 3. Nội dung UI
    OnboardingContent(
        pagerState = pagerState,
        items = onboardingItems,
        onNextClick = {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        },
        onStartClick = {
            // TODO: Tạm thời chưa xử lý nút START
        }
    )
}

@Composable
private fun OnboardingContent(...) {
    // Chứa HorizontalPager, Indicators và Buttons
}
```
