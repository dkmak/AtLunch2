package com.atlunch.ui.listplaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atlunch.domain.LocationRepository
import com.atlunch.domain.LocationResult
import com.atlunch.domain.PlacePreview
import com.atlunch.domain.PlacesRepository
import com.atlunch.domain.PlacesResult
import com.atlunch.ui.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ListPlacesUiState {
    val isLocationPermissionEnabled: Boolean

    data class Success(
        val placesPreviews: List<PlacePreview>,
        override val isLocationPermissionEnabled: Boolean
    ) : ListPlacesUiState

    data class Failure(
        val message: String,
        override val isLocationPermissionEnabled: Boolean
    ) : ListPlacesUiState

    data class Loading(
        override val isLocationPermissionEnabled: Boolean
    ) : ListPlacesUiState
}

@HiltViewModel
class ListViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val placesRepository: PlacesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ListPlacesUiState>(
        ListPlacesUiState.Loading(isLocationPermissionEnabled = false)
    )
    val uiState = _uiState.asStateFlow()

    fun search(query: String) {
        if (query.isNotEmpty()) {
            placesRepository.searchQuery(query)
                .onEach { result ->
                    _uiState.update { currentState ->
                        result.toUiState(
                            isLocationPermissionEnabled = currentState.isLocationPermissionEnabled
                        )
                    }
                }.onStart {
                    _uiState.update { currentState ->
                        ListPlacesUiState.Loading(
                            isLocationPermissionEnabled = currentState.isLocationPermissionEnabled
                        )
                    }
                }.launchIn(viewModelScope)
        } else {
            loadPlacesNearby()
        }
    }

    private fun loadPlacesNearby() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                ListPlacesUiState.Loading(
                    isLocationPermissionEnabled = currentState.isLocationPermissionEnabled
                )
            }

            when (val locationResult = locationRepository.getCurrentLocation()) {
                is LocationResult.LocationSuccess -> {
                    val (latitude, longitude) = locationResult.userLocation
                    placesRepository.searchNearby(latitude, longitude)
                        .onEach { result ->
                            _uiState.update { currentState ->
                                result.toUiState(
                                    isLocationPermissionEnabled = currentState.isLocationPermissionEnabled
                                )
                            }
                        }.launchIn(viewModelScope)
                }

                is LocationResult.LocationError.Unknown -> {
                    _uiState.update { currentState ->
                        ListPlacesUiState.Failure(
                            message = locationResult.toUserMessage(),
                            currentState.isLocationPermissionEnabled
                        )
                    }
                }
            }
        }
    }

    fun onLocationPermissionChanged(isEnabled: Boolean) {
        _uiState.update { currentState ->
            when (currentState) {
                is ListPlacesUiState.Success ->
                    currentState.copy(isLocationPermissionEnabled = isEnabled)

                is ListPlacesUiState.Failure ->
                    currentState.copy(isLocationPermissionEnabled = isEnabled)

                is ListPlacesUiState.Loading ->
                    currentState.copy(isLocationPermissionEnabled = isEnabled)
            }
        }
    }



    private fun PlacesResult.toUiState(
        isLocationPermissionEnabled: Boolean
    ): ListPlacesUiState { // low level exceptions don't reach the high level abstractions like presentation layer
        return when (this) {
            is PlacesResult.PlacesSuccess -> ListPlacesUiState.Success(
                placesPreviews = this.places,
                isLocationPermissionEnabled = isLocationPermissionEnabled
            )

            is PlacesResult.PlacesError.Backend -> ListPlacesUiState.Failure(
                message = this.toUserMessage(),
                isLocationPermissionEnabled = isLocationPermissionEnabled
            )

            is PlacesResult.PlacesError.Network -> ListPlacesUiState.Failure(
                message = this.toUserMessage(),
                isLocationPermissionEnabled = isLocationPermissionEnabled
            )

            is PlacesResult.PlacesError.Unknown -> ListPlacesUiState.Failure(
                message = this.toUserMessage(),
                isLocationPermissionEnabled = isLocationPermissionEnabled
            )
        }
    }
}
