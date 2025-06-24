package com.example.myairportapp.repository

import com.example.myairportapp.data.*
import android.content.Context
import kotlinx.coroutines.flow.Flow

class FlightRepository(
    private val airportDao: AirportDao,
    private val favoriteRouteDao: FavoriteRouteDao,
    private val preferencesManager: PreferencesManager
) {

    val searchQueryFlow: Flow<String> = preferencesManager.searchQueryFlow

    suspend fun saveSearchQuery(query: String) {
        preferencesManager.saveSearchQuery(query)
    }

    suspend fun searchAirports(query: String?): List<AirportEntity> = airportDao.searchAirports("%$query%")

    suspend fun getAllAirportsSorted(): List<AirportEntity> = airportDao.getAllAirportsSorted()
    suspend fun getAirportByCode(iataCode: String): AirportEntity? = airportDao.getAirportByCode(iataCode)

    suspend fun getAllFavorites(): List<FavoriteRouteEntity> = favoriteRouteDao.getAllFavorites()
    suspend fun insertFavorite(favorite: FavoriteRouteEntity) = favoriteRouteDao.insertFavorite(favorite)
    suspend fun deleteFavorite(favorite: FavoriteRouteEntity) = favoriteRouteDao.deleteFavorite(favorite)

    suspend fun getFavorite(departure: String, destination: String): FavoriteRouteEntity? =
        favoriteRouteDao.getFavoriteByCodes(departure, destination)

    companion object {
        fun create(context: Context): FlightRepository {
            val db = FlightSearchDatabase.getDatabase(context)
            return FlightRepository(
                airportDao = db.airportDao(),
                favoriteRouteDao = db.favoriteRouteDao(),
                preferencesManager = PreferencesManager(context)
            )
        }
    }
}