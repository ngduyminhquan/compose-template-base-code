package com.project.composeproject.ui.screen.home

import com.project.composeproject.ui.screen.home.model.HomePlaylistFilter
import com.project.composeproject.ui.screen.home.model.HomeSectionUiModel
import com.project.composeproject.ui.screen.home.model.HomeSourceCardUiModel

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
