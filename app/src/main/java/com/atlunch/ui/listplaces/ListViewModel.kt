package com.atlunch.ui.listplaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atlunch.domain.PlacePreview
import com.atlunch.domain.PlacesRepository
import com.atlunch.ui.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface ListPlacesUiState {
    data class Success(val placesPreviews: List<PlacePreview>) : ListPlacesUiState
    data class Failure(val message: String) : ListPlacesUiState
    data object Loading : ListPlacesUiState
}

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: PlacesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ListPlacesUiState>(ListPlacesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadPlacesNearby() {
        repository.searchNearby(0.0, 0.0) // TODO placeholders
            .onEach { list ->
                _uiState.update { ListPlacesUiState.Success(list) }
            }.catch { throwable ->
                _uiState.update { ListPlacesUiState.Failure(throwable.toUserMessage()) }
            }.launchIn(viewModelScope)
    }

    fun search(query: String){
        if (query.isNotEmpty()){
         repository.searchQuery(query)
             .onEach { list ->
                 _uiState.update { ListPlacesUiState.Success(list) }
             }.onStart {
                 _uiState.update { ListPlacesUiState.Loading }
             }
             .catch { throwable ->
                 _uiState.update { ListPlacesUiState.Failure(throwable.toUserMessage()) }
             }.launchIn(viewModelScope)
        }
    }
}