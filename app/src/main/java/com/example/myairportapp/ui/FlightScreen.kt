package com.example.myairportapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myairportapp.data.AirportEntity
import com.example.myairportapp.viewmodel.UiState
import com.example.myairportapp.viewmodel.FlightViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightScreen(viewModel: FlightViewModel, modifier: Modifier = Modifier) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Enter departure airport") },
            singleLine = true,
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp)
        )

        when (uiState) {
            is UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        "Enter airport name or IATA code",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
            is UiState.Suggestions -> {
                val airports = (uiState as UiState.Suggestions).airports
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(airports) { airport ->
                        SuggestionItem(airport) {
                            viewModel.selectAirport(airport)
                            focusManager.clearFocus()
                        }
                    }
                }
            }
            is UiState.Flights -> {
                val state = uiState as UiState.Flights
                Text(
                    "Flights from ${state.departure.iataCode}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(state.availableDestinations) { dest ->
                        val isFavorite = state.favorites.any { it.toCode == dest.iataCode }
                        val toggleFavorite = {
                            if (isFavorite) {
                                val fav = state.favorites.first { it.toCode == dest.iataCode }
                                viewModel.removeFavorite(fav, state.departure)
                            } else {
                                viewModel.addFavorite(state.departure, dest)
                            }
                        }
                        RouteCard(
                            departureCode = state.departure.iataCode,
                            departureName = state.departure.name,
                            destinationCode = dest.iataCode,
                            destinationName = dest.name,
                            isFavorite = isFavorite,
                            onToggleFavorite = toggleFavorite,
                            onClick = {},
                            onLongClick = toggleFavorite
                        )
                    }
                }
            }
            is UiState.Favorites -> {
                val favs = (uiState as UiState.Favorites).favorites
                Text(
                    "Favorite routes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                if (favs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            "No favorites yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(bottom = 12.dp)) {
                        items(favs) { favWithAirports ->
                            RouteCard(
                                departureCode = favWithAirports.favoriteRoute.fromCode,
                                departureName = favWithAirports.departureAirport?.name,
                                destinationCode = favWithAirports.favoriteRoute.toCode,
                                destinationName = favWithAirports.destinationAirport?.name,
                                isFavorite = true,
                                onToggleFavorite = null,
                                onClick = {},
                                onLongClick = {
                                    viewModel.removeFavorite(
                                        favWithAirports.favoriteRoute,
                                        favWithAirports.departureAirport ?: return@RouteCard,
                                        fromFavoritesScreen = true
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionItem(airport: AirportEntity, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                airport.iataCode,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(56.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                airport.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RouteCard(
    departureCode: String,
    departureName: String?,
    destinationCode: String,
    destinationName: String?,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(14.dp)

    val bgColor by animateColorAsState(
        targetValue = if (isFavorite) MaterialTheme.colorScheme.secondary // светло-желтый фон
        else MaterialTheme.colorScheme.surface
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (isFavorite) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(
                    onClick = onClick ?: {},
                    onLongClick = onLongClick ?: {},
                    indication = rememberRipple(bounded = true, radius = 300.dp, color = MaterialTheme.colorScheme.primary),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .clip(shape)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "DEPART",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "$departureCode  ${departureName.orEmpty()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "ARRIVE",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "$destinationCode  ${destinationName.orEmpty()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (onToggleFavorite != null) {
                IconButton(onClick = onToggleFavorite) {
                    if (isFavorite) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Favorite",
                            tint = Color(0xFFFFA000)
                        )
                    } else {
                        Icon(
                            Icons.Outlined.Star,
                            contentDescription = "Not favorite",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else if (isFavorite) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Favorite",
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
