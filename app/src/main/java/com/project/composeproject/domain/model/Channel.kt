package com.project.composeproject.domain.model

data class Channel(
    val id: Long,
    val groupId: Long,
    val name: String,
    val url: String,
    val logoUrl: String,
    val isFavorited: Boolean,
    val modifiedAt: Long,
)
