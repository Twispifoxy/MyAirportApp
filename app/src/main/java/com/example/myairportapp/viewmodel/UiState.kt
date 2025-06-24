package com.example.myairportapp.viewmodel

import com.example.myairportapp.data.AirportEntity
import com.example.myairportapp.data.FavoriteRouteEntity

sealed class UiState {
    object Empty : UiState()

    data class Suggestions(val airports: List<AirportEntity>) : UiState()

    data class Flights(
        val departure: AirportEntity,
        val availableDestinations: List<AirportEntity>,
        val favorites: List<FavoriteRouteEntity>
    ) : UiState()

    data class FavoriteWithAirports(
        val favoriteRoute: FavoriteRouteEntity,
        val departureAirport: AirportEntity?,
        val destinationAirport: AirportEntity?
    )

    data class Favorites(val favorites: List<FavoriteWithAirports>) : UiState()
}