package com.project.composeproject.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.project.composeproject.data.source.database.AppDatabase
import com.project.composeproject.data.source.database.dao.ChannelDao
import com.project.composeproject.data.source.database.dao.ChannelGroupDao
import com.project.composeproject.data.source.database.dao.ChannelSourceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.NAME,
        ).build()
    }

    @Provides
    fun provideChannelDao(database: AppDatabase): ChannelDao = database.channelDao()

    @Provides
    fun provideChannelGroupDao(database: AppDatabase): ChannelGroupDao = database.channelGroupDao()

    @Provides
    fun provideChannelSourceDao(database: AppDatabase): ChannelSourceDao = database.channelSourceDao()

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver = context.contentResolver
}
