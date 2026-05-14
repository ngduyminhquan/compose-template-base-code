package com.project.composeproject.data.repository

import android.net.Uri
import android.util.Log
import androidx.room.withTransaction
import com.project.composeproject.data.core.m3u.M3uChannel
import com.project.composeproject.data.mapper.toDomain
import com.project.composeproject.data.source.database.AppDatabase
import com.project.composeproject.data.source.database.dao.ChannelDao
import com.project.composeproject.data.source.database.dao.ChannelGroupDao
import com.project.composeproject.data.source.database.dao.ChannelSourceDao
import com.project.composeproject.data.source.database.entity.ChannelEntity
import com.project.composeproject.data.source.database.entity.ChannelGroupEntity
import com.project.composeproject.data.source.database.entity.ChannelSourceEntity
import com.project.composeproject.data.source.network.M3uPlaylistNetworkSource
import com.project.composeproject.data.source.storage.M3uPlaylistStorageSource
import com.project.composeproject.data.utils.now
import com.project.composeproject.data.utils.toDataResult
import com.project.composeproject.data.utils.toException
import com.project.composeproject.data.utils.toUnitDataResult
import com.project.composeproject.domain.model.Channel
import com.project.composeproject.domain.model.ChannelGroup
import com.project.composeproject.domain.model.ChannelSource
import com.project.composeproject.domain.model.DataResult
import com.project.composeproject.domain.model.SourceType
import com.project.composeproject.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val channelDao: ChannelDao,
    private val channelGroupDao: ChannelGroupDao,
    private val channelSourceDao: ChannelSourceDao,
    private val m3uPlaylistNetworkSource: M3uPlaylistNetworkSource,
    private val m3uPlaylistStorageSource: M3uPlaylistStorageSource,
) : ChannelRepository {

    override fun observeChannels(groupId: Long): Flow<DataResult<List<Channel>>> {
        return channelDao.observeByGroupId(groupId)
            .map { channels -> DataResult.Success(channels.map { it.toDomain() }) as DataResult<List<Channel>> }
            .onStart { emit(DataResult.Loading) }
            .catch { emit(DataResult.Error(it.toException())) }
    }

    override fun observeAllChannels(): Flow<DataResult<List<Channel>>> {
        return channelDao.observeAll()
            .map { channels -> DataResult.Success(channels.map { it.toDomain() }) as DataResult<List<Channel>> }
            .onStart { emit(DataResult.Loading) }
            .catch { emit(DataResult.Error(it.toException())) }
    }

    override fun observeFavoriteChannels(): Flow<DataResult<List<Channel>>> {
        return channelDao.observeFavorites()
            .map { channels -> DataResult.Success(channels.map { it.toDomain() }) as DataResult<List<Channel>> }
            .onStart { emit(DataResult.Loading) }
            .catch { emit(DataResult.Error(it.toException())) }
    }

    override suspend fun favoriteChannel(channelId: Long): DataResult<Unit> {
        return updateChannel(channelId) { channel ->
            channel.copy(
                isFavorited = true,
                modifiedAt = now(),
            )
        }
    }

    override suspend fun unfavoriteChannel(channelId: Long): DataResult<Unit> {
        return updateChannel(channelId) { channel ->
            channel.copy(
                isFavorited = false,
                modifiedAt = now(),
            )
        }
    }

    override suspend fun deleteChannel(channelId: Long): DataResult<Unit> {
        return runCatching {
            require(channelDao.deleteById(channelId) > 0) { "Channel $channelId not found" }
        }.toUnitDataResult()
    }

    override suspend fun renameChannel(channelId: Long, name: String): DataResult<Unit> {
        return updateChannel(channelId) { channel ->
            channel.copy(
                name = name,
                modifiedAt = now(),
            )
        }
    }


    override fun observeChannelGroups(sourceId: Long): Flow<DataResult<List<ChannelGroup>>> {
        return channelGroupDao.observeBySourceId(sourceId)
            .map { channelGroups -> DataResult.Success(channelGroups.map { it.toDomain() }) as DataResult<List<ChannelGroup>> }
            .onStart { emit(DataResult.Loading) }
            .catch { emit(DataResult.Error(it.toException())) }
    }

    override suspend fun deleteChannelGroup(channelGroupId: Long): DataResult<Unit> {
        return runCatching {
            require(channelGroupDao.deleteById(channelGroupId) > 0) { "ChannelGroup $channelGroupId not found" }
        }.toUnitDataResult()
    }


    override fun observeChannelSources(): Flow<DataResult<List<ChannelSource>>> {
        return channelSourceDao.observeAll()
            .map { channelSources -> DataResult.Success(channelSources.map { it.toDomain() }) as DataResult<List<ChannelSource>> }
            .onStart { emit(DataResult.Loading) }
            .catch { emit(DataResult.Error(it.toException())) }
    }

    override suspend fun createChannelSourceFromUrl(url: String): DataResult<Long> {
        return runCatching {
            val parsedChannels = m3uPlaylistNetworkSource.fetchChannels(url)
            createChannelSourceWithChannels(
                name = url.toChannelSourceName(Uri.parse(url)),
                sourceType = SourceType.URL,
                parsedChannels = parsedChannels,
            )
        }.toDataResult()
    }

    override suspend fun createChannelSourceFromFile(uri: Uri): DataResult<Long> {
        return runCatching {
            val parsedChannels = m3uPlaylistStorageSource.fetchChannels(uri)
            createChannelSourceWithChannels(
                name = uri.path?.toChannelSourceName(uri) ?: DEFAULT_SOURCE_NAME,
                sourceType = SourceType.FILE,
                parsedChannels = parsedChannels,
            )
        }.toDataResult()
    }

    override suspend fun deleteChannelSource(channelSourceId: Long): DataResult<Unit> {
        return runCatching {
            require(channelSourceDao.deleteById(channelSourceId) > 0) { "ChannelSource $channelSourceId not found" }
        }.toUnitDataResult()
    }

    override suspend fun renameChannelSource(
        channelSourceId: Long,
        name: String
    ): DataResult<Unit> {
        return runCatching {
            val channelSource = requireNotNull(channelSourceDao.getById(channelSourceId)) {
                "ChannelSource $channelSourceId not found"
            }
            channelSourceDao.update(
                channelSource.copy(
                    name = name,
                    modifiedAt = now(),
                ),
            )
        }.toUnitDataResult()
    }

    override fun observeChannelSourcesWithCount(): Flow<DataResult<Map<ChannelSource, Int>>> {
        val sourcesFlow = channelSourceDao.observeAll()
        val groupsFlow = channelGroupDao.observeAll()
        val channelsFlow = channelDao.observeAll()
        return kotlinx.coroutines.flow.combine(sourcesFlow, groupsFlow, channelsFlow) { sources, groups, channels ->
            val mappedSources = sources.map { it.toDomain() }
            val groupSourceMap = groups.associate { it.id to it.sourceId }
            val channelsBySourceId = channels.groupBy { groupSourceMap[it.groupId] }
            val resultMap = mappedSources.associateWith { source ->
                channelsBySourceId[source.id]?.size ?: 0
            }
            DataResult.Success(resultMap) as DataResult<Map<ChannelSource, Int>>
        }.onStart { emit(DataResult.Loading) }
         .catch { emit(DataResult.Error(it.toException())) }
    }

    override fun observeAllChannelGroupsWithChannels(): Flow<DataResult<Map<ChannelGroup, List<Channel>>>> {
        val groupsFlow = channelGroupDao.observeAll()
        val channelsFlow = channelDao.observeAll()
        return kotlinx.coroutines.flow.combine(groupsFlow, channelsFlow) { groups, channels ->
            val mappedGroups = groups.map { it.toDomain() }
            val mappedChannels = channels.map { it.toDomain() }
            val channelsByGroupId = mappedChannels.groupBy { it.groupId }
            val resultMap = mappedGroups.associateWith { group ->
                channelsByGroupId[group.id] ?: emptyList()
            }
            DataResult.Success(resultMap) as DataResult<Map<ChannelGroup, List<Channel>>>
        }.onStart { emit(DataResult.Loading) }
         .catch { emit(DataResult.Error(it.toException())) }
    }


    private suspend fun updateChannel(
        channelId: Long,
        transform: (ChannelEntity) -> ChannelEntity,
    ): DataResult<Unit> {
        return runCatching {
            val channel = requireNotNull(channelDao.getById(channelId)) {
                "Channel $channelId not found"
            }
            channelDao.update(transform(channel))
        }.toUnitDataResult()
    }

    private suspend fun createChannelSourceWithChannels(
        name: String,
        sourceType: SourceType,
        parsedChannels: List<M3uChannel>,
    ): Long {
        require(parsedChannels.isNotEmpty()) { "No channels found in source" }

        val now = now()
        return appDatabase.withTransaction {
            val sourceId = channelSourceDao.insert(
                ChannelSourceEntity(
                    name = name,
                    sourceType = sourceType.name,
                    addedAt = now,
                    modifiedAt = now,
                )
            )

            val groupNames = parsedChannels
                .map { it.groupTitle.ifBlank { DEFAULT_GROUP_NAME } }
                .distinct()
            val groupIds = channelGroupDao.insertAll(
                groupNames.map { groupName ->
                    ChannelGroupEntity(
                        sourceId = sourceId,
                        name = groupName,
                        modifiedAt = now,
                    )
                }
            )
            val groupIdByName = groupNames.zip(groupIds).toMap()

            channelDao.insertAll(
                parsedChannels.map { parsedChannel ->
                    ChannelEntity(
                        groupId = groupIdByName.getValue(parsedChannel.groupTitle.ifBlank { DEFAULT_GROUP_NAME }),
                        name = parsedChannel.name,
                        url = parsedChannel.url,
                        logoUrl = parsedChannel.logo,
                        isFavorited = false,
                        modifiedAt = now,
                    )
                }
            )

            sourceId
        }
    }

    private fun String.toChannelSourceName(uri: Uri): String {
        return uri.lastPathSegment
            ?.substringAfterLast('/')
            ?.takeIf { it.isNotBlank() }
            ?: takeIf { it.isNotBlank() }
            ?: DEFAULT_SOURCE_NAME
    }

    private companion object {
        const val DEFAULT_GROUP_NAME = "Ungrouped"
        const val DEFAULT_SOURCE_NAME = "M3U Source"
    }
}
