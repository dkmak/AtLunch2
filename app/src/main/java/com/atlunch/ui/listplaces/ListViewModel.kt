package com.atlunch.ui.listplaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atlunch.domain.PlacePreview
import com.atlunch.domain.PlacesRepository
import com.atlunch.ui.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface ListPlacesUiState {
    data class Success(val placesPreviews: List<PlacePreview>) : ListPlacesUiState
    data class Failure(val message: String) : ListPlacesUiState
    data object Loading : ListPlacesUiState
}

@HiltViewModel
class ListViewModel @Inject constructor(
    repository: PlacesRepository
) : ViewModel() {

    val uiState: StateFlow<ListPlacesUiState> = repository.searchNearby(0.0, 0.0) // placeholders
        .map<List<PlacePreview>, ListPlacesUiState> { places ->
            ListPlacesUiState.Success(places)
        }.catch { throwable ->
            emit(ListPlacesUiState.Failure(throwable.toUserMessage()))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ListPlacesUiState.Loading
        )
}