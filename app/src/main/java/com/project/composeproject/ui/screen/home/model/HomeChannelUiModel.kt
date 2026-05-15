package com.project.composeproject.ui.screen.home.model

data class HomeChannelUiModel(
    val id: Long,
    val name: String,
    val thumbnailUrl: String?,
    val isFavorited: Boolean
)
