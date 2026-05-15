package com.project.composeproject.ui.screen.home

import com.project.composeproject.domain.model.SourceType
import com.project.composeproject.ui.screen.home.model.HomePlaylistFilter

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
    data object OnPlaylistMenuDismissed : HomeIntent()
    data class OnSectionViewAllClicked(val sectionId: String) : HomeIntent()
    data class OnChannelClicked(val channelId: Long) : HomeIntent()
    data class OnFavoriteClicked(val channelId: Long, val isFavorited: Boolean) : HomeIntent()
    data class OnChannelMenuClicked(val channelId: Long) : HomeIntent()
    data object OnChannelMenuDismissed : HomeIntent()
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
