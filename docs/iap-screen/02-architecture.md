# Premium Screen Architecture

## Source Documents

- SRS: `docs/premium-screen/01-srs.md`
- Repository rule: `.opencode/skills/architecture-designer/rules/repository-rule.md`
- ViewModel MVI rule: `.opencode/skills/architecture-designer/rules/viewmodel-mvi-rule.md`

## Architecture Tree

```text
app/src/main/java/com/project/composeproject/
├── data/
│   ├── repository/
│   │   ├── ChannelRepositoryImpl.kt              [giữ nguyên]
│   │   └── PremiumRepositoryImpl.kt              [thêm mới]
│   └── source/
│       ├── database/                             [giữ nguyên]
│       ├── network/                              [giữ nguyên]
│       ├── storage/                              [giữ nguyên]
│       └── premium/
│           ├── PremiumPurchaseSource.kt          [thêm mới]
│           └── PremiumLinkSource.kt              [thêm mới]
├── di/
│   ├── DatabaseModule.kt                         [giữ nguyên]
│   └── RepositoryModule.kt                       [mở rộng bind PremiumRepository]
├── domain/
│   ├── model/
│   │   ├── DataResult.kt                         [giữ nguyên]
│   │   ├── PremiumPlan.kt                        [thêm mới]
│   │   ├── PremiumPlanType.kt                    [thêm mới]
│   │   └── PremiumLinks.kt                       [thêm mới]
│   └── repository/
│       ├── ChannelRepository.kt                  [giữ nguyên]
│       └── PremiumRepository.kt                  [thêm mới]
└── ui/
    ├── navigation/
    │   ├── Route.kt                              [mở rộng Route.Premium]
    │   └── AppNavigation.kt                      [mở rộng start destination + entry]
    └── screen/
        ├── home/                                 [giữ nguyên]
        └── premium/
            ├── PremiumScreen.kt                  [thêm mới]
            ├── PremiumViewModel.kt               [thêm mới]
            ├── PremiumUiState.kt                 [thêm mới]
            ├── PremiumIntent.kt                  [thêm mới]
            └── PremiumSideEffect.kt              [thêm mới]
```

## Existing Data Layer

- `domain/model/DataResult.kt` đã có đủ `Success`, `Error`, và `Loading`, tiếp tục dùng cho Premium repository.
- `domain/repository/ChannelRepository.kt` giữ nguyên, không phục vụ Premium Screen.
- `data/repository/ChannelRepositoryImpl.kt` giữ nguyên, không phục vụ Premium Screen.
- `data/source/database`, `data/source/network`, và `data/source/storage` giữ nguyên.
- `di/RepositoryModule.kt` đang bind `ChannelRepository`, cần mở rộng để bind thêm `PremiumRepository`.

## UI Layer

Premium UI Layer tuân thủ MVI:

- `PremiumScreen`: hiển thị Premium Screen theo SRS và gửi action của người dùng thành `PremiumIntent`.
- `PremiumViewModel`: nhận `PremiumIntent`, cập nhật `PremiumUiState`, gọi `PremiumRepository`, và phát `PremiumSideEffect` cho event một lần.
- `PremiumUiState`: chứa toàn bộ trạng thái hiển thị của Premium Screen.
- `PremiumIntent`: chứa các action từ người dùng.
- `PremiumSideEffect`: chứa các event một lần như điều hướng Home, mở link, và thông báo mua thất bại.

```kotlin
data class PremiumUiState(
    val selectedPlanType: PremiumPlanType = PremiumPlanType.Yearly,
    val plans: List<PremiumPlan> = emptyList(),
    val links: PremiumLinks? = null,
    val isPurchasing: Boolean = false,
    val purchaseErrorMessage: String? = null,
)

sealed interface PremiumIntent {
    data object OnCloseClicked : PremiumIntent
    data class OnPlanClicked(val planType: PremiumPlanType) : PremiumIntent
    data object OnSubscribeClicked : PremiumIntent
    data object OnRestoreClicked : PremiumIntent
    data object OnPrivacyPolicyClicked : PremiumIntent
    data object OnTermOfUseClicked : PremiumIntent
}

sealed interface PremiumSideEffect {
    data object NavigateToHome : PremiumSideEffect
    data class OpenUrl(val url: String) : PremiumSideEffect
    data class ShowPurchaseFailed(val message: String) : PremiumSideEffect
}
```

## Data Layer

Premium Data Layer dùng Repository Pattern:

- `PremiumRepository`: interface UI Layer gọi, mapping trực tiếp với System Actions trong SRS.
- `PremiumRepositoryImpl`: implementation của `PremiumRepository`, nhận `PremiumPurchaseSource` và `PremiumLinkSource` qua constructor.
- `PremiumPurchaseSource`: nguồn mua gói. Nguồn cụ thể quyết định sau theo lựa chọn đã duyệt.
- `PremiumLinkSource`: nguồn cung cấp link `Privacy Policy` và `Term of Use`. URL cụ thể quyết định sau theo lựa chọn đã duyệt.
- `PremiumPlan`, `PremiumPlanType`, và `PremiumLinks`: model cho Premium Screen.

```kotlin
interface PremiumRepository {
    fun observePremiumPlans(): Flow<DataResult<List<PremiumPlan>>>

    suspend fun purchasePlan(planType: PremiumPlanType): DataResult<Unit>

    suspend fun getPremiumLinks(): DataResult<PremiumLinks>
}
```

```kotlin
interface PremiumPurchaseSource {
    suspend fun purchase(planType: PremiumPlanType): DataResult<Unit>
}
```

```kotlin
interface PremiumLinkSource {
    suspend fun getLinks(): DataResult<PremiumLinks>
}
```

```kotlin
data class PremiumPlan(
    val type: PremiumPlanType,
    val title: String,
    val price: String,
    val isBestOffer: Boolean,
)

enum class PremiumPlanType {
    Yearly,
    Monthly,
    Weekly,
}

data class PremiumLinks(
    val privacyPolicyUrl: String,
    val termOfUseUrl: String,
)
```

## ViewModel Flow

```text
PremiumScreen -> PremiumIntent -> PremiumViewModel -> PremiumRepository -> PremiumPurchaseSource/PremiumLinkSource
PremiumViewModel -> PremiumUiState -> PremiumScreen
PremiumViewModel -> PremiumSideEffect -> PremiumScreen one-shot handling
```

## SRS Mapping

| SRS System Action | Architecture Mapping |
|---|---|
| Hien thi `Premium Screen` la man hinh dau tien | `Route.Premium` là start destination trong `AppNavigation` |
| Dong `Premium Screen` va vao Home Screen | `PremiumIntent.OnCloseClicked` -> `PremiumSideEffect.NavigateToHome` |
| Chon goi `Yearly`, `Monthly`, hoac `Weekly` | `PremiumIntent.OnPlanClicked` -> cập nhật `PremiumUiState.selectedPlanType` |
| Bat dau mua goi dang duoc chon | `PremiumIntent.OnSubscribeClicked` -> `PremiumRepository.purchasePlan(selectedPlanType)` |
| Hien thi thong bao mua that bai | `DataResult.Error` từ `purchasePlan` -> `PremiumSideEffect.ShowPurchaseFailed` |
| Cham `Restore` khong co hanh dong | `PremiumIntent.OnRestoreClicked` được xử lý no-op trong `PremiumViewModel` |
| Mo trang/chinh sach quyen rieng tu | `PremiumIntent.OnPrivacyPolicyClicked` -> `PremiumRepository.getPremiumLinks()` -> `PremiumSideEffect.OpenUrl(privacyPolicyUrl)` |
| Mo trang/dieu khoan su dung | `PremiumIntent.OnTermOfUseClicked` -> `PremiumRepository.getPremiumLinks()` -> `PremiumSideEffect.OpenUrl(termOfUseUrl)` |

## Dependency Injection

- Tiếp tục dùng Hilt như project hiện có.
- Mở rộng `RepositoryModule` để bind `PremiumRepositoryImpl` vào `PremiumRepository`.
- `PremiumRepositoryImpl` nhận `PremiumPurchaseSource` và `PremiumLinkSource` qua constructor.
- Không khởi tạo cứng repository hoặc source trong `PremiumViewModel`.

## Change Notes

### Giữ Nguyên

- `ChannelRepository`
- `ChannelRepositoryImpl`
- Database, network, storage hiện có
- `DataResult`
- Home Screen architecture hiện có

### Mở Rộng

- `RepositoryModule`: bind thêm `PremiumRepository`.
- `Route`: thêm `Route.Premium`.
- `AppNavigation`: đặt Premium Screen làm màn hình đầu tiên và thêm entry Premium.

### Thêm Mới

- Premium UI package.
- Premium domain models.
- Premium repository interface và implementation.
- Premium purchase source interface.
- Premium link source interface.

## Self Check

- `PremiumRepository` phục vụ đủ System Actions trong SRS: danh sách gói, mua gói, lấy link policy/term.
- `Restore` không thêm repository function vì SRS xác nhận không có hành động.
- `Close` và chọn gói được xử lý trong UI Layer vì không cần Data Layer.
- `PremiumRepository` trả về `DataResult` hoặc `Flow<DataResult<T>>` theo repository rule.
- `PremiumViewModel` dùng `UiState`, `Intent`, và `SideEffect` theo MVI rule.
- Architecture tree hiển thị Data Layer có sẵn và phần thêm mới.
