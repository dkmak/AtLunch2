package com.atlunch.ui.placedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atlunch.domain.FavoriteResult
import com.atlunch.domain.Photo
import com.atlunch.domain.PlaceDetails
import com.atlunch.domain.PlaceDetailsResult
import com.atlunch.domain.PlacesRepository
import com.atlunch.domain.SummaryRepository
import com.atlunch.domain.SummaryResult
import com.atlunch.ui.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PlaceDetailsUIState(
    val placeDetailsDataState: PlacessDetailDataState = PlacessDetailDataState.Loading,
    val summary: String? = null
)

sealed interface PlacessDetailDataState {
    data class Success(
        val placeDetails: PlaceDetails,
        val photos: List<Photo>,
        val isFavorite: Boolean,
    ) : PlacessDetailDataState

    data class Failure(val message: String) : PlacessDetailDataState
    data object Loading : PlacessDetailDataState
}

@HiltViewModel
class PlaceDetailsViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val summaryRepository: SummaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaceDetailsUIState>(PlaceDetailsUIState())
    val uiState = _uiState.asStateFlow()

    fun loadDetails(id: String) {
        placesRepository.getPlaceDetails(id).onEach { result ->
            _uiState.update { currentState ->
                currentState.copy(placeDetailsDataState = result.toUiState())
            }
        }.launchIn(viewModelScope)

        summaryRepository.getSummary().onEach { result ->
            _uiState.update { currentState ->
                when (result) {
                    is SummaryResult.SummarySuccess -> currentState.copy(summary = result.summaryText)
                    else -> currentState.copy(summary = "Something went wrong.")
                }
            }
        }.launchIn(viewModelScope)
    }


    fun addFavorite() {
        val curr = uiState.value.placeDetailsDataState as? PlacessDetailDataState.Success
        curr?.placeDetails?.id.let { placeDetailsId ->
            placeDetailsId?.let { id ->
                placesRepository.addFavorite(id).onEach { result ->
                    _uiState.update { currentState ->
                        when (result) {
                            is FavoriteResult.FavoriteError -> {
                                currentState.copy(
                                    placeDetailsDataState = PlacessDetailDataState.Failure(
                                        message = result.toUserMessage()
                                    )
                                )
                            }

                            is FavoriteResult.FavoriteSuccess -> {
                                currentState.copy(
                                    placeDetailsDataState = curr?.copy(
                                        isFavorite = result.isFavorite
                                    ) ?: PlacessDetailDataState.Failure(
                                        message = FavoriteResult.FavoriteError.DatabaseError.toUserMessage()
                                    )
                                )
                            }
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun removeFavorites() {
        val curr = uiState.value.placeDetailsDataState as? PlacessDetailDataState.Success
        curr?.placeDetails?.id.let { placeDetailsId ->
            placeDetailsId?.let { id ->
                placesRepository.removeFavorite(id).onEach { result ->
                    _uiState.update { currentState ->
                        when (result) {
                            is FavoriteResult.FavoriteError -> {
                                currentState.copy(
                                    placeDetailsDataState = PlacessDetailDataState.Failure(
                                        message = result.toUserMessage()
                                    )
                                )
                            }

                            is FavoriteResult.FavoriteSuccess -> {
                                currentState.copy(
                                    placeDetailsDataState = curr?.copy(
                                        isFavorite = result.isFavorite
                                    ) ?: PlacessDetailDataState.Failure(
                                        message = FavoriteResult.FavoriteError.DatabaseError.toUserMessage()
                                    )
                                )
                            }
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun PlaceDetailsResult.toUiState(): PlacessDetailDataState {
        return when (this) {
            is PlaceDetailsResult.DetailsSuccess -> PlacessDetailDataState.Success(
                placeDetails = this.placeDetails,
                photos = this.photos,
                isFavorite = this.favorite
            )

            is PlaceDetailsResult.DetailsError.Backend -> PlacessDetailDataState.Failure(this.toUserMessage())
            is PlaceDetailsResult.DetailsError.Network -> PlacessDetailDataState.Failure(this.toUserMessage())
            is PlaceDetailsResult.DetailsError.Unknown -> PlacessDetailDataState.Failure(this.toUserMessage())
        }
    }

    fun onBackClicked() {
        _uiState.update { PlaceDetailsUIState() }
    }
}
