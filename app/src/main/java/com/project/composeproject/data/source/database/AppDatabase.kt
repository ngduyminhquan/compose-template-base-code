package com.project.composeproject.data.source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.composeproject.data.source.database.dao.ChannelDao
import com.project.composeproject.data.source.database.dao.ChannelGroupDao
import com.project.composeproject.data.source.database.dao.ChannelSourceDao
import com.project.composeproject.data.source.database.entity.ChannelEntity
import com.project.composeproject.data.source.database.entity.ChannelGroupEntity
import com.project.composeproject.data.source.database.entity.ChannelSourceEntity

@Database(
    entities = [
        ChannelEntity::class,
        ChannelGroupEntity::class,
        ChannelSourceEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun channelDao(): ChannelDao

    abstract fun channelGroupDao(): ChannelGroupDao

    abstract fun channelSourceDao(): ChannelSourceDao

    companion object {
        const val NAME = "app_database"
    }
}
