package com.project.composeproject.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.composeproject.domain.model.Channel
import com.project.composeproject.domain.model.ChannelGroup
import com.project.composeproject.domain.model.ChannelSource
import com.project.composeproject.domain.model.DataResult
import com.project.composeproject.domain.repository.ChannelRepository
import com.project.composeproject.ui.screen.home.model.HomeChannelUiModel
import com.project.composeproject.ui.screen.home.model.HomePlaylistFilter
import com.project.composeproject.ui.screen.home.model.HomeSectionUiModel
import com.project.composeproject.ui.screen.home.model.HomeSourceCardUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeSideEffect>()
    val sideEffect: SharedFlow<HomeSideEffect> = _sideEffect.asSharedFlow()

    init {
        onIntent(HomeIntent.LoadHome)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadHome -> observeHomeData()
            HomeIntent.OnBannerAutoAdvance -> advanceBanner()
            is HomeIntent.OnFilterSelected -> selectFilter(intent.filter)
            is HomeIntent.OnFavoriteClicked -> toggleFavorite(intent.channelId, intent.isFavorited)
            is HomeIntent.OnSourceMenuClicked -> openSourceMenu(intent.sourceType)
            HomeIntent.OnPlaylistMenuDismissed -> closeSourceMenu()
            is HomeIntent.OnChannelMenuClicked -> openChannelMenu(intent.channelId)
            HomeIntent.OnChannelMenuDismissed -> closeChannelMenu()
            HomeIntent.OnEditPlaylistClicked,
            HomeIntent.OnDeletePlaylistClicked -> closeSourceMenu()
            HomeIntent.OnEditChannelClicked,
            HomeIntent.OnDeleteChannelClicked -> closeChannelMenu()
            HomeIntent.OnAddClicked -> openAddOverlay()
            HomeIntent.OnAddOverlayDismissed,
            HomeIntent.OnPlaySingleUrlClicked,
            HomeIntent.OnImportPlaylistUrlClicked,
            HomeIntent.OnImportFromDeviceClicked,
            HomeIntent.OnUploadM3uFileClicked -> closeAddOverlay()
            else -> Unit
        }
    }

    private fun observeHomeData() {
        viewModelScope.launch {
            combine(
                channelRepository.observeChannelSourcesWithCount(),
                channelRepository.observeRecentChannels(limit = HOME_SECTION_LIMIT),
                channelRepository.observeFavoriteChannels(),
                channelRepository.observeAllChannelGroupsWithChannels(),
            ) { sourceResult, recentResult, favoriteResult, groupsResult ->
                HomeDataResults(sourceResult, recentResult, favoriteResult, groupsResult)
            }.collect { results ->
                when {
                    results.hasError -> handleError(results.errorMessage)
                    results.isLoading -> _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    results.isSuccess -> updateContent(
                        sourcesWithCount = results.sourcesWithCount.orEmpty(),
                        recentChannels = results.recentChannels.orEmpty(),
                        favoriteChannels = results.favoriteChannels.orEmpty(),
                        groupsWithChannels = results.groupsWithChannels.orEmpty(),
                    )
                }
            }
        }
    }

    private fun updateContent(
        sourcesWithCount: Map<ChannelSource, Int>,
        recentChannels: List<Channel>,
        favoriteChannels: List<Channel>,
        groupsWithChannels: Map<ChannelGroup, List<Channel>>,
    ) {
        _uiState.update { state ->
            val filteredSources = sourcesWithCount.filterKeysBy(state.selectedFilter)
            val allowedSourceIds = filteredSources.keys.map { it.id }.toSet()
            state.copy(
                isLoading = false,
                sourceCards = filteredSources.entries
                    .sortedBy { it.key.sourceType.ordinal }
                    .map { (source, count) -> source.toSourceCard(count) },
                recentSection = recentChannels
                    .filterBySources(groupsWithChannels, allowedSourceIds)
                    .toSectionOrNull(id = HOME_RECENT_SECTION_ID, title = HOME_RECENT_SECTION_TITLE),
                favoriteSection = favoriteChannels
                    .filterBySources(groupsWithChannels, allowedSourceIds)
                    .take(HOME_SECTION_LIMIT)
                    .toSectionOrNull(id = HOME_FAVORITE_SECTION_ID, title = HOME_FAVORITE_SECTION_TITLE),
                playlistSections = groupsWithChannels.entries
                    .filter { (group, _) -> allowedSourceIds.isEmpty() || group.sourceId in allowedSourceIds }
                    .sortedBy { it.key.modifiedAt }
                    .mapNotNull { (group, channels) ->
                        channels.take(HOME_SECTION_LIMIT).toSectionOrNull(
                            id = "playlist_${group.id}",
                            title = group.name,
                        )
                    },
                errorMessage = null,
            )
        }
    }

    private fun handleError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
        viewModelScope.launch { _sideEffect.emit(HomeSideEffect.ShowError(message)) }
    }

    private fun advanceBanner() {
        _uiState.update { state ->
            state.copy(bannerIndex = (state.bannerIndex + 1) % HOME_BANNER_COUNT)
        }
    }

    private fun selectFilter(filter: HomePlaylistFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    private fun toggleFavorite(channelId: Long, isFavorited: Boolean) {
        viewModelScope.launch {
            val result = if (isFavorited) {
                channelRepository.unfavoriteChannel(channelId)
            } else {
                channelRepository.favoriteChannel(channelId)
            }
            if (result is DataResult.Error) {
                handleError(result.exception.message ?: HOME_UNKNOWN_ERROR)
            }
        }
    }

    private fun openSourceMenu(sourceType: com.project.composeproject.domain.model.SourceType) {
        val sourceId = _uiState.value.sourceCards.firstOrNull { it.sourceType == sourceType }?.id ?: return
        _uiState.update { it.copy(openedPlaylistSourceMenuId = sourceId, openedChannelMenuId = null) }
    }

    private fun closeSourceMenu() {
        _uiState.update { it.copy(openedPlaylistSourceMenuId = null) }
    }

    private fun openChannelMenu(channelId: Long) {
        _uiState.update { it.copy(openedChannelMenuId = channelId, openedPlaylistSourceMenuId = null) }
    }

    private fun closeChannelMenu() {
        _uiState.update { it.copy(openedChannelMenuId = null) }
    }

    private fun openAddOverlay() {
        _uiState.update { it.copy(isAddOverlayOpen = true, openedPlaylistSourceMenuId = null, openedChannelMenuId = null) }
    }

    private fun closeAddOverlay() {
        _uiState.update { it.copy(isAddOverlayOpen = false) }
    }

    private data class HomeDataResults(
        val sourceResult: DataResult<Map<ChannelSource, Int>>,
        val recentResult: DataResult<List<Channel>>,
        val favoriteResult: DataResult<List<Channel>>,
        val groupsResult: DataResult<Map<ChannelGroup, List<Channel>>>,
    ) {
        val isLoading: Boolean
            get() = sourceResult is DataResult.Loading ||
                recentResult is DataResult.Loading ||
                favoriteResult is DataResult.Loading ||
                groupsResult is DataResult.Loading

        val hasError: Boolean
            get() = sourceResult is DataResult.Error ||
                recentResult is DataResult.Error ||
                favoriteResult is DataResult.Error ||
                groupsResult is DataResult.Error

        val errorMessage: String
            get() = listOf(sourceResult, recentResult, favoriteResult, groupsResult)
                .filterIsInstance<DataResult.Error>()
                .firstOrNull()
                ?.exception
                ?.message
                ?: HOME_UNKNOWN_ERROR

        val isSuccess: Boolean
            get() = sourceResult is DataResult.Success &&
                recentResult is DataResult.Success &&
                favoriteResult is DataResult.Success &&
                groupsResult is DataResult.Success

        val sourcesWithCount: Map<ChannelSource, Int>?
            get() = (sourceResult as? DataResult.Success)?.data

        val recentChannels: List<Channel>?
            get() = (recentResult as? DataResult.Success)?.data

        val favoriteChannels: List<Channel>?
            get() = (favoriteResult as? DataResult.Success)?.data

        val groupsWithChannels: Map<ChannelGroup, List<Channel>>?
            get() = (groupsResult as? DataResult.Success)?.data
    }

    private fun Map<ChannelSource, Int>.filterKeysBy(filter: HomePlaylistFilter): Map<ChannelSource, Int> {
        val selectedType = filter.sourceType ?: return this
        return filterKeys { it.sourceType == selectedType }
    }

    private fun List<Channel>.filterBySources(
        groupsWithChannels: Map<ChannelGroup, List<Channel>>,
        sourceIds: Set<Long>,
    ): List<Channel> {
        if (sourceIds.isEmpty()) return take(HOME_SECTION_LIMIT)
        val groupIds = groupsWithChannels.keys
            .filter { it.sourceId in sourceIds }
            .map { it.id }
            .toSet()
        return filter { it.groupId in groupIds }.take(HOME_SECTION_LIMIT)
    }

    private fun ChannelSource.toSourceCard(channelCount: Int): HomeSourceCardUiModel {
        return HomeSourceCardUiModel(
            id = id,
            sourceType = sourceType,
            title = name,
            subtitle = if (channelCount == 1) "1 channels" else "$channelCount channels",
            channelCount = channelCount,
        )
    }

    private fun List<Channel>.toSectionOrNull(
        id: String,
        title: String,
    ): HomeSectionUiModel? {
        if (isEmpty()) return null
        return HomeSectionUiModel(
            id = id,
            title = title,
            itemCount = size,
            items = take(HOME_SECTION_LIMIT).map { channel ->
                HomeChannelUiModel(
                    id = channel.id,
                    title = channel.name,
                    thumbnailUrl = channel.logoUrl,
                    isFavorited = channel.isFavorited,
                )
            },
        )
    }

    private companion object {
        const val HOME_BANNER_COUNT = 4
        const val HOME_SECTION_LIMIT = 10
        const val HOME_RECENT_SECTION_ID = "recent"
        const val HOME_FAVORITE_SECTION_ID = "favorite"
        const val HOME_RECENT_SECTION_TITLE = "Recent"
        const val HOME_FAVORITE_SECTION_TITLE = "Favorite"
        const val HOME_UNKNOWN_ERROR = "Unknown error"
    }
}
