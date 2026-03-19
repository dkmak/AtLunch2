package com.atlunch.ui.placedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atlunch.R
import com.atlunch.data.dto.PlaceDetailsDTO
import com.atlunch.domain.PlaceDetails
import com.atlunch.ui.theme.AtLunchTheme

@Composable
fun PlaceDetailsScreen(
    placeId: String,
    viewModel: PlaceDetailsViewModel = hiltViewModel(),
    onBackClicked: () -> Unit
) {
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
                    DisplayPlaceDetails(
                        placeDetails = state.placeDetails,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayPlaceDetails(
    placeDetails: PlaceDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            placeDetails.restaurantName,
            style = MaterialTheme.typography.titleLarge
        )
        Text(placeDetails.formattedAddress,
            style = MaterialTheme.typography.titleMedium
        )
        Text(placeDetails.nationalPhoneNumber)
        Row(
            modifier = Modifier.padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.star_filled),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = placeDetails.rating.toString(),
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• (${placeDetails.userRatingCount} reviews)",
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayPlaceDetailsPreview() {
    AtLunchTheme {
        DisplayPlaceDetails(
            placeDetails = PlaceDetails(
                restaurantName = "Joe's Pizza",
                id = "preview-place-id",
                rating = 4.7,
                userRatingCount = 128,
                formattedAddress = "123 Main St, New York, NY 10001",
                nationalPhoneNumber = "(212) 555-1234"
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}
