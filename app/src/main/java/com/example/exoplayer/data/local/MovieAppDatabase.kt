package com.example.exoplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exoplayer.data.model.Movie
import com.example.exoplayer.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Movie::class], version = 1)
abstract class MovieAppDatabase : RoomDatabase() {

    abstract fun getMovieAppDao(): MovieAppDao

    class Callback @Inject constructor(
        private val database: Provider<MovieAppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}