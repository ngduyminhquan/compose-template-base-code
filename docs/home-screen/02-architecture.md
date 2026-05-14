# Home Screen Architecture

## Scope

Tai lieu nay thiet ke kien truc Home Screen dua tren `docs/home-screen/01-srs.md`, `repository-rule.md`, va `viewmodel-mvi-rule.md`.

Home Screen phu trach hien thi trang chinh IPTV Smart Player, gom empty state, state co playlist, popup menu, add overlay, filter playlist theo loai, recent, favorite, playlist sections, va toggle favorite channel.

## SRS System Actions

| System action | Architectural handling |
|---|---|
| Banner tu dong chuyen sang anh tiep theo sau 5 giay | UI state noi bo trong `HomeViewModel` |
| Hien thi tat ca playlist | `ChannelRepository.observeChannelSources()`, `observeChannelSourcesWithCount()`, `observeAllChannelGroupsWithChannels()` |
| Loc danh sach playlist theo `URL`, `STREAM`, `FILE`, `DEVICE` | Mo rong `ChannelRepository` de observe/filter theo `SourceType` |
| Hien thi empty state khi chua co playlist | Dua vao ket qua source rong tu `ChannelRepository` |
| Hien thi 4 the source playlist va count channels | `ChannelRepository.observeChannelSourcesWithCount()` va ham filter theo type |
| Hien thi `Recent` toi da 10 item | `ChannelRepository.observeRecentChannels(limit = 10)` |
| Hien thi `Favorite` toi da 10 item | `ChannelRepository.observeFavoriteChannels()`, ViewModel cat 10 item |
| Hien thi cac section playlist hien co, moi section toi da 10 item | `ChannelRepository.observeAllChannelGroupsWithChannels()`, ViewModel cat 10 item/group |
| Them hoac bo item khoi `Favorite` | `ChannelRepository.favoriteChannel()` hoac `unfavoriteChannel()` |
| Hien thi popup menu source/channel | UI state noi bo trong `HomeViewModel` |
| Mo/dong add overlay tu nut `+` | UI state noi bo trong `HomeViewModel` |
| Cac action ghi `Khong co hanh dong` | Intent duoc nhan, khong goi repository, khong phat side effect |

## Architecture Tree

```text
app/src/main/java/com/project/composeproject/
├── ui/
│   └── screen/
│       └── home/
│           ├── HomeScreen.kt                         [Mo rong]
│           ├── HomeViewModel.kt                      [Them moi]
│           ├── HomeUiState.kt                        [Them moi]
│           ├── HomeIntent.kt                         [Them moi]
│           ├── HomeSideEffect.kt                     [Them moi]
│           └── model/
│               ├── HomePlaylistFilter.kt             [Them moi]
│               ├── HomeSourceCardUiModel.kt          [Them moi]
│               ├── HomeChannelUiModel.kt             [Them moi]
│               └── HomeSectionUiModel.kt             [Them moi]
├── domain/
│   ├── model/
│   │   ├── Channel.kt                                [Giu nguyen]
│   │   ├── ChannelGroup.kt                           [Giu nguyen]
│   │   ├── ChannelSource.kt                          [Giu nguyen]
│   │   ├── SourceType.kt                             [Giu nguyen]
│   │   └── DataResult.kt                             [Giu nguyen]
│   └── repository/
│       └── ChannelRepository.kt                      [Mo rong]
├── data/
│   ├── repository/
│   │   └── ChannelRepositoryImpl.kt                  [Mo rong]
│   ├── mapper/
│   │   └── ChannelMapper.kt                          [Giu nguyen]
│   ├── source/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt                        [Giu nguyen]
│   │   │   ├── dao/
│   │   │   │   ├── ChannelDao.kt                     [Co the mo rong neu can filter DB-level]
│   │   │   │   ├── ChannelGroupDao.kt                [Giu nguyen]
│   │   │   │   └── ChannelSourceDao.kt               [Co the mo rong neu can filter DB-level]
│   │   │   └── entity/
│   │   │       ├── ChannelEntity.kt                  [Giu nguyen]
│   │   │       ├── ChannelGroupEntity.kt             [Giu nguyen]
│   │   │       └── ChannelSourceEntity.kt            [Giu nguyen]
│   │   ├── network/
│   │   │   └── M3uPlaylistNetworkSource.kt           [Giu nguyen]
│   │   └── storage/
│   │       └── M3uPlaylistStorageSource.kt           [Giu nguyen]
└── di/
    ├── DatabaseModule.kt                             [Giu nguyen]
    └── RepositoryModule.kt                           [Giu nguyen]
```

## UI Layer

UI Layer tuan thu MVI theo `viewmodel-mvi-rule.md`:

| Component | Role |
|---|---|
| `HomeScreen` | Compose screen render UI tu `HomeUiState`, gui user actions thanh `HomeIntent` |
| `HomeViewModel` | Nhan `HomeIntent` qua `onIntent()`, observe data tu repository, cap nhat `HomeUiState`, phat `HomeSideEffect` neu can |
| `HomeUiState` | Boc toan bo state hien thi cua Home Screen |
| `HomeIntent` | Sealed class dai dien action tu UI/user |
| `HomeSideEffect` | Sealed class cho event mot lan |
| UI models | Dinh dang data tu domain model sang data hien thi tren Home Screen |

### HomeUiState

```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val selectedFilter: HomePlaylistFilter = HomePlaylistFilter.ALL,
    val bannerIndex: Int = 0,
    val sourceCards: List<HomeSourceCardUiModel> = emptyList(),
    val recentSection: HomeSectionUiModel? = null,
    val favoriteSection: HomeSectionUiModel? = null,
    val playlistSections: List<HomeSectionUiModel> = emptyList(),
    val openedPlaylistSourceMenuId: Long? = null,
    val openedChannelMenuId: Long? = null,
    val isAddOverlayOpen: Boolean = false,
    val errorMessage: String? = null,
)
```

### HomeIntent

```kotlin
sealed class HomeIntent {
    data object LoadHome : HomeIntent()
    data object OnHelpClicked : HomeIntent()
    data object OnBannerAutoAdvance : HomeIntent()

    data class OnFilterSelected(val filter: HomePlaylistFilter) : HomeIntent()

    data object OnChannelTabClicked : HomeIntent()
    data object OnFavoriteTabClicked : HomeIntent()
    data object OnSettingsTabClicked : HomeIntent()

    data object OnTutorialGetStartedClicked : HomeIntent()

    data class OnSourceCardClicked(val sourceType: SourceType) : HomeIntent()
    data class OnSourceMenuClicked(val sourceType: SourceType) : HomeIntent()
    data class OnSectionViewAllClicked(val sectionId: String) : HomeIntent()
    data class OnChannelClicked(val channelId: Long) : HomeIntent()
    data class OnFavoriteClicked(val channelId: Long, val isFavorited: Boolean) : HomeIntent()
    data class OnChannelMenuClicked(val channelId: Long) : HomeIntent()

    data object OnEditPlaylistClicked : HomeIntent()
    data object OnDeletePlaylistClicked : HomeIntent()
    data object OnEditChannelClicked : HomeIntent()
    data object OnDeleteChannelClicked : HomeIntent()

    data object OnAddClicked : HomeIntent()
    data object OnAddOverlayDismissed : HomeIntent()
    data object OnPlaySingleUrlClicked : HomeIntent()
    data object OnImportPlaylistUrlClicked : HomeIntent()
    data object OnImportFromDeviceClicked : HomeIntent()
    data object OnUploadM3uFileClicked : HomeIntent()
}
```

### HomeSideEffect

SRS khong yeu cau navigation/toast. SideEffect chi dung cho event mot lan neu can bao loi.

```kotlin
sealed class HomeSideEffect {
    data class ShowError(val message: String) : HomeSideEffect()
}
```

### HomeViewModel Contract

```kotlin
class HomeViewModel(
    private val channelRepository: ChannelRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState>
    val sideEffect: SharedFlow<HomeSideEffect>

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadHome -> observeHomeData()
            HomeIntent.OnBannerAutoAdvance -> advanceBanner()
            is HomeIntent.OnFilterSelected -> selectFilter(intent.filter)
            is HomeIntent.OnFavoriteClicked -> toggleFavorite(intent.channelId, intent.isFavorited)
            HomeIntent.OnAddClicked -> openAddOverlay()
            HomeIntent.OnAddOverlayDismissed -> closeAddOverlay()
            else -> handleNoActionIntent(intent)
        }
    }
}
```

## Data Layer

Data Layer tuan thu Repository Pattern theo `repository-rule.md`:

| Layer item | Status | Role |
|---|---|---|
| `ChannelRepository` | Mo rong | Interface giao tiep giua ViewModel va Data Layer |
| `ChannelRepositoryImpl` | Mo rong | Implementation dung DAO/source hien co |
| `DataResult` | Giu nguyen | Result wrapper co `Success`, `Error`, `Loading` |
| `ChannelDao` | Co the mo rong | Quan ly channel trong Room |
| `ChannelGroupDao` | Giu nguyen | Quan ly group trong Room |
| `ChannelSourceDao` | Co the mo rong | Quan ly source trong Room |
| `M3uPlaylistNetworkSource` | Giu nguyen | Source import M3U URL hien co, khong dung cho action Home theo SRS |
| `M3uPlaylistStorageSource` | Giu nguyen | Source import M3U file hien co, khong dung cho action Home theo SRS |

### Repository Interface Existing Contract

```kotlin
interface ChannelRepository {
    fun observeChannels(groupId: Long): Flow<DataResult<List<Channel>>>

    fun observeAllChannels(): Flow<DataResult<List<Channel>>>

    fun observeFavoriteChannels(): Flow<DataResult<List<Channel>>>

    fun observeRecentChannels(limit: Int = 10): Flow<DataResult<List<Channel>>>

    suspend fun favoriteChannel(channelId: Long): DataResult<Unit>

    suspend fun unfavoriteChannel(channelId: Long): DataResult<Unit>

    fun observeChannelSources(): Flow<DataResult<List<ChannelSource>>>

    fun observeChannelSourcesWithCount(): Flow<DataResult<Map<ChannelSource, Int>>>

    fun observeAllChannelGroupsWithChannels(): Flow<DataResult<Map<ChannelGroup, List<Channel>>>>
}
```

### Repository Interface Extension

Mo rong `ChannelRepository` de phuc vu filter source theo SRS.

```kotlin
interface ChannelRepository {
    fun observeChannelSourcesByType(
        sourceType: SourceType,
    ): Flow<DataResult<List<ChannelSource>>>

    fun observeChannelSourcesWithCountByType(
        sourceType: SourceType,
    ): Flow<DataResult<Map<ChannelSource, Int>>>
}
```

## Repository Mapping

| SRS requirement | Repository/API |
|---|---|
| Empty state khi chua co playlist | `observeChannelSources()` |
| Hien thi source cards va channel count | `observeChannelSourcesWithCount()` |
| Filter `ALL` | `observeChannelSources()`, `observeChannelSourcesWithCount()`, `observeAllChannelGroupsWithChannels()` |
| Filter `URL`, `STREAM`, `FILE`, `DEVICE` | `observeChannelSourcesByType(sourceType)`, `observeChannelSourcesWithCountByType(sourceType)` |
| Recent toi da 10 | `observeRecentChannels(limit = 10)` |
| Favorite section | `observeFavoriteChannels()`, ViewModel `take(10)` |
| Playlist sections hien co | `observeAllChannelGroupsWithChannels()`, ViewModel filter theo selected source va `take(10)` moi group |
| Toggle favorite | `favoriteChannel(channelId)`, `unfavoriteChannel(channelId)` |
| Popup menu | Khong can repository |
| Add overlay | Khong can repository |
| Banner auto advance | Khong can repository |

## Data Flow

```text
HomeScreen
    -> HomeIntent
    -> HomeViewModel.onIntent(intent)
    -> ChannelRepository
    -> ChannelRepositoryImpl
    -> Room DAO / existing data sources
    -> DataResult<T>
    -> HomeViewModel maps domain data to HomeUiState
    -> HomeScreen renders state
```

## Notes

- UI naming dung `Add` cho overlay/action lien quan nut `+`.
- UI van co the hien thi icon/text `+` theo SRS.
- Khong them navigation cho Help, Channel, Favorite nav, Settings, View All, channel click, add overlay buttons vi SRS ghi `Khong co hanh dong`.
- Khong them import playlist behavior tu Home Screen.
- Khong them edit/delete behavior du repository hien co co ham tuong ung.
- Khong them data source moi.
- Khong mo rong repository de gioi han 10 item/group. ViewModel tu cat 10 item/group theo quyet dinh da duyet.
- Repository return type phai dung `DataResult<T>` hoac `Flow<DataResult<T>>` theo rule.
- ViewModel nhan Intent bang `onIntent()` va xu ly truc tiep bang `when`; khong dung Flow de nhan Intent.

## Self Review

| Check | Result |
|---|---|
| Repository Interface phuc vu du System Actions trong SRS | Pass |
| Khong them Interface/DataSource/nguon data ngoai SRS | Pass |
| Nguon data ro rang va khop project hien co | Pass |
| UI Layer tuan thu MVI | Pass |
| Data Layer tuan thu Repository Pattern | Pass |
| Architecture tree hien thi Data Layer co san | Pass |
| Naming overlay dung `Add` | Pass |
