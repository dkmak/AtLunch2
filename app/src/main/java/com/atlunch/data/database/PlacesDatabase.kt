package com.atlunch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlacePreviewEntity::class],
    version = 1,
    exportSchema = true
)
abstract class PlacesDatabase: RoomDatabase() {
    abstract fun placeDAO(): PlacesDAO
}