# Premium Screen Data Layer Architecture

## Scope

Tai lieu nay thiet ke Data Layer cho Premium Screen dua tren `docs/iap-screen/01-srs.md`, `repository-rule.md`, `viewmodel-mvi-rule.md`, va Shared Repository Contract da chot voi User.

Data Layer phuc vu 3 nhu cau trong SRS: cung cap danh sach goi Premium hien thi tren man hinh, bat dau mua goi dang duoc chon bang mock/local source, va cung cap mock/local link cho `Privacy Policy` va `Term of Use`.

Nguon data da duoc User xac nhan:

| Data need | Source |
|---|---|
| Premium plans | Mock/local only |
| Subscribe Now purchase handling | Mock/local only |
| Privacy Policy / Term of Use links | Mock local URLs |

Data Layer khong mo ta chi tiet ViewModel, UiState, Intent, SideEffect ngoai ten Repository Interface UI goi.

## Data Architecture Tree

```text
app/src/main/java/com/project/composeproject/
├── domain/
│   ├── model/
│   │   ├── Channel.kt                                  [Giu nguyen]
│   │   ├── ChannelGroup.kt                             [Giu nguyen]
│   │   ├── ChannelSource.kt                            [Giu nguyen]
│   │   ├── SourceType.kt                               [Giu nguyen]
│   │   ├── DataResult.kt                               [Giu nguyen]
│   │   ├── PremiumPlan.kt                              [Them moi]
│   │   ├── PremiumPlanId.kt                            [Them moi]
│   │   ├── PremiumPurchaseResult.kt                    [Them moi]
│   │   └── PremiumPolicyLinks.kt                       [Them moi]
│   └── repository/
│       ├── ChannelRepository.kt                        [Giu nguyen]
│       └── PremiumRepository.kt                        [Them moi]
├── data/
│   ├── repository/
│   │   ├── ChannelRepositoryImpl.kt                    [Giu nguyen]
│   │   └── PremiumRepositoryImpl.kt                    [Them moi]
│   ├── mapper/
│   │   └── ChannelMapper.kt                            [Giu nguyen]
│   └── source/
│       ├── database/                                   [Giu nguyen]
│       │   ├── AppDatabase.kt                          [Giu nguyen]
│       │   ├── dao/
│       │   │   ├── ChannelDao.kt                       [Giu nguyen]
│       │   │   ├── ChannelGroupDao.kt                  [Giu nguyen]
│       │   │   └── ChannelSourceDao.kt                 [Giu nguyen]
│       │   └── entity/
│       │       ├── ChannelEntity.kt                    [Giu nguyen]
│       │       ├── ChannelGroupEntity.kt               [Giu nguyen]
│       │       └── ChannelSourceEntity.kt              [Giu nguyen]
│       ├── network/
│       │   └── M3uPlaylistNetworkSource.kt             [Giu nguyen]
│       ├── storage/
│       │   └── M3uPlaylistStorageSource.kt             [Giu nguyen]
│       └── local/
│           └── PremiumLocalDataSource.kt               [Them moi]
└── di/
    ├── DatabaseModule.kt                               [Giu nguyen]
    └── RepositoryModule.kt                             [Mo rong]
```

## Existing Data Layer

| Existing item | Status | Role |
|---|---|---|
| `DataResult` | Giu nguyen | Result wrapper co `Success`, `Error`, `Loading`, dung lai cho Premium Repository |
| `ChannelRepository` | Giu nguyen | Repository hien co cho Channel/IPTV, khong dung cho Premium Screen |
| `ChannelRepositoryImpl` | Giu nguyen | Implementation hien co cho Channel/IPTV, khong dung cho Premium Screen |
| Room database/DAO/entity | Giu nguyen | Data layer hien co cho Channel/IPTV, khong dung cho Premium Screen |
| `M3uPlaylistNetworkSource` | Giu nguyen | Source import M3U URL, khong dung cho Premium Screen |
| `M3uPlaylistStorageSource` | Giu nguyen | Source import M3U file, khong dung cho Premium Screen |
| `RepositoryModule` | Mo rong | Bind them `PremiumRepositoryImpl` cho `PremiumRepository` |

## DataSource

`PremiumLocalDataSource` la mock/local source da duoc User chot. DataSource nay chi phuc vu data/hang dong co trong SRS.

```kotlin
class PremiumLocalDataSource {
    fun observePremiumPlans(): Flow<List<PremiumPlan>>

    suspend fun purchaseSubscription(
        planId: PremiumPlanId,
    ): PremiumPurchaseResult

    suspend fun getPolicyLinks(): PremiumPolicyLinks
}
```

| Function | Data Provided/Action | SRS mapping |
|---|---|---|
| `observePremiumPlans()` | 3 plan `Yearly`, `Monthly`, `Weekly`, gia, badge, trial text | Hien thi danh sach goi dang ky |
| `purchaseSubscription(planId)` | Mock/local purchase action cho plan dang chon | Bat dau mua goi dang duoc chon, co the tra loi that bai |
| `getPolicyLinks()` | Mock/local URLs cho privacy va term | Mo trang/chinh sach quyen rieng tu, mo trang/dieu khoan su dung |

## Repository Implementation

```kotlin
class PremiumRepositoryImpl(
    private val premiumLocalDataSource: PremiumLocalDataSource,
) : PremiumRepository {
    override fun observePremiumPlans(): Flow<DataResult<List<PremiumPlan>>>

    override suspend fun purchaseSubscription(
        planId: PremiumPlanId,
    ): DataResult<PremiumPurchaseResult>

    override suspend fun getPolicyLinks(): DataResult<PremiumPolicyLinks>
}
```

Implementation phai tuan thu `repository-rule.md`:

| Rule | Application |
|---|---|
| Result wrapper | Moi output tu Repository boc bang `DataResult<T>` |
| Realtime/list data | `observePremiumPlans()` tra `Flow<DataResult<List<PremiumPlan>>>` |
| One-shot action | `purchaseSubscription()` va `getPolicyLinks()` tra `DataResult<T>` qua `suspend fun` |
| DI | Implementation inject dependency qua constructor, bind trong Hilt `RepositoryModule` hien co |

## Model

```kotlin
enum class PremiumPlanId {
    YEARLY,
    MONTHLY,
    WEEKLY,
}

data class PremiumPlan(
    val id: PremiumPlanId,
    val name: String,
    val priceText: String,
    val badgeText: String?,
    val trialText: String,
)

data class PremiumPurchaseResult(
    val planId: PremiumPlanId,
)

data class PremiumPolicyLinks(
    val privacyPolicyUrl: String,
    val termOfUseUrl: String,
)
```

Model value scope theo SRS:

| Model | Values |
|---|---|
| `PremiumPlanId.YEARLY` | `Yearly`, `$13.99`, badge `Best Offer`, selected default in UI |
| `PremiumPlanId.MONTHLY` | `Monthly`, `$4.99`, no badge |
| `PremiumPlanId.WEEKLY` | `Weekly`, `$2.99`, no badge |
| `trialText` | `3-day free trial. Cancel Anytime` |
| `PremiumPolicyLinks` | Mock local URLs da duoc User chot |

## Shared Repository Contract

Shared Repository Contract la source of truth chung giua `02-ui-layer.md` va `03-data-layer.md`.

```kotlin
interface PremiumRepository {
    fun observePremiumPlans(): Flow<DataResult<List<PremiumPlan>>>

    suspend fun purchaseSubscription(
        planId: PremiumPlanId,
    ): DataResult<PremiumPurchaseResult>

    suspend fun getPolicyLinks(): DataResult<PremiumPolicyLinks>
}

enum class PremiumPlanId {
    YEARLY,
    MONTHLY,
    WEEKLY,
}

data class PremiumPlan(
    val id: PremiumPlanId,
    val name: String,
    val priceText: String,
    val badgeText: String?,
    val trialText: String,
)

data class PremiumPurchaseResult(
    val planId: PremiumPlanId,
)

data class PremiumPolicyLinks(
    val privacyPolicyUrl: String,
    val termOfUseUrl: String,
)
```

## System Action Mapping

| SRS System Action | Repository function | DataSource | Model |
|---|---|---|---|
| Hien thi `Premium Screen` la man hinh dau tien | `observePremiumPlans()` | `PremiumLocalDataSource.observePremiumPlans()` | `PremiumPlan`, `PremiumPlanId`, `DataResult<List<PremiumPlan>>` |
| Chon goi `Yearly`, `Monthly`, `Weekly` | None | None | UI state only, uses `PremiumPlanId` |
| Bat dau mua goi dang duoc chon | `purchaseSubscription(planId)` | `PremiumLocalDataSource.purchaseSubscription(planId)` | `PremiumPurchaseResult`, `DataResult<PremiumPurchaseResult>` |
| Hien thi thong bao mua that bai | Output error from `purchaseSubscription(planId)` | Mock/local purchase failure result | `DataResult.Error` |
| Dong `Premium Screen` va vao Home Screen | None | None | UI/navigation only |
| `Restore` khong co hanh dong | None | None | None |
| Mo trang/chinh sach quyen rieng tu | `getPolicyLinks()` | `PremiumLocalDataSource.getPolicyLinks()` | `PremiumPolicyLinks`, `DataResult<PremiumPolicyLinks>` |
| Mo trang/dieu khoan su dung | `getPolicyLinks()` | `PremiumLocalDataSource.getPolicyLinks()` | `PremiumPolicyLinks`, `DataResult<PremiumPolicyLinks>` |

## Notes

| Item | Status | Note |
|---|---|---|
| `PremiumRepository` | Them moi | Repository rieng cho Premium Screen, khong tron voi ChannelRepository |
| `PremiumLocalDataSource` | Them moi | Mock/local source theo User confirmation |
| `DataResult` | Giu nguyen | Dung wrapper hien co trong `domain/model` |
| `RepositoryModule` | Mo rong | Bind `PremiumRepositoryImpl` bang Hilt hien co |
| Billing/IAP SDK | Khong them | User chot mock/local only |
| Network/API/Database cho Premium | Khong them | SRS va User confirmation khong yeu cau |
| Restore repository function | Khong them | SRS ghi `Khong co hanh dong` |

## Self Review

| Check | Result |
|---|---|
| Repository Interface phuc vu du System Actions trong SRS | Pass |
| Khong them Interface/DataSource/nguon data ngoai SRS | Pass |
| Nguon data da duoc User xac nhan mock/local only | Pass |
| Data Layer tuan thu Repository Pattern | Pass |
| Return type dung `DataResult<T>` hoac `Flow<DataResult<T>>` | Pass |
| DI theo Hilt hien co, inject qua constructor | Pass |
| Data Layer co hien thi Data Layer co san | Pass |
| Data Layer khong mo ta chi tiet ViewModel/UiState/Intent/SideEffect | Pass |
| Shared Repository Contract khop `02-ui-layer.md` | Pass |
| `Restore` khong tao function vi SRS ghi no-op | Pass |
