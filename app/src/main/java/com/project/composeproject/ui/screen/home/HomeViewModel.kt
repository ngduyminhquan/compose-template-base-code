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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeSideEffect>()
    val sideEffect: SharedFlow<HomeSideEffect> = _sideEffect.asSharedFlow()

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadHome -> observeHomeData()
            HomeIntent.OnBannerAutoAdvance -> advanceBanner()
            is HomeIntent.OnFilterSelected -> selectFilter(intent.filter)
            is HomeIntent.OnFavoriteClicked -> toggleFavorite(intent.channelId, intent.isFavorited)
            HomeIntent.OnAddClicked -> openAddOverlay()
            HomeIntent.OnAddOverlayDismissed -> closeAddOverlay()
            is HomeIntent.OnSourceMenuClicked -> openSourceMenu(intent.sourceType)
            is HomeIntent.OnChannelMenuClicked -> openChannelMenu(intent.channelId)
            else -> handleNoActionIntent(intent)
        }
    }

    private fun observeHomeData() {
        viewModelScope.launch {
            combine(
                channelRepository.observeChannelSourcesWithCount(),
                channelRepository.observeRecentChannels(limit = 10),
                channelRepository.observeFavoriteChannels(),
                channelRepository.observeAllChannelGroupsWithChannels()
            ) { sourcesResult, recentResult, favoriteResult, groupsResult ->
                _uiState.update { currentState ->
                    val sourceCards = when (sourcesResult) {
                        is DataResult.Success -> buildSourceCards(sourcesResult.data, currentState.selectedFilter)
                        else -> currentState.sourceCards
                    }

                    val recentSection = when (recentResult) {
                        is DataResult.Success -> buildRecentSection(recentResult.data)
                        else -> currentState.recentSection
                    }

                    val favoriteSection = when (favoriteResult) {
                        is DataResult.Success -> buildFavoriteSection(favoriteResult.data)
                        else -> currentState.favoriteSection
                    }

                    val playlistSections = when (groupsResult) {
                        is DataResult.Success -> buildPlaylistSections(groupsResult.data, currentState.selectedFilter)
                        else -> currentState.playlistSections
                    }

                    val isLoading = sourcesResult is DataResult.Loading ||
                            recentResult is DataResult.Loading ||
                            favoriteResult is DataResult.Loading ||
                            groupsResult is DataResult.Loading

                    currentState.copy(
                        isLoading = isLoading,
                        sourceCards = sourceCards,
                        recentSection = recentSection,
                        favoriteSection = favoriteSection,
                        playlistSections = playlistSections
                    )
                }
            }.collect {}
        }
    }

    private fun advanceBanner() {
        _uiState.update { currentState ->
            val nextIndex = (currentState.bannerIndex + 1) % BANNER_COUNT
            currentState.copy(bannerIndex = nextIndex)
        }
    }

    private fun selectFilter(filter: HomePlaylistFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        observeHomeData()
    }

    private fun toggleFavorite(channelId: Long, isFavorited: Boolean) {
        viewModelScope.launch {
            val result = if (isFavorited) {
                channelRepository.unfavoriteChannel(channelId)
            } else {
                channelRepository.favoriteChannel(channelId)
            }

            if (result is DataResult.Error) {
                _sideEffect.emit(HomeSideEffect.ShowError(result.exception.message ?: "Unknown error"))
            }
        }
    }

    private fun openAddOverlay() {
        _uiState.update { it.copy(isAddOverlayOpen = true) }
    }

    private fun closeAddOverlay() {
        _uiState.update { it.copy(isAddOverlayOpen = false) }
    }

    private fun openSourceMenu(sourceType: SourceType) {
        _uiState.update { it.copy(openedPlaylistSourceMenuId = sourceType.ordinal.toLong()) }
    }

    private fun openChannelMenu(channelId: Long) {
        _uiState.update { it.copy(openedChannelMenuId = channelId) }
    }

    private fun handleNoActionIntent(intent: HomeIntent) {
        // Intents that have no action according to SRS
    }

    private fun buildSourceCards(
        sourcesWithCount: Map<ChannelSource, Int>,
        filter: HomePlaylistFilter
    ): List<HomeSourceCardUiModel> {
        val allSourceTypes = SourceType.entries
        return allSourceTypes.map { sourceType ->
            val count = if (filter == HomePlaylistFilter.ALL || filter.name == sourceType.name) {
                sourcesWithCount.entries
                    .filter { it.key.sourceType == sourceType }
                    .sumOf { it.value }
            } else {
                0
            }
            HomeSourceCardUiModel(
                sourceType = sourceType,
                channelCount = count
            )
        }
    }

    private fun buildRecentSection(channels: List<Channel>): HomeSectionUiModel? {
        if (channels.isEmpty()) return null
        return HomeSectionUiModel(
            id = "recent",
            title = "Recent",
            count = channels.size,
            channels = channels.take(10).map { it.toUiModel() }
        )
    }

    private fun buildFavoriteSection(channels: List<Channel>): HomeSectionUiModel? {
        if (channels.isEmpty()) return null
        return HomeSectionUiModel(
            id = "favorite",
            title = "Favorite",
            count = channels.size,
            channels = channels.take(10).map { it.toUiModel() }
        )
    }

    private fun buildPlaylistSections(
        groupsWithChannels: Map<ChannelGroup, List<Channel>>,
        filter: HomePlaylistFilter
    ): List<HomeSectionUiModel> {
        return groupsWithChannels
            .filter { (_, channels) -> channels.isNotEmpty() }
            .map { (group, channels) ->
                HomeSectionUiModel(
                    id = "playlist_${group.id}",
                    title = group.name,
                    count = channels.size,
                    channels = channels.take(10).map { it.toUiModel() }
                )
            }
    }

    private fun Channel.toUiModel(): HomeChannelUiModel {
        return HomeChannelUiModel(
            id = id,
            name = name,
            thumbnailUrl = logoUrl,
            isFavorited = isFavorited
        )
    }

    private companion object {
        const val BANNER_COUNT = 4
    }
}
