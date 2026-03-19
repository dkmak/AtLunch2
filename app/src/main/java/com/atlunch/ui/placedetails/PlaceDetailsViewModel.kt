package com.atlunch.ui.placedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atlunch.domain.Photo
import com.atlunch.domain.PlaceDetails
import com.atlunch.domain.PlaceDetailsResult
import com.atlunch.domain.PlacesRepository
import com.atlunch.ui.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface DetailsUiState{
    data class Success(
        val placeDetails: PlaceDetails,
        val photos: List<Photo>,
    ): DetailsUiState
    data class Failure(val message: String): DetailsUiState
    data object Loading: DetailsUiState
}

@HiltViewModel
class PlaceDetailsViewModel @Inject constructor(
    private val repository: PlacesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadDetails(id: String) {
        repository.getPlaceDetails(id).onEach { result ->
            _uiState.update { result.toUiState() }
        }.launchIn(viewModelScope)
    }

    private fun PlaceDetailsResult.toUiState(): DetailsUiState{
        return when (this){
            is PlaceDetailsResult.DetailsSuccess -> DetailsUiState.Success(
                placeDetails = this.placeDetails,
                photos = this.photos
            )
            is PlaceDetailsResult.DetailsError.Backend -> DetailsUiState.Failure(this.toUserMessage())
            is PlaceDetailsResult.DetailsError.Network -> DetailsUiState.Failure(this.toUserMessage())
            is PlaceDetailsResult.DetailsError.Unknown -> DetailsUiState.Failure(this.toUserMessage())
        }
    }
}