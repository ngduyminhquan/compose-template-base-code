package com.project.composeproject.ui.screen.home.model

import com.project.composeproject.domain.model.SourceType

data class HomeSourceCardUiModel(
    val id: Long,
    val sourceType: SourceType,
    val title: String,
    val subtitle: String,
    val channelCount: Int,
)
