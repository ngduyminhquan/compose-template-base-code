package com.project.composeproject.data.repository

import com.project.composeproject.data.mapper.toDomain
import com.project.composeproject.data.source.database.dao.ChannelDao
import com.project.composeproject.data.source.database.dao.ChannelGroupDao
import com.project.composeproject.data.source.database.dao.ChannelSourceDao
import com.project.composeproject.data.source.database.entity.ChannelEntity
import com.project.composeproject.data.source.database.entity.ChannelSourceEntity
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
    private val channelDao: ChannelDao,
    private val channelGroupDao: ChannelGroupDao,
    private val channelSourceDao: ChannelSourceDao,
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

    override suspend fun createChannelSource(
        name: String,
        sourceType: SourceType,
    ): DataResult<Long> {
        return runCatching {
            val now = now()
            channelSourceDao.insert(
                ChannelSourceEntity(
                    name = name,
                    sourceType = sourceType.name,
                    addedAt = now,
                    modifiedAt = now,
                ),
            )
        }.toDataResult()
    }

    override suspend fun deleteChannelSource(channelSourceId: Long): DataResult<Unit> {
        return runCatching {
            require(channelSourceDao.deleteById(channelSourceId) > 0) { "ChannelSource $channelSourceId not found" }
        }.toUnitDataResult()
    }

    override suspend fun renameChannelSource(channelSourceId: Long, name: String): DataResult<Unit> {
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
}
