package com.example.myairportapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myairportapp.data.AirportEntity
import com.example.myairportapp.data.FavoriteRouteEntity
import com.example.myairportapp.repository.FlightRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlightViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FlightRepository.create(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        repository.searchQueryFlow
            .filter { it.isNotBlank() }
            .onEach { setSearchQuery(it, save = false) }
            .launchIn(viewModelScope)
    }

    fun setSearchQuery(query: String, save: Boolean = true) {
        _searchQuery.value = query

        if (save) {
            viewModelScope.launch {
                repository.saveSearchQuery(query)
            }
        }

        if (query.isBlank()) {
            loadFavorites()
        } else {
            viewModelScope.launch {
                val suggestions = repository.searchAirports(query)
                _uiState.value = UiState.Suggestions(suggestions)
            }
        }
    }

    fun selectAirport(airport: AirportEntity) {
        viewModelScope.launch {
            val allAirports = repository.getAllAirportsSorted().filter { it.iataCode != airport.iataCode }

            val favorites = repository.getAllFavorites().filter { it.fromCode == airport.iataCode }

            _uiState.value = UiState.Flights(airport, allAirports, favorites)
        }
    }

    fun addFavorite(departure: AirportEntity, destination: AirportEntity) {
        viewModelScope.launch {
            val existing = repository.getFavorite(departure.iataCode, destination.iataCode)
            if (existing == null) {
                val favorite = FavoriteRouteEntity(
                    fromCode = departure.iataCode,
                    toCode = destination.iataCode
                )
                repository.insertFavorite(favorite)
            }
            selectAirport(departure)
        }
    }

    fun removeFavorite(favorite: FavoriteRouteEntity, departure: AirportEntity, fromFavoritesScreen: Boolean = false) {
        viewModelScope.launch {
            repository.deleteFavorite(favorite)
            if (fromFavoritesScreen) {
                loadFavorites()
            } else {
                selectAirport(departure)
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            val favorites = repository.getAllFavorites()
            val favoritesWithAirports = favorites.mapNotNull { fav ->
                val dep = repository.getAirportByCode(fav.fromCode)
                val dest = repository.getAirportByCode(fav.toCode)
                if (dep != null && dest != null) {
                    UiState.FavoriteWithAirports(
                        favoriteRoute = fav,
                        departureAirport = dep,
                        destinationAirport = dest
                    )
                } else null
            }
            _uiState.value = UiState.Favorites(favoritesWithAirports)
        }
    }
}