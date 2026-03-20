package com.atlunch.ui.listplaces

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.atlunch.R
import com.atlunch.domain.PlacePreview
import com.atlunch.domain.Location
import com.atlunch.ui.theme.AtLunchTheme
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.serialization.Serializable

@Serializable
data object PlaceDestination : NavKey

@Composable
fun PlacesScreen(
    viewModel: ListViewModel = hiltViewModel(),
    onPlacePreviewClicked: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var textFieldValue by rememberSaveable { mutableStateOf("") }
    var isMapView by rememberSaveable { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onLocationPermissionChanged(granted)
    }

    LaunchedEffect(Unit) {
        if (textFieldValue.isEmpty()) {
            val hasLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            viewModel.onLocationPermissionChanged(hasLocationPermission)

            if (hasLocationPermission) {
                viewModel.search(textFieldValue)
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            viewModel.search(textFieldValue)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isMapView = !isMapView },
                modifier = Modifier.padding(bottom = 16.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Text(
                    text = if (isMapView) "List" else "Map",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
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
            if (!uiState.isLocationPermissionEnabled) {
                Text(
                    text = "Location permission denied. Please enable location services.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                when (val state = uiState.dataState) {
                    is ListPlacesUiState.DataState.Failure -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(text = state.message)
                        }
                    }

                    ListPlacesUiState.DataState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ListPlacesUiState.DataState.Success -> {
                        if (state.placesPreviews.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text("No results found.")
                            }
                        } else {
                            if (isMapView) {
                                PlacesMapContent(
                                    placePreviews = state.placesPreviews,
                                    userLocation = uiState.location,
                                    onPlaceClicked = onPlacePreviewClicked,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
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
fun PlacesMapContent(
    placePreviews: List<PlacePreview>,
    userLocation: Location?,
    onPlaceClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mapLoaded by remember { mutableStateOf(false) }

    val markerIcon = remember(mapLoaded) {
        if (mapLoaded) {
            BitmapDescriptorFactory.fromResource(R.drawable.resting_pin)
        } else {
            null
        }
    }

    userLocation?.let{ location ->
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            val location = LatLng(userLocation.latitude, userLocation.longitude)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 15f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLoaded = { mapLoaded = true }
            ) {
                placePreviews.filter { preview -> preview.location != null }.forEach {preview ->
                    val (previewLat, previewLong) = preview.location!!
                    MarkerComposable(
                        state = rememberMarkerState(
                            position = LatLng(previewLat, previewLong)
                        ),
                        title = preview.restaurantName,
                        snippet = preview.shortFormattedAddress
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.selected_pin),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}