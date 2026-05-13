package com.project.composeproject.data.source.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.composeproject.data.source.database.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: ChannelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<ChannelEntity>): List<Long>

    @Update
    suspend fun update(channel: ChannelEntity)

    @Query("SELECT * FROM channels WHERE id = :channelId LIMIT 1")
    suspend fun getById(channelId: Long): ChannelEntity?

    @Query("DELETE FROM channels WHERE id = :channelId")
    suspend fun deleteById(channelId: Long): Int

    @Query("SELECT * FROM channels WHERE groupId = :groupId ORDER BY id ASC")
    fun observeByGroupId(groupId: Long): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels ORDER BY modifiedAt DESC, id ASC")
    fun observeAll(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE isFavorited = 1 ORDER BY modifiedAt DESC, id ASC")
    fun observeFavorites(): Flow<List<ChannelEntity>>
}
