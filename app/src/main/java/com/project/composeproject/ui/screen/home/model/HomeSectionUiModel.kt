package com.project.composeproject.ui.screen.home.model

data class HomeSectionUiModel(
    val id: String,
    val title: String,
    val count: Int,
    val items: List<HomeChannelUiModel>,
)
