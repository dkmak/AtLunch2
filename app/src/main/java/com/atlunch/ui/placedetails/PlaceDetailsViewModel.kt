package com.atlunch.ui.placedetails

import android.telecom.Call
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atlunch.DetailsDestination
import com.atlunch.data.toDomainError
import com.atlunch.domain.PlaceDetails
import com.atlunch.domain.PlacesRepository
import com.atlunch.ui.listplaces.ListPlacesUiState
import com.atlunch.ui.toUserMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface DetailsUiState{
    data class Success(val placeDetails: PlaceDetails): DetailsUiState
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
        repository.getPlaceDetails(id).onEach { details ->
            _uiState.update { DetailsUiState.Success(details) }
        }.catch { throwable ->
            _uiState.update { DetailsUiState.Failure(throwable.toUserMessage()) }
        }.launchIn(viewModelScope)
    }
}