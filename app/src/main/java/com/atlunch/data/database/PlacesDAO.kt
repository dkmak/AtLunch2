package com.atlunch.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlacesDAO {
    @Query("SELECT * FROM PlacePreviewEntity")
    suspend fun getPlacePreviews(): List<PlacePreviewEntity>

    @Query("DELETE FROM PlacePreviewEntity")
    suspend fun clearPlacePreviews()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacePreviews(placePreviews: List<PlacePreviewEntity>)
}
