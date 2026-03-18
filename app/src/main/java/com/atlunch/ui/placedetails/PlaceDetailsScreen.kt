package com.atlunch.ui.placedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PlaceDetailsScreen(
    placeId: String,
    viewModel: PlaceDetailsViewModel = hiltViewModel(),
    onBackClicked: ()-> Unit
){
    LaunchedEffect(placeId) {
        viewModel.loadDetails(placeId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            when (val state = uiState) {
                is DetailsUiState.Failure -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = state.message)
                    }
                }

                DetailsUiState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is DetailsUiState.Success -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(state.placeDetails.restaurantName)
                        Text(state.placeDetails.rating.toString())
                        Text(state.placeDetails.userRatingCount.toString())
                        Text(state.placeDetails.formattedAddress)
                        Text(state.placeDetails.nationalPhoneNumber)
                    }
                }
            }
        }
    }
}