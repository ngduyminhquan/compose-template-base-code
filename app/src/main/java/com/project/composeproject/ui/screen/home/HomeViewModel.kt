package com.project.composeproject.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.composeproject.domain.model.Channel
import com.project.composeproject.domain.model.DataResult
import com.project.composeproject.domain.model.SourceType
import com.project.composeproject.domain.repository.ChannelRepository
import com.project.composeproject.ui.screen.home.model.HomeChannelUiModel
import com.project.composeproject.ui.screen.home.model.HomePlaylistFilter
import com.project.composeproject.ui.screen.home.model.HomeSectionUiModel
import com.project.composeproject.ui.screen.home.model.HomeSourceCardUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var observeJob: Job? = null

    init {
        observeHomeData()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadHome -> observeHomeData()
            HomeIntent.OnBannerAutoAdvance -> advanceBanner()
            is HomeIntent.OnFilterSelected -> selectFilter(intent.filter)
            is HomeIntent.OnFavoriteClicked -> toggleFavorite(intent.channelId, intent.isFavorited)
            HomeIntent.OnAddClicked -> openAddOverlay()
            HomeIntent.OnAddOverlayDismissed -> closeAddOverlay()
            is HomeIntent.OnSourceMenuClicked -> openPlaylistSourceMenu(intent.sourceType)
            is HomeIntent.OnChannelMenuClicked -> openChannelMenu(intent.channelId)
            else -> handleNoActionIntent(intent)
        }
    }

    private fun observeHomeData() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            val filter = _uiState.value.selectedFilter

            launch {
                val sourcesFlow = if (filter == HomePlaylistFilter.ALL) {
                    channelRepository.observeChannelSourcesWithCount()
                } else {
                    channelRepository.observeChannelSourcesWithCountByType(filter.toSourceType())
                }
                sourcesFlow.collect { result ->
                    when (result) {
                        is DataResult.Success -> {
                            val cards = result.data.map { (source, count) ->
                                HomeSourceCardUiModel(
                                    id = source.id,
                                    sourceType = source.sourceType,
                                    name = source.name,
                                    channelCount = count
                                )
                            }
                            _uiState.update { it.copy(sourceCards = cards) }
                        }
                        is DataResult.Error -> emitError(result.exception.message ?: "Error loading sources")
                        DataResult.Loading -> { /* Handle loading if needed */ }
                    }
                }
            }

            launch {
                channelRepository.observeRecentChannels(limit = 10).collect { result ->
                    if (result is DataResult.Success) {
                        val items = result.data.map { it.toUiModel() }
                        _uiState.update { 
                            it.copy(recentSection = if (items.isNotEmpty()) HomeSectionUiModel("recent", "Recent", items.size, items) else null) 
                        }
                    }
                }
            }

            launch {
                channelRepository.observeFavoriteChannels().collect { result ->
                    if (result is DataResult.Success) {
                        val items = result.data.take(10).map { it.toUiModel() }
                        _uiState.update { 
                            it.copy(favoriteSection = if (items.isNotEmpty()) HomeSectionUiModel("favorite", "Favorite", items.size, items) else null) 
                        }
                    }
                }
            }

            launch {
                combine(
                    channelRepository.observeAllChannelGroupsWithChannels(),
                    channelRepository.observeChannelSources()
                ) { groupsResult, sourcesResult ->
                    if (groupsResult is DataResult.Success && sourcesResult is DataResult.Success) {
                        val sources = sourcesResult.data
                        val validSourceIds = if (filter == HomePlaylistFilter.ALL) {
                            sources.map { it.id }.toSet()
                        } else {
                            sources.filter { it.sourceType == filter.toSourceType() }.map { it.id }.toSet()
                        }
                        
                        val groups = groupsResult.data.filterKeys { it.sourceId in validSourceIds }
                        val mappedSections = groups.map { (group, channels) ->
                            val items = channels.take(10).map { it.toUiModel() }
                            HomeSectionUiModel(
                                id = group.id.toString(),
                                title = group.name,
                                count = channels.size,
                                items = items
                            )
                        }
                        _uiState.update { it.copy(playlistSections = mappedSections) }
                    }
                }.collect {}
            }
        }
    }

    private fun advanceBanner() {
        _uiState.update { it.copy(bannerIndex = (it.bannerIndex + 1) % 3) }
    }

    private fun selectFilter(filter: HomePlaylistFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        observeHomeData()
    }

    private fun toggleFavorite(channelId: Long, isFavorited: Boolean) {
        viewModelScope.launch {
            if (isFavorited) {
                channelRepository.unfavoriteChannel(channelId)
            } else {
                channelRepository.favoriteChannel(channelId)
            }
        }
    }

    private fun openAddOverlay() {
        _uiState.update { it.copy(isAddOverlayOpen = true) }
    }

    private fun closeAddOverlay() {
        _uiState.update { it.copy(isAddOverlayOpen = false) }
    }
    
    private fun openPlaylistSourceMenu(sourceType: SourceType) {
        _uiState.update { it.copy(openedPlaylistSourceMenuId = sourceType.ordinal.toLong()) }
    }
    
    private fun openChannelMenu(channelId: Long) {
        _uiState.update { it.copy(openedChannelMenuId = channelId) }
    }

    private fun handleNoActionIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OnEditPlaylistClicked, HomeIntent.OnDeletePlaylistClicked -> {
                _uiState.update { it.copy(openedPlaylistSourceMenuId = null) }
            }
            HomeIntent.OnEditChannelClicked, HomeIntent.OnDeleteChannelClicked -> {
                _uiState.update { it.copy(openedChannelMenuId = null) }
            }
            else -> {}
        }
    }

    private fun emitError(message: String) {
        viewModelScope.launch {
            _sideEffect.emit(HomeSideEffect.ShowError(message))
        }
    }

    private fun Channel.toUiModel() = HomeChannelUiModel(
        id = this.id,
        name = this.name,
        logoUrl = this.logoUrl ?: "",
        isFavorited = this.isFavorited
    )

    private fun HomePlaylistFilter.toSourceType(): SourceType {
        return when (this) {
            HomePlaylistFilter.URL -> SourceType.URL
            HomePlaylistFilter.STREAM -> SourceType.STREAM
            HomePlaylistFilter.FILE -> SourceType.FILE
            HomePlaylistFilter.DEVICE -> SourceType.DEVICE
            HomePlaylistFilter.ALL -> SourceType.URL
        }
    }
}
