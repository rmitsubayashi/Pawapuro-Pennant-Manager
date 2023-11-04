package com.rmitsubayashi.pennantmanager.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.rmitsubayashi.pennantmanager.data.repository.RoomDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("main_shared_prefs", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, RoomDB::class.java, "room_db").build()

    @Singleton
    @Provides
    fun providePlayerDao(db: RoomDB) = db.playerDao()

    @Singleton
    @Provides
    fun provideSaveFileDao(db: RoomDB) = db.saveFileDao()

    @Singleton
    @Provides
    fun provideNoteDao(db: RoomDB) = db.noteDao()
}