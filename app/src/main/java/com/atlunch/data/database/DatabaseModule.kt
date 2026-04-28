package com.atlunch.data.database

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): PlacesDatabase =
        Room
            .databaseBuilder(application, PlacesDatabase::class.java, "Places.db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun providePlacePreviewDAO(appDatabase: PlacesDatabase): PlacesDAO = appDatabase.placeDAO()
}
