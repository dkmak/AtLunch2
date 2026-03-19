package com.atlunch.ui.listplaces

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import coil.compose.AsyncImage
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var textFieldValue by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // TODO
    LaunchedEffect(Unit) {
        if (textFieldValue.isEmpty()){
            viewModel.loadPlacesNearby()
        }else {
            viewModel.search(textFieldValue)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchPlacesTopBar(
                query = textFieldValue,
                onQueryChange = { newValue -> textFieldValue = newValue },
                onSearch = {
                    keyboardController?.hide()
                    viewModel.search(textFieldValue)
                    focusManager.clearFocus()
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
                is ListPlacesUiState.Failure -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = state.message)
                    }
                }

                ListPlacesUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ListPlacesUiState.Success -> {
                    if (state.placesPreviews.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("No results found.")
                        }
                    } else {
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
}

@Composable
fun SearchPlacesTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_lockup),
            contentDescription = "AtLunch",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search restaurants") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSearch() }
                ),
                shape = RoundedCornerShape(35.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPlacesTopBarPreview() {
    AtLunchTheme {
        SearchPlacesTopBar(
            query = "Sushi",
            onQueryChange = {},
            onSearch = {}
        )
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = placePreview.iconBaseUri,
                    contentDescription = "${placePreview.restaurantName} icon",
                    modifier = Modifier
                        .size(48.dp),
                    contentScale = ContentScale.Fit
                )

                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
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
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "• (${placePreview.userRatingCount} reviews)",
                            modifier = Modifier.padding(start = 6.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        text = placePreview.shortFormattedAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Visible
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
                shortFormattedAddress = "123 Main St",
                iconBaseUri = ""
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
                    shortFormattedAddress = "123 Main St",
                    iconBaseUri = ""
                ),
                PlacePreview(
                    restaurantName = "Sushi Corner",
                    id = "preview-place-2",
                    rating = 4.5,
                    userRatingCount = 84,
                    shortFormattedAddress = "456 Elm St",
                    iconBaseUri = ""
                ),
                PlacePreview(
                    restaurantName = "Burger House",
                    id = "preview-place-3",
                    rating = 4.3,
                    userRatingCount = 201,
                    shortFormattedAddress = "789 Oak Ave",
                    iconBaseUri = ""
                )
            ),
            onPlaceClicked = {},
            modifier = Modifier
                .padding(8.dp)
        )
    }
}
