package com.project.composeproject.ui.screen.home.model

import com.project.composeproject.domain.model.SourceType

enum class HomePlaylistFilter(
    val sourceType: SourceType?,
) {
    ALL(sourceType = null),
    URL(sourceType = SourceType.URL),
    STREAM(sourceType = SourceType.STREAM),
    FILE(sourceType = SourceType.FILE),
    DEVICE(sourceType = SourceType.DEVICE),
}
