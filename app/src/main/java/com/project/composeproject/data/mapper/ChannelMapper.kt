package com.project.composeproject.data.mapper

import com.project.composeproject.data.source.database.entity.ChannelEntity
import com.project.composeproject.data.source.database.entity.ChannelGroupEntity
import com.project.composeproject.data.source.database.entity.ChannelSourceEntity
import com.project.composeproject.domain.model.Channel
import com.project.composeproject.domain.model.ChannelGroup
import com.project.composeproject.domain.model.ChannelSource
import com.project.composeproject.domain.model.SourceType

fun ChannelEntity.toDomain(): Channel {
    return Channel(
        id = id,
        groupId = groupId,
        name = name,
        url = url,
        logoUrl = logoUrl,
        isFavorited = isFavorited,
        modifiedAt = modifiedAt,
    )
}

fun Channel.toEntity(): ChannelEntity {
    return ChannelEntity(
        id = id,
        groupId = groupId,
        name = name,
        url = url,
        logoUrl = logoUrl,
        isFavorited = isFavorited,
        modifiedAt = modifiedAt,
    )
}

fun ChannelGroupEntity.toDomain(): ChannelGroup {
    return ChannelGroup(
        id = id,
        sourceId = sourceId,
        name = name,
        modifiedAt = modifiedAt,
    )
}

fun ChannelGroup.toEntity(): ChannelGroupEntity {
    return ChannelGroupEntity(
        id = id,
        sourceId = sourceId,
        name = name,
        modifiedAt = modifiedAt,
    )
}

fun ChannelSourceEntity.toDomain(): ChannelSource {
    return ChannelSource(
        id = id,
        name = name,
        sourceType = SourceType.valueOf(sourceType),
        addedAt = addedAt,
        modifiedAt = modifiedAt,
    )
}

fun ChannelSource.toEntity(): ChannelSourceEntity {
    return ChannelSourceEntity(
        id = id,
        name = name,
        sourceType = sourceType.name,
        addedAt = addedAt,
        modifiedAt = modifiedAt,
    )
}
