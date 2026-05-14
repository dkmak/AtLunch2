package dkmak.atlunch.ui.listplaces

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import dkmak.atlunch.R
import dkmak.atlunch.domain.Location
import dkmak.atlunch.domain.PlacePreview

@Composable
fun MapPlaces(
    placePreviews: List<PlacePreview>,
    userLocation: Location?,
    onPlaceClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPlaceId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedPlacePreview =
        selectedPlaceId?.let { placeId ->
            placePreviews.firstOrNull { preview -> preview.id == placeId }
        }

    userLocation?.let { location ->
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            val location = LatLng(userLocation.latitude, userLocation.longitude)
            val cameraPositionState =
                rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 15f)
                }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            ) {
                placePreviews
                    .mapNotNull { preview ->
                        preview.location?.let { location -> preview to location }
                    }.forEach { (preview, location) ->
                        val isSelected = selectedPlaceId == preview.id
                        MarkerComposable(
                            keys = arrayOf(preview.id, isSelected),
                            state =
                                rememberMarkerState(
                                    position = LatLng(location.latitude, location.longitude),
                                ),
                            title = preview.restaurantName,
                            snippet = preview.shortFormattedAddress,
                            onClick = {
                                selectedPlaceId = preview.id
                                true
                            },
                        ) {
                            Image(
                                painter =
                                    painterResource(
                                        id =
                                            if (isSelected) {
                                                R.drawable.selected_pin
                                            } else {
                                                R.drawable.resting_pin
                                            },
                                    ),
                                contentDescription = null,
                                modifier =
                                    Modifier.clickable {
                                        selectedPlaceId = preview.id
                                    },
                            )
                        }
                    }
            }

            Column(
                modifier =
                    Modifier
                        .align(Alignment.Center),
            ) {
                selectedPlacePreview?.let { placePreview ->
                    PlacePreviewListItem(
                        placePreview = placePreview,
                        onPlaceClicked = onPlaceClicked,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }
}
