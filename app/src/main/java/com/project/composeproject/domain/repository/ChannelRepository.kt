package com.project.composeproject.domain.repository

import android.net.Uri
import com.project.composeproject.domain.model.Channel
import com.project.composeproject.domain.model.ChannelGroup
import com.project.composeproject.domain.model.ChannelSource
import com.project.composeproject.domain.model.DataResult
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {


    fun observeChannels(groupId: Long): Flow<DataResult<List<Channel>>>

    fun observeAllChannels(): Flow<DataResult<List<Channel>>>

    fun observeFavoriteChannels(): Flow<DataResult<List<Channel>>>

    fun observeRecentChannels(limit: Int = 10): Flow<DataResult<List<Channel>>>

    suspend fun deleteChannel(channelId: Long): DataResult<Unit>

    suspend fun renameChannel(
        channelId: Long,
        name: String,
    ): DataResult<Unit>

    suspend fun favoriteChannel(channelId: Long): DataResult<Unit>

    suspend fun unfavoriteChannel(channelId: Long): DataResult<Unit>


    fun observeChannelGroups(sourceId: Long): Flow<DataResult<List<ChannelGroup>>>

    suspend fun deleteChannelGroup(channelGroupId: Long): DataResult<Unit>


    fun observeChannelSources(): Flow<DataResult<List<ChannelSource>>>

    suspend fun createChannelSourceFromUrl(url: String): DataResult<Long>

    suspend fun createChannelSourceFromFile(uri: Uri): DataResult<Long>

    suspend fun deleteChannelSource(channelSourceId: Long): DataResult<Unit>

    suspend fun renameChannelSource(
        channelSourceId: Long,
        name: String,
    ): DataResult<Unit>

    fun observeChannelSourcesWithCount(): Flow<DataResult<Map<ChannelSource, Int>>>

    fun observeAllChannelGroupsWithChannels(): Flow<DataResult<Map<ChannelGroup, List<Channel>>>>
}
