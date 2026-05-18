# Premium Screen UI Layer Architecture

## Scope

Tai lieu nay thiet ke UI Layer cho Premium Screen dua tren `docs/iap-screen/01-srs.md`, `repository-rule.md`, `viewmodel-mvi-rule.md`, va Shared Repository Contract da chot voi User.

Premium Screen la man hinh dau tien cua ung dung, hien thi loi moi nang cap Premium, danh sach quyen loi, danh sach goi dang ky, thong tin dung thu, nut dang ky, link policy, va hanh dong dong man hinh de vao Home Screen.

UI Layer chi duoc goi `PremiumRepository`. UI Layer khong mo ta DataSource, API, Database, DTO, Entity, mapper, hoac chi tiet implementation Repository.

## UI Architecture Tree

```text
app/src/main/java/com/project/composeproject/
├── ui/
│   ├── navigation/
│   │   ├── Route.kt                                      [Mo rong]
│   │   └── AppNavigation.kt                              [Mo rong]
│   └── screen/
│       └── premium/
│           ├── PremiumScreen.kt                          [Them moi]
│           ├── PremiumViewModel.kt                       [Them moi]
│           ├── PremiumUiState.kt                         [Them moi]
│           ├── PremiumIntent.kt                          [Them moi]
│           ├── PremiumSideEffect.kt                      [Them moi]
│           └── model/
│               ├── PremiumPlanUiModel.kt                 [Them moi]
│               └── PremiumBenefitUiModel.kt              [Them moi]
├── domain/
│   ├── model/
│   │   ├── DataResult.kt                                 [Giu nguyen]
│   │   ├── PremiumPlan.kt                                [Them moi]
│   │   ├── PremiumPlanId.kt                              [Them moi]
│   │   ├── PremiumPurchaseResult.kt                      [Them moi]
│   │   └── PremiumPolicyLinks.kt                         [Them moi]
│   └── repository/
│       └── PremiumRepository.kt                          [Them moi]
└── di/
    └── RepositoryModule.kt                               [Mo rong]
```

## Screen And Component Roles

| Component | Role |
|---|---|
| `PremiumScreen` | Compose screen render UI tu `PremiumUiState`, gui user action thanh `PremiumIntent`, collect `PremiumSideEffect` |
| Close button `X` | Gui intent dong Premium Screen va vao Home Screen |
| Banner | Hien thi anh banner lon phia tren man hinh |
| Header text | Hien thi `BE PREMIUM` va `to unlock all features` |
| Benefit list | Hien thi `Remove Ads`, `Cast to TV`, `Unlimited Playlist & Channels`, `Smooth & Stable Streaming` |
| Plan list | Hien thi `Yearly`, `Monthly`, `Weekly`, gia, selected state, badge `Best Offer` cho `Yearly` |
| Subscribe button | Gui intent bat dau mua goi dang chon |
| Policy row | Gui intent mo `Privacy Policy`, no-op `Restore`, mo `Term of Use` |

## ViewModel MVI

`PremiumViewModel` tuan thu `viewmodel-mvi-rule.md`:

| MVI item | Rule |
|---|---|
| `PremiumUiState` | Data class boc toan bo state hien thi |
| `PremiumIntent` | Sealed class dai dien user action, nhan qua `onIntent()` |
| `PremiumSideEffect` | Sealed class cho event mot lan, phat qua `SharedFlow` |
| Intent handling | `onIntent(intent)` xu ly truc tiep bang `when`, khong dung Flow de nhan Intent |

## UiState

```kotlin
data class PremiumUiState(
    val isLoading: Boolean = false,
    val selectedPlanId: PremiumPlanId = PremiumPlanId.YEARLY,
    val benefits: List<PremiumBenefitUiModel> = emptyList(),
    val plans: List<PremiumPlanUiModel> = emptyList(),
    val trialText: String = "3-day free trial. Cancel Anytime",
    val errorMessage: String? = null,
)

data class PremiumBenefitUiModel(
    val title: String,
)

data class PremiumPlanUiModel(
    val id: PremiumPlanId,
    val name: String,
    val priceText: String,
    val badgeText: String?,
    val isSelected: Boolean,
)
```

## Intent/User Action

```kotlin
sealed class PremiumIntent {
    data object LoadPremium : PremiumIntent()
    data object OnCloseClicked : PremiumIntent()
    data class OnPlanSelected(val planId: PremiumPlanId) : PremiumIntent()
    data object OnSubscribeClicked : PremiumIntent()
    data object OnRestoreClicked : PremiumIntent()
    data object OnPrivacyPolicyClicked : PremiumIntent()
    data object OnTermOfUseClicked : PremiumIntent()
}
```

## SideEffect/System Feedback

```kotlin
sealed class PremiumSideEffect {
    data object NavigateToHome : PremiumSideEffect()
    data class ShowPurchaseFailed(val message: String) : PremiumSideEffect()
    data class OpenUrl(val url: String) : PremiumSideEffect()
}
```

## Repository Interface UI Can Call

UI Layer duoc phep goi `PremiumRepository` qua `PremiumViewModel`.

```kotlin
interface PremiumRepository {
    fun observePremiumPlans(): Flow<DataResult<List<PremiumPlan>>>

    suspend fun purchaseSubscription(
        planId: PremiumPlanId,
    ): DataResult<PremiumPurchaseResult>

    suspend fun getPolicyLinks(): DataResult<PremiumPolicyLinks>
}
```

## User/System Action Mapping

| SRS User Action | SRS System Action | Intent | UiState update | SideEffect | Repository call |
|---|---|---|---|---|---|
| Mo ung dung | Hien thi `Premium Screen` la man hinh dau tien | `LoadPremium` | Default selected `YEARLY`, load plans/benefits | None | `observePremiumPlans()` |
| Cham nut `X` | Dong `Premium Screen` va vao Home Screen | `OnCloseClicked` | No change | `NavigateToHome` | None |
| Cham goi `Yearly` | Chon goi `Yearly`, bo chon goi truoc do | `OnPlanSelected(YEARLY)` | `selectedPlanId = YEARLY`, update plan selected state | None | None |
| Cham goi `Monthly` | Chon goi `Monthly`, bo chon goi truoc do | `OnPlanSelected(MONTHLY)` | `selectedPlanId = MONTHLY`, update plan selected state | None | None |
| Cham goi `Weekly` | Chon goi `Weekly`, bo chon goi truoc do | `OnPlanSelected(WEEKLY)` | `selectedPlanId = WEEKLY`, update plan selected state | None | None |
| Cham nut `Subscribe Now` | Bat dau mua goi dang duoc chon | `OnSubscribeClicked` | Set `isLoading` while request running | On error: `ShowPurchaseFailed` | `purchaseSubscription(selectedPlanId)` |
| Mua that bai | Hien thi thong bao mua that bai | Result handling from subscribe | Set `errorMessage` if needed | `ShowPurchaseFailed(message)` | Output from `purchaseSubscription()` |
| Cham `Restore` | Khong co hanh dong | `OnRestoreClicked` | No change | None | None |
| Cham `Privacy Policy` | Mo trang/chinh sach quyen rieng tu | `OnPrivacyPolicyClicked` | No change | `OpenUrl(privacyPolicyUrl)` | `getPolicyLinks()` |
| Cham `Term of Use` | Mo trang/dieu khoan su dung | `OnTermOfUseClicked` | No change | `OpenUrl(termOfUseUrl)` | `getPolicyLinks()` |

## ViewModel Contract

```kotlin
class PremiumViewModel(
    private val premiumRepository: PremiumRepository,
) : ViewModel() {

    val uiState: StateFlow<PremiumUiState>
    val sideEffect: SharedFlow<PremiumSideEffect>

    fun onIntent(intent: PremiumIntent) {
        when (intent) {
            PremiumIntent.LoadPremium -> observePremiumPlans()
            PremiumIntent.OnCloseClicked -> navigateToHome()
            is PremiumIntent.OnPlanSelected -> selectPlan(intent.planId)
            PremiumIntent.OnSubscribeClicked -> purchaseSelectedPlan()
            PremiumIntent.OnRestoreClicked -> Unit
            PremiumIntent.OnPrivacyPolicyClicked -> openPrivacyPolicy()
            PremiumIntent.OnTermOfUseClicked -> openTermOfUse()
        }
    }
}
```

## Notes

| Item | Status | Note |
|---|---|---|
| `Route.kt` | Mo rong | Them route Premium de Premium Screen la man hinh dau tien |
| `AppNavigation.kt` | Mo rong | Start route la Premium, close chuyen vao Home Screen |
| `PremiumScreen` | Them moi | Render dung component trong SRS, khong co placeholder |
| `PremiumViewModel` | Them moi | Tuan thu MVI |
| `PremiumRepository` | Them moi | Shared contract voi Data Layer |
| Existing Channel UI/Data | Giu nguyen | Khong lien quan Premium Screen |

## Self Review

| Check | Result |
|---|---|
| UI Layer tuan thu MVI | Pass |
| Intent nhan qua `onIntent()` va xu ly bang `when` | Pass |
| SideEffect dung `SharedFlow` cho navigation, toast/message, open URL | Pass |
| UI Layer chi lap lai Repository Interface/function duoc phep goi | Pass |
| UI Layer khong mo ta DataSource/API/Database/DTO/Entity/mapper | Pass |
| Mapping phu hop User Actions/System Actions trong SRS | Pass |
| `Restore` la no-op theo SRS | Pass |
| Premium Screen mac dinh chon `Yearly` | Pass |
| Shared Repository Contract khop `03-data-layer.md` | Pass |
