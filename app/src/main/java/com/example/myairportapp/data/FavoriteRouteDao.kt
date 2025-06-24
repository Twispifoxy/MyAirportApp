package com.example.myairportapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy

@Dao
interface FavoriteRouteDao {

    @Query("SELECT * FROM favorite")
    suspend fun getAllFavorites(): List<FavoriteRouteEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(route: FavoriteRouteEntity)

    @Delete
    suspend fun deleteFavorite(route: FavoriteRouteEntity)

    @Query("""
        SELECT * FROM favorite
        WHERE departure_code = :departure AND destination_code = :destination
        LIMIT 1
    """)
    suspend fun getFavoriteByCodes(departure: String, destination: String): FavoriteRouteEntity?
}