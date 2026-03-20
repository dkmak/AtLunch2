package com.atlunch.ui.listplaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atlunch.domain.LocationRepository
import com.atlunch.domain.LocationResult
import com.atlunch.domain.PlacePreview
import com.atlunch.domain.PlacesRepository
import com.atlunch.domain.PlacesResult
import com.atlunch.domain.Location
import com.atlunch.ui.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
    val location: Location? = null
) {
    sealed interface DataState {
        data class Success(val placesPreviews: List<PlacePreview>) : DataState
        data class Failure(val message: String) : DataState
        data object Loading : DataState
    }
}

@HiltViewModel
class ListViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val placesRepository: PlacesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListPlacesUiState())
    val uiState = _uiState.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(dataState = ListPlacesUiState.DataState.Loading)
            }

            when (val locationResult = locationRepository.getCurrentLocation()) {
                is LocationResult.LocationSuccess -> {
                    val userLocation = locationResult.location
                    _uiState.update { currentState ->
                        currentState.copy(location = userLocation)
                    }

                    val placesFlow = if (query.isNotBlank()) {
                        placesRepository.searchQuery(
                            query,
                            userLocation.latitude,
                            userLocation.longitude
                        )
                    } else {
                        placesRepository.searchNearby(
                            userLocation.latitude,
                            userLocation.longitude
                        )
                    }

                    placesFlow.renderPlacesState()
                }

                is LocationResult.LocationError.Unknown -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            location = null,
                            dataState = ListPlacesUiState.DataState.Failure(
                                message = locationResult.toUserMessage()
                            )
                        )
                    }
                }
            }
        }
    }

    fun onLocationPermissionChanged(isEnabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLocationPermissionEnabled = isEnabled,
                location = if (isEnabled) currentState.location else null
            )
        }
        if (isEnabled) {
            search("")
        }
    }

    private fun Flow<PlacesResult>.renderPlacesState() {
        onEach { placesResult ->
            _uiState.update { currentState ->
                currentState.copy(dataState = placesResult.toDataState())
            }
        }.launchIn(viewModelScope)
    }

    private fun PlacesResult.toDataState(): ListPlacesUiState.DataState {
        return when (this) {
            is PlacesResult.PlacesSuccess -> ListPlacesUiState.DataState.Success(
                placesPreviews = this.places
            )

            is PlacesResult.PlacesError.Backend -> ListPlacesUiState.DataState.Failure(
                message = this.toUserMessage()
            )

            is PlacesResult.PlacesError.Network -> ListPlacesUiState.DataState.Failure(
                message = this.toUserMessage()
            )

            is PlacesResult.PlacesError.Unknown -> ListPlacesUiState.DataState.Failure(
                message = this.toUserMessage()
            )
        }
    }
}
