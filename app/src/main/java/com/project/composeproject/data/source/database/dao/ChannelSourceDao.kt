package com.project.composeproject.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.composeproject.data.source.database.entity.ChannelSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelSourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channelSource: ChannelSourceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channelSources: List<ChannelSourceEntity>): List<Long>

    @Update
    suspend fun update(channelSource: ChannelSourceEntity)

    @Query("SELECT * FROM channel_sources WHERE id = :channelSourceId LIMIT 1")
    suspend fun getById(channelSourceId: Long): ChannelSourceEntity?

    @Query("DELETE FROM channel_sources WHERE id = :channelSourceId")
    suspend fun deleteById(channelSourceId: Long): Int

    @Query("SELECT * FROM channel_sources ORDER BY modifiedAt DESC, id ASC")
    fun observeAll(): Flow<List<ChannelSourceEntity>>

    @Query("SELECT * FROM channel_sources WHERE sourceType = :sourceType ORDER BY modifiedAt DESC, id ASC")
    fun observeByType(sourceType: String): Flow<List<ChannelSourceEntity>>
}
