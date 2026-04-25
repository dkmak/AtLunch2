package com.atlunch.ui.placedetails

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.atlunch.R
import com.atlunch.domain.Photo
import com.atlunch.domain.PlaceDetails
import com.atlunch.ui.theme.AtLunchTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailsScreen(
    placeId: String,
    viewModel: PlaceDetailsViewModel = hiltViewModel(),
    onBackClicked: () -> Unit
) {
    var showBottomSheet by remember {mutableStateOf(false)}
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(placeId) {
        viewModel.loadDetails(placeId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (showBottomSheet){
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }
        )  {
            val placeDetails = uiState as? DetailsUiState.Success
            var showLink by remember { mutableStateOf(false) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = "Share ${placeDetails?.placeDetails?.restaurantName?:""}")
                Row(){
                    Button(
                        onClick = {
                            showLink = true
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion { showBottomSheet = false }
                        }
                    ) {
                        Text("Link")
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }
                            val sendIntent = Intent().apply{
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Place")
                            }

                            val shareIntent = Intent.createChooser(sendIntent, "")
                            context.startActivity(shareIntent)
                        }
                    ) {
                        Text("Place")
                    }
                }

                if (showLink){
                    Text("Link to Restauraunt: https.....")
                }
            }
        }
    }
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
                },
                actions = {
                        IconButton(
                            onClick = {
                                showBottomSheet = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
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
            when (val state = uiState.placeDetailsDataState) {
                is PlacessDetailDataState.Failure -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = state.message)
                    }
                }

                PlacessDetailDataState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is PlacessDetailDataState.Success -> {
                    DisplayPlacePhotos(
                        state.photos,
                        restaurantName = state.placeDetails.restaurantName
                    )
                    DisplayPlaceDetails(
                        placeDetails = state.placeDetails,
                        favorite = state.isFavorite,
                        onFavoriteClicked = {
                            if (state.isFavorite) {
                                viewModel.removeFavorites()
                            } else {
                                viewModel.addFavorite()
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
            uiState.summary?.let{ summary ->
                Text(summary)
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
                    contentScale = ContentScale.Fit,
                    error = painterResource(R.drawable.star_filled)
                )
            }
        }
    }
}

@Composable
fun DisplayPlaceDetails(
    placeDetails: PlaceDetails,
    favorite: Boolean,
    modifier: Modifier = Modifier,
    onFavoriteClicked: () -> Unit
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
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(placeDetails.nationalPhoneNumber ?: "")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.star_filled),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = (placeDetails.rating ?: 0.0).toString(),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• (${placeDetails.userRatingCount ?: 0} reviews)",
                style = MaterialTheme.typography.bodyMedium
            )

            Icon(
                imageVector = if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorites",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable { onFavoriteClicked() }
            )
        }

        HoursItem(
            placeDetails.openingHours
        )
    }
}

@Composable
fun HoursItem(
    openingHours: List<String>?,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = null,
            ) { isExpanded = !isExpanded }
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hours",
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.titleSmall
            )

            val rotation by animateFloatAsState(
                targetValue = if (isExpanded) 0f else 180f,
                label = "HoursArrowRotation"
            )

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = if (isExpanded) "Collapse hours" else "Expand hours",
                modifier = Modifier.rotate(rotation)
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                openingHours?.forEach { openHoursString ->
                    Text(
                        text = openHoursString,
                        modifier = Modifier.padding(start = 6.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } ?: run {
                    Text(
                        text = "Opening hours unavailable",
                        modifier = Modifier.padding(start = 6.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayPlacePhotosPreview() {
    AtLunchTheme {
        DisplayPlacePhotos(
            photos = List(6) { Photo(photoUrl = "") },
            restaurantName = "Preview Restaurant"
        )
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
                googleMapsUri = "",
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
            favorite = false,
            onFavoriteClicked = {}
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
                googleMapsUri = "",
                userRatingCount = null,
                formattedAddress = null,
                nationalPhoneNumber = null,
                openingHours = null

            ),
            favorite = false,
            onFavoriteClicked = {}
        )
    }
}
