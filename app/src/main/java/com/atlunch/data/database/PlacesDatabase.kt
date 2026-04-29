package com.atlunch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlacePreviewEntity::class,
        FavoritesEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class PlacesDatabase : RoomDatabase() {
    abstract fun placeDAO(): PlacesDAO
}
