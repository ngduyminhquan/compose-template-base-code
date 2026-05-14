# Quy Tắc Thiết Kế ViewModel Layer

Khi tiến hành thiết kế UI Layer (cụ thể là ViewModel), yêu cầu bắt buộc là thiết kế theo kiến trúc **MVI (Model-View-Intent)**.

Cấu trúc cụ thể bao gồm:
1. **UiState**: Tất cả trạng thái hiển thị trên màn hình phải được bọc trong một `data class` đại diện cho trạng thái UI.
2. **Intent**: Các hành động (actions/events) từ người dùng gửi tới ViewModel phải được định nghĩa bằng một `sealed class` và xử lý trực tiếp thông qua một hàm nhận Intent (ví dụ: `onIntent()`) sử dụng khối `when`. Tuyệt đối KHÔNG tạo Flow (StateFlow/SharedFlow) để nhận Intent.
3. **SideEffect**: Các sự kiện chỉ xảy ra một lần (như hiển thị Toast, điều hướng, snackbar, v.v.) phải được định nghĩa bằng một `sealed class` và phát ra thông qua `SharedFlow`.

### Ví dụ Minh Họa

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. UiState: Bọc toàn bộ trạng thái trong data class
data class ExampleUiState(
    val isLoading: Boolean = false,
    val data: List<String> = emptyList(),
    val errorMessage: String? = null
)

// 2. Intent: Định nghĩa các action từ UI/người dùng bằng sealed class
sealed class ExampleIntent {
    object LoadInitialData : ExampleIntent()
    data class OnItemClicked(val itemId: String) : ExampleIntent()
    object OnRefresh : ExampleIntent()
}

// 3. SideEffect: Các sự kiện xảy ra 1 lần (Toast, Navigation...) bằng sealed class
sealed class ExampleSideEffect {
    data class ShowToast(val message: String) : ExampleSideEffect()
    data class NavigateToDetail(val itemId: String) : ExampleSideEffect()
}

// 4. ViewModel
class ExampleViewModel : ViewModel() {

    // StateFlow quản lý trạng thái UI
    private val _uiState = MutableStateFlow(ExampleUiState())
    val uiState: StateFlow<ExampleUiState> = _uiState.asStateFlow()

    // SharedFlow phát ra SideEffect
    private val _sideEffect = MutableSharedFlow<ExampleSideEffect>()
    val sideEffect: SharedFlow<ExampleSideEffect> = _sideEffect.asSharedFlow()

    // UI Layer gọi hàm này để gửi Intent vào ViewModel
    // KHÔNG dùng Flow để quản lý Intent, xử lý trực tiếp bằng when
    fun onIntent(intent: ExampleIntent) {
        when (intent) {
            is ExampleIntent.LoadInitialData -> loadData()
            is ExampleIntent.OnItemClicked -> navigateToItem(intent.itemId)
            is ExampleIntent.OnRefresh -> refreshData()
        }
    }

    private fun loadData() {
        // Cập nhật state thành loading
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        // Logic gọi API/Database ở đây...
    }

    private fun navigateToItem(itemId: String) {
        viewModelScope.launch {
            // Phát ra side effect để UI xử lý chuyển màn hình
            _sideEffect.emit(ExampleSideEffect.NavigateToDetail(itemId))
        }
    }

    private fun refreshData() {
        // Xử lý refresh...
    }
}
```
