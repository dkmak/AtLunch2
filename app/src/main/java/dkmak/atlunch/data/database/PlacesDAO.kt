package dkmak.atlunch.data.database

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

    @Query("SELECT * FROM FavoritesEntity")
    suspend fun getFavorites(): List<FavoritesEntity>

    @Query("DELETE FROM FavoritesEntity WHERE ID = :id ")
    suspend fun deleteFavorites(id: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: FavoritesEntity)
}
