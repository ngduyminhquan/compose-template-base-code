package com.project.composeproject.domain.model

data class ChannelSource(
    val id: Long,
    val name: String,
    val sourceType: SourceType,
    val addedAt: Long,
    val modifiedAt: Long,
)
