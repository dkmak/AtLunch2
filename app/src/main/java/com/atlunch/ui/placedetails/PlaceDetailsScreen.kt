package com.atlunch.ui.placedetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.atlunch.R
import com.atlunch.domain.Photo
import com.atlunch.domain.PlaceDetails
import com.atlunch.ui.theme.AtLunchTheme

@OptIn(ExperimentalMaterial3Api::class)
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
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClicked()
                        viewModel.onBackClicked()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
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
                    DisplayPlacePhotos(
                        state.photos,
                        restaurantName = state.placeDetails.restaurantName
                    )
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
fun DisplayPlacePhotos(
    photos: List<Photo>,
    restaurantName: String,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
    ) {
        items(
            photos
        ) { picture ->
            PictureItem(picture, restaurantName)
        }
    }
}

@Composable
fun PictureItem(
    photo: Photo,
    restaurantName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        border = BorderStroke(
            width = 2.dp,
            color = Color.DarkGray
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(128.dp)
                        .padding(8.dp),
                    model = photo.photoUrl,
                    contentDescription = "Photo of {$restaurantName}",
                    contentScale = ContentScale.Fit
                )
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
        Text(
            placeDetails.formattedAddress ?: "Address not available.",
            style = MaterialTheme.typography.titleMedium
        )
        Text(placeDetails.nationalPhoneNumber?:"")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.star_filled),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = (placeDetails.rating ?: 0.0).toString(),
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• (${placeDetails.userRatingCount ?: 0} reviews)",
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hours",
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.titleSmall
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Expand hours"
            )
        }
        placeDetails.openingHours?.forEach { openHoursString ->
            Text(
                text = openHoursString,
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }?: run {
            Text(
                text = "opening hours unavailable",
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
                nationalPhoneNumber = "(212) 555-1234",
                openingHours = listOf(
                    "Monday: 8:30AM–3:30PM",
                    "Tuesday: 8:30AM–3:30PM",
                    "Wednesday: 8:30AM–3:30PM, 5:30–10:00PM",
                    "Thursday: 8:30AM–3:30PM, 5:30–10:00PM",
                    "Friday: 8:30AM–3:30PM, 5:30–10:00PM",
                    "Saturday: 8:30AM–4:00PM, 5:30–10:00PM",
                    "Sunday: 8:30AM–4:00PM"
                )
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayPlaceDefaultsPreview() {
    AtLunchTheme {
        DisplayPlaceDetails(
            placeDetails = PlaceDetails(
                restaurantName = "Joe's Pizza",
                id = "preview-place-id",
                rating = null,
                userRatingCount = null,
                formattedAddress = null,
                nationalPhoneNumber = null,
                openingHours = null
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}
