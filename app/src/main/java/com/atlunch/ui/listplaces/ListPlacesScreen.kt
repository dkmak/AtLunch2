package com.atlunch.ui.listplaces

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.atlunch.R
import com.atlunch.domain.PlacePreview
import com.atlunch.ui.theme.AtLunchTheme
import kotlinx.serialization.Serializable

@Serializable
data object ListDestination : NavKey

@Composable
fun ListPlacesScreen(
    viewModel: ListViewModel = hiltViewModel(),
    onPlacePreviewClicked: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadPlacesNearby()
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var textFieldValue by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search restaurants") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            viewModel.search(textFieldValue)
                        }
                    ),
                )
            }

            when (val state = uiState) {
                is ListPlacesUiState.Failure -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = state.message)
                    }
                }

                ListPlacesUiState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ListPlacesUiState.Success -> {
                    DisplayPlacesList(
                        placePreviews = state.placesPreviews,
                        onPlaceClicked = onPlacePreviewClicked,
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
fun DisplayPlacesList(
    placePreviews: List<PlacePreview>,
    onPlaceClicked: (String) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        items(
            items = placePreviews,
            key = { placePreview -> placePreview.id }
        ) { preview ->
            PlacePreviewListItem(
                preview,
                onPlaceClicked = onPlaceClicked,
                modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun PlacePreviewListItem(
    placePreview: PlacePreview,
    onPlaceClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPlaceClicked(placePreview.id) },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = placePreview.restaurantName,
                    style = MaterialTheme.typography.titleMedium
                )

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
                        text = placePreview.rating.toString(),
                        modifier = Modifier.padding(start = 6.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "(${placePreview.userRatingCount} reviews)",
                        modifier = Modifier.padding(start = 6.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlacePreviewListItemPreview() {
    AtLunchTheme {
        PlacePreviewListItem(
            placePreview = PlacePreview(
                restaurantName = "Joe's Pizza",
                id = "preview-place-id",
                rating = 4.7,
                userRatingCount = 128,
                shortFormattedAddress = "123 Main St"
            ),
            onPlaceClicked = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayPlacesListPreview() {
    AtLunchTheme {
        DisplayPlacesList(
            placePreviews = listOf(
                PlacePreview(
                    restaurantName = "Joe's Pizza",
                    id = "preview-place-1",
                    rating = 4.7,
                    userRatingCount = 128,
                    shortFormattedAddress = "123 Main St"
                ),
                PlacePreview(
                    restaurantName = "Sushi Corner",
                    id = "preview-place-2",
                    rating = 4.5,
                    userRatingCount = 84,
                    shortFormattedAddress = "456 Elm St"
                ),
                PlacePreview(
                    restaurantName = "Burger House",
                    id = "preview-place-3",
                    rating = 4.3,
                    userRatingCount = 201,
                    shortFormattedAddress = "789 Oak Ave"
                )
            ),
            onPlaceClicked = {},
            modifier = Modifier
                .padding(8.dp)
        )
    }
}
