package com.example.exoplayer.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.example.exoplayer.data.local.MovieAppDao
import com.example.exoplayer.data.local.MovieAppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: MovieAppDatabase.Callback): MovieAppDatabase{
        return Room.databaseBuilder(application, MovieAppDatabase::class.java, "alpha_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideMovieAppDao(db: MovieAppDatabase): MovieAppDao {
        return db.getMovieAppDao()
    }
}