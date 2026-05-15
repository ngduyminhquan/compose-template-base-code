package com.project.composeproject.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.composeproject.domain.model.Channel
import com.project.composeproject.domain.model.ChannelGroup
import com.project.composeproject.domain.model.ChannelSource
import com.project.composeproject.domain.model.DataResult
import com.project.composeproject.domain.model.SourceType
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
) : ViewModel() {

    private val selectedFilter = MutableStateFlow(HomePlaylistFilter.ALL)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _sideEffect = MutableSharedFlow<HomeSideEffect>()
    val sideEffect: SharedFlow<HomeSideEffect> = _sideEffect

    init {
        observeHomeData()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadHome -> observeHomeData()
            HomeIntent.OnBannerAutoAdvance -> advanceBanner()
            is HomeIntent.OnFilterSelected -> selectedFilter.value = intent.filter
            is HomeIntent.OnFavoriteClicked -> toggleFavorite(intent.channelId, intent.isFavorited)
            is HomeIntent.OnSourceMenuClicked -> _uiState.update { it.copy(openedPlaylistSourceMenuType = intent.sourceType.name) }
            is HomeIntent.OnChannelMenuClicked -> _uiState.update { it.copy(openedChannelMenuId = intent.channelId) }
            HomeIntent.OnAddClicked -> _uiState.update { it.copy(isAddOverlayOpen = true) }
            HomeIntent.OnAddOverlayDismissed -> _uiState.update { it.copy(isAddOverlayOpen = false) }
            else -> Unit
        }
    }

    private fun observeHomeData() {
        viewModelScope.launch {
            combine(
                selectedFilter,
                channelRepository.observeChannelSourcesWithCount(),
                channelRepository.observeRecentChannels(limit = SECTION_LIMIT),
                channelRepository.observeFavoriteChannels(),
                channelRepository.observeAllChannelGroupsWithChannels(),
            ) { filter, sourceCounts, recent, favorite, groups ->
                buildState(filter, sourceCounts, recent, favorite, groups)
            }.collect { newState ->
                _uiState.value = newState.copy(
                    bannerIndex = _uiState.value.bannerIndex,
                    openedPlaylistSourceMenuType = _uiState.value.openedPlaylistSourceMenuType,
                    openedChannelMenuId = _uiState.value.openedChannelMenuId,
                    isAddOverlayOpen = _uiState.value.isAddOverlayOpen,
                )
            }
        }
    }

    private fun buildState(
        filter: HomePlaylistFilter,
        sourceCountsResult: DataResult<Map<ChannelSource, Int>>,
        recentResult: DataResult<List<Channel>>,
        favoriteResult: DataResult<List<Channel>>,
        groupResult: DataResult<Map<ChannelGroup, List<Channel>>>,
    ): HomeUiState {
        val errorMessage = listOf(sourceCountsResult, recentResult, favoriteResult, groupResult)
            .firstNotNullOfOrNull { (it as? DataResult.Error)?.exception?.message }
        val sourceCounts = (sourceCountsResult as? DataResult.Success)?.data.orEmpty()
        val recent = (recentResult as? DataResult.Success)?.data.orEmpty()
        val favorite = (favoriteResult as? DataResult.Success)?.data.orEmpty()
        val groups = (groupResult as? DataResult.Success)?.data.orEmpty()
        val allowedSourceIds = sourceCounts.keys
            .filter { filter.sourceType == null || it.sourceType == filter.sourceType }
            .map { it.id }
            .toSet()
        val filteredGroups = groups.filterKeys { group ->
            filter.sourceType == null || group.sourceId in allowedSourceIds
        }

        return HomeUiState(
            isLoading = listOf(sourceCountsResult, recentResult, favoriteResult, groupResult).any { it is DataResult.Loading },
            selectedFilter = filter,
            sourceCards = buildSourceCards(sourceCounts),
            hasPlaylists = sourceCounts.isNotEmpty(),
            recentSection = HomeSectionUiModel(RECENT_ID, RECENT_TITLE, recent.size, recent.take(SECTION_LIMIT).map { it.toUiModel() }),
            favoriteSection = HomeSectionUiModel(FAVORITE_ID, FAVORITE_TITLE, favorite.size, favorite.take(SECTION_LIMIT).map { it.toUiModel() }),
            playlistSections = filteredGroups.map { (group, channels) ->
                HomeSectionUiModel(
                    id = group.id.toString(),
                    title = group.name,
                    totalCount = channels.size,
                    channels = channels.take(SECTION_LIMIT).map { it.toUiModel() },
                )
            },
            errorMessage = errorMessage,
        )
    }

    private fun buildSourceCards(sourceCounts: Map<ChannelSource, Int>): List<HomeSourceCardUiModel> {
        return SourceType.entries.map { type ->
            val sources = sourceCounts.filterKeys { it.sourceType == type }
            HomeSourceCardUiModel(
                sourceType = type,
                sourceIds = sources.keys.map { it.id },
                title = type.displayTitle(),
                channelCount = sources.values.sum(),
            )
        }
    }

    private fun toggleFavorite(channelId: Long, isFavorited: Boolean) {
        viewModelScope.launch {
            val result = if (isFavorited) {
                channelRepository.unfavoriteChannel(channelId)
            } else {
                channelRepository.favoriteChannel(channelId)
            }
            if (result is DataResult.Error) {
                _sideEffect.emit(HomeSideEffect.ShowError(result.exception.message.orEmpty()))
            }
        }
    }

    private fun advanceBanner() {
        _uiState.update { it.copy(bannerIndex = (it.bannerIndex + 1) % BANNER_COUNT) }
    }
}

private fun Channel.toUiModel(): HomeChannelUiModel {
    return HomeChannelUiModel(
        id = id,
        title = name,
        logoUrl = logoUrl,
        isFavorited = isFavorited,
    )
}

private fun SourceType.displayTitle(): String {
    return when (this) {
        SourceType.FILE -> "File"
        SourceType.DEVICE -> "Device"
        SourceType.URL -> "URL"
        SourceType.STREAM -> "Stream"
    }
}

private const val SECTION_LIMIT = 10
private const val BANNER_COUNT = 4
private const val RECENT_ID = "recent"
private const val FAVORITE_ID = "favorite"
private const val RECENT_TITLE = "Recent"
private const val FAVORITE_TITLE = "Favorite"
