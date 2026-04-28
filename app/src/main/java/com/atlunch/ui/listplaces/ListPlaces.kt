package com.atlunch.ui.listplaces

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.atlunch.R
import com.atlunch.domain.Location
import com.atlunch.domain.PlacePreview
import com.atlunch.ui.theme.AtLunchTheme

@Composable
fun ListPlaces(
    placePreviews: List<PlacePreview>,
    onPlaceClicked: (String) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) {
        items(
            items = placePreviews,
            key = { placePreview -> placePreview.id },
        ) { preview ->
            PlacePreviewListItem(
                preview,
                onPlaceClicked = onPlaceClicked,
                modifier.padding(8.dp),
            )
        }
    }
}

@Composable
fun PlacePreviewListItem(
    placePreview: PlacePreview,
    onPlaceClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onPlaceClicked(placePreview.id) },
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = placePreview.iconBaseUri,
                    contentDescription = "${placePreview.restaurantName} icon",
                    modifier =
                        Modifier
                            .size(48.dp),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.selected_pin),
                    error = painterResource(R.drawable.selected_pin),
                )

                Column(
                    modifier =
                        Modifier
                            .padding(start = 16.dp)
                            .fillMaxWidth(),
                ) {
                    Text(
                        text = placePreview.restaurantName,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.star_filled),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                        )

                        Text(
                            text = (placePreview.rating ?: 0.0).toString(),
                            modifier = Modifier.padding(start = 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Text(
                            text = "• (${placePreview.userRatingCount ?: 0} reviews)",
                            modifier = Modifier.padding(start = 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Text(
                        text = placePreview.shortFormattedAddress ?: "Address not available.",
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Visible,
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
            placePreview =
                PlacePreview(
                    restaurantName = "Joe's Pizza",
                    id = "preview-place-id",
                    rating = 4.7,
                    userRatingCount = 128,
                    shortFormattedAddress = "123 Main St",
                    location = Location(0.0, 0.0),
                    iconBaseUri = "",
                ),
            onPlaceClicked = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListPlacesPreview() {
    AtLunchTheme {
        ListPlaces(
            placePreviews =
                listOf(
                    PlacePreview(
                        restaurantName = "Joe's Pizza",
                        id = "preview-place-1",
                        rating = 4.7,
                        userRatingCount = 128,
                        shortFormattedAddress = "123 Main St",
                        location = Location(0.0, 0.0),
                        iconBaseUri = "",
                    ),
                    PlacePreview(
                        restaurantName = "Sushi Corner",
                        id = "preview-place-2",
                        rating = 4.5,
                        userRatingCount = 84,
                        shortFormattedAddress = "456 Elm St",
                        location = Location(0.0, 0.0),
                        iconBaseUri = "",
                    ),
                    PlacePreview(
                        restaurantName = "Burger House",
                        id = "preview-place-3",
                        rating = 4.3,
                        userRatingCount = 201,
                        shortFormattedAddress = "789 Oak Ave",
                        location = Location(0.0, 0.0),
                        iconBaseUri = "",
                    ),
                ),
            onPlaceClicked = {},
            modifier =
                Modifier
                    .padding(8.dp),
        )
    }
}
