package com.project.composeproject.ui.screen.home.model

data class HomeSectionUiModel(
    val id: String,
    val title: String,
    val totalCount: Int,
    val channels: List<HomeChannelUiModel>,
)
