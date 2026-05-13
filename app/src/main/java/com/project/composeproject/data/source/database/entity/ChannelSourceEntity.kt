package com.project.composeproject.data.source.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel_sources")
data class ChannelSourceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val sourceType: String,

    val addedAt: Long,
    val modifiedAt: Long
)
