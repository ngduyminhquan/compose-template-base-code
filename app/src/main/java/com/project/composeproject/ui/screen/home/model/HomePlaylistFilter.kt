package com.project.composeproject.ui.screen.home.model

import com.project.composeproject.domain.model.SourceType

enum class HomePlaylistFilter(val sourceType: SourceType?) {
    ALL(null),
    URL(SourceType.URL),
    STREAM(SourceType.STREAM),
    FILE(SourceType.FILE),
    DEVICE(SourceType.DEVICE),
}
