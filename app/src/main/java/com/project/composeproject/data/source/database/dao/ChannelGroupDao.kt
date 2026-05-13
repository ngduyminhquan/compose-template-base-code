package com.project.composeproject.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.composeproject.data.source.database.entity.ChannelGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channelGroup: ChannelGroupEntity): Long

    @Update
    suspend fun update(channelGroup: ChannelGroupEntity)

    @Query("SELECT * FROM channel_groups WHERE id = :channelGroupId LIMIT 1")
    suspend fun getById(channelGroupId: Long): ChannelGroupEntity?

    @Query("DELETE FROM channel_groups WHERE id = :channelGroupId")
    suspend fun deleteById(channelGroupId: Long): Int

    @Query("SELECT * FROM channel_groups WHERE sourceId = :sourceId ORDER BY modifiedAt DESC, id ASC")
    fun observeBySourceId(sourceId: Long): Flow<List<ChannelGroupEntity>>
}
