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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PlaceDetailsUIState(
    val placeDetailsDataState: PlacesDetailDataState = PlacesDetailDataState.Loading,
    val summaryDataState: PlacesDetailSummaryDataState? = null
)

sealed interface PlacesDetailDataState {
    data class Success(
        val placeDetails: PlaceDetails,
        val photos: List<Photo>,
        val isFavorite: Boolean,
    ) : PlacesDetailDataState

    data class Failure(val message: String) : PlacesDetailDataState
    data object Loading : PlacesDetailDataState
}

sealed interface PlacesDetailSummaryDataState {
    data class Success(
        val summaryText: String
    ) : PlacesDetailSummaryDataState

    data class Failure(val message: String) : PlacesDetailSummaryDataState
    data object Loading : PlacesDetailSummaryDataState
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
                currentState.copy(placeDetailsDataState = result.toDataState())
            }
        }.launchIn(viewModelScope)
    }

    fun askAi(){
        summaryRepository.getSummary().onEach { result ->
            _uiState.update { currentState ->
                currentState.copy(summaryDataState = result.toDataState())
            }
        }.onStart {
            _uiState.update { currentState ->
                currentState.copy(summaryDataState = PlacesDetailSummaryDataState.Loading)
            }
        }.launchIn(viewModelScope)

    }


    fun addFavorite() {
        val curr = uiState.value.placeDetailsDataState as? PlacesDetailDataState.Success
        curr?.placeDetails?.id.let { placeDetailsId ->
            placeDetailsId?.let { id ->
                placesRepository.addFavorite(id).onEach { result ->
                    _uiState.update { currentState ->
                        when (result) {
                            is FavoriteResult.FavoriteError -> {
                                currentState.copy(
                                    placeDetailsDataState = PlacesDetailDataState.Failure(
                                        message = result.toUserMessage()
                                    )
                                )
                            }

                            is FavoriteResult.FavoriteSuccess -> {
                                currentState.copy(
                                    placeDetailsDataState = curr?.copy(
                                        isFavorite = result.isFavorite
                                    ) ?: PlacesDetailDataState.Failure(
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
        val curr = uiState.value.placeDetailsDataState as? PlacesDetailDataState.Success
        curr?.placeDetails?.id.let { placeDetailsId ->
            placeDetailsId?.let { id ->
                placesRepository.removeFavorite(id).onEach { result ->
                    _uiState.update { currentState ->
                        when (result) {
                            is FavoriteResult.FavoriteError -> {
                                currentState.copy(
                                    placeDetailsDataState = PlacesDetailDataState.Failure(
                                        message = result.toUserMessage()
                                    )
                                )
                            }

                            is FavoriteResult.FavoriteSuccess -> {
                                currentState.copy(
                                    placeDetailsDataState = curr?.copy(
                                        isFavorite = result.isFavorite
                                    ) ?: PlacesDetailDataState.Failure(
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

    private fun PlaceDetailsResult.toDataState(): PlacesDetailDataState {
        return when (this) {
            is PlaceDetailsResult.DetailsSuccess -> PlacesDetailDataState.Success(
                placeDetails = this.placeDetails,
                photos = this.photos,
                isFavorite = this.favorite
            )

            is PlaceDetailsResult.DetailsError.Backend -> PlacesDetailDataState.Failure(this.toUserMessage())
            is PlaceDetailsResult.DetailsError.Network -> PlacesDetailDataState.Failure(this.toUserMessage())
            is PlaceDetailsResult.DetailsError.Unknown -> PlacesDetailDataState.Failure(this.toUserMessage())
        }
    }

    private fun SummaryResult.toDataState(): PlacesDetailSummaryDataState {
        return when (this) {
            is SummaryResult.SummaryError.Backend -> PlacesDetailSummaryDataState.Failure(this.toUserMessage())
            is SummaryResult.SummaryError.Network -> PlacesDetailSummaryDataState.Failure(this.toUserMessage())
            is SummaryResult.SummaryError.Unknown -> PlacesDetailSummaryDataState.Failure(this.toUserMessage())
            is SummaryResult.SummarySuccess -> PlacesDetailSummaryDataState.Success(
                summaryText = this.summaryText?: "OpenAI failed."
            )
        }
    }

    fun onBackClicked() {
        _uiState.update { PlaceDetailsUIState() }
    }
}
