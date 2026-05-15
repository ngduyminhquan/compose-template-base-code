package com.project.composeproject.ui.screen.home.model

import com.project.composeproject.domain.model.SourceType

data class HomeSourceCardUiModel(
    val sourceType: SourceType,
    val sourceIds: List<Long>,
    val title: String,
    val channelCount: Int,
)
