package dkmak.atlunch.ui.listplaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dkmak.atlunch.domain.Location
import dkmak.atlunch.domain.LocationRepository
import dkmak.atlunch.domain.LocationResult
import dkmak.atlunch.domain.PlacePreview
import dkmak.atlunch.domain.PlacesRepository
import dkmak.atlunch.domain.PlacesResult
import dkmak.atlunch.ui.toUserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListPlacesUiState(
    val isLocationPermissionEnabled: Boolean = false,
    val dataState: DataState = DataState.Loading,
    val location: Location? = null,
) {
    sealed interface DataState {
        data class Success(
            val placesPreviews: List<PlacePreview>,
        ) : DataState

        data class Failure(
            val message: String,
        ) : DataState

        data object Loading : DataState
    }
}

@HiltViewModel
class ListPlacesViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val placesRepository: PlacesRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(ListPlacesUiState())
        val uiState = _uiState.asStateFlow()
        private var activeSearchJob: Job? = null
        private var hasLoadedInitialNearbyPlaces = false

        fun onLocationPermissionChanged(isEnabled: Boolean) {
            if (uiState.value.isLocationPermissionEnabled == isEnabled) return

            _uiState.update { currentState ->
                currentState.copy(
                    isLocationPermissionEnabled = isEnabled,
                    location = if (isEnabled) currentState.location else null,
                )
            }
            if (!isEnabled) {
                activeSearchJob?.cancel()
                activeSearchJob = null
                hasLoadedInitialNearbyPlaces = false
            }
        }

        fun loadInitialNearbyPlaces() {
            if (hasLoadedInitialNearbyPlaces) return

            if (uiState.value.isLocationPermissionEnabled) {
                hasLoadedInitialNearbyPlaces = true
                updateUserLocation()
            }
        }

        fun search(query: String) {
            viewModelScope.launch {
                when (val userLocation = uiState.value.location ?: getCurrentLocation()) {
                    is Location -> {
                        performSearch(query = query, location = userLocation)
                    }
                    is LocationResult.LocationError.Unknown -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                dataState =
                                    ListPlacesUiState.DataState.Failure(
                                        userLocation.toUserMessage(),
                                    ),
                            )
                        }
                    }
                    is LocationResult.LocationSuccess -> {
                        val location = userLocation.location
                        _uiState.update { currentState ->
                            currentState.copy(
                                location = location,
                            )
                        }
                        performSearch(query = query, location = location)
                    }
                }
            }
        }

        private fun performSearch(
            query: String,
            location: Location,
        ) {
            if (query.isNotBlank()) {
                activeSearchJob?.cancel()
                activeSearchJob =
                    placesRepository
                        .searchQuery(
                            query,
                            location.latitude,
                            location.longitude,
                        ).onEach { result ->
                            _uiState.update { currentState ->
                                currentState.copy(dataState = result.toDataState())
                            }
                        }.onStart {
                            _uiState.update { currentState ->
                                currentState.copy(dataState = ListPlacesUiState.DataState.Loading)
                            }
                        }.launchIn(viewModelScope)
            } else {
                loadPlacesNearby(userLocation = location)
            }
        }

        private fun loadPlacesNearby(userLocation: Location) {
            activeSearchJob?.cancel()
            activeSearchJob =
                placesRepository
                    .searchNearby(userLocation.latitude, userLocation.longitude)
                    .onStart {
                        _uiState.update { currentState ->
                            currentState.copy(dataState = ListPlacesUiState.DataState.Loading)
                        }
                    }.onEach { placesResult ->
                        _uiState.update { currentState ->
                            currentState.copy(dataState = placesResult.toDataState())
                        }
                    }.launchIn(viewModelScope)
        }

        private fun updateUserLocation() {
            viewModelScope.launch {
                when (val locationResult = locationRepository.getCurrentLocation()) {
                    is LocationResult.LocationSuccess -> {
                        val location = locationResult.location
                        _uiState.update { currentState ->
                            currentState.copy(location = locationResult.location)
                        }
                        loadPlacesNearby(location)
                    }

                    is LocationResult.LocationError.Unknown -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                location = null,
                                dataState =
                                    ListPlacesUiState.DataState.Failure(
                                        message = locationResult.toUserMessage(),
                                    ),
                            )
                        }
                    }
                }
            }
        }

        private suspend fun getCurrentLocation() = locationRepository.getCurrentLocation()

        private fun PlacesResult.toDataState(): ListPlacesUiState.DataState =
            when (this) {
                is PlacesResult.PlacesSuccess ->
                    ListPlacesUiState.DataState.Success(
                        placesPreviews = this.places,
                    )

                is PlacesResult.PlacesError.Backend ->
                    ListPlacesUiState.DataState.Failure(
                        message = this.toUserMessage(),
                    )

                is PlacesResult.PlacesError.Network ->
                    ListPlacesUiState.DataState.Failure(
                        message = this.toUserMessage(),
                    )

                is PlacesResult.PlacesError.Unknown ->
                    ListPlacesUiState.DataState.Failure(
                        message = this.toUserMessage(),
                    )
            }
    }
