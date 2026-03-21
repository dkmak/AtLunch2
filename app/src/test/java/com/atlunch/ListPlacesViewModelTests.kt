package com.atlunch

import app.cash.turbine.test
import com.atlunch.domain.Location
import com.atlunch.domain.LocationResult
import com.atlunch.domain.PlacePreview
import com.atlunch.domain.PlacesResult
import com.atlunch.ui.listplaces.ListPlacesUiState
import com.atlunch.ui.listplaces.ListPlacesViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListPlacesViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var locationRepository: FakeLocationRepository
    private lateinit var placesRepository: FakePlacesRepository
    private lateinit var listPlacesViewModel: ListPlacesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun after() {
        Dispatchers.resetMain()
    }


    @Test
    fun `onLocationPermissionChanged requests location and loads nearby places when permission is enabled`() = runTest {
        val expectedLocation = BaseLocation
        val expectedPlaces = listOf(BaseExamplePlacePreview)

        placesRepository = FakePlacesRepository().apply {
            nearbyResult = PlacesResult.PlacesSuccess(expectedPlaces)
        }

        locationRepository = FakeLocationRepository().apply {
            locationResult = LocationResult.LocationSuccess(expectedLocation)
        }

        listPlacesViewModel = ListPlacesViewModel(
            locationRepository = locationRepository,
            placesRepository = placesRepository
        )

        listPlacesViewModel.uiState.test{
            assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

            listPlacesViewModel.onLocationPermissionChanged(true)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(
                ListPlacesUiState(
                    isLocationPermissionEnabled = true,
                    dataState = ListPlacesUiState.DataState.Loading,
                    location = null
                )
            )

            assertThat(awaitItem()).isEqualTo(
                ListPlacesUiState(
                    isLocationPermissionEnabled = true,
                    dataState = ListPlacesUiState.DataState.Loading,
                    location = expectedLocation
                )
            )

            assertThat(awaitItem()).isEqualTo(
                ListPlacesUiState(
                    isLocationPermissionEnabled = true,
                    dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                    location = expectedLocation
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onLocationPermissionChanged clears stored location and does not request location when permission is disabled`() = runTest {
        val expectedLocation = BaseLocation
        val expectedPlaces = listOf(BaseExamplePlacePreview)

        placesRepository = FakePlacesRepository().apply {
            nearbyResult = PlacesResult.PlacesSuccess(expectedPlaces)
        }

        locationRepository = FakeLocationRepository().apply {
            locationResult = LocationResult.LocationSuccess(expectedLocation)
        }

        listPlacesViewModel = ListPlacesViewModel(
            locationRepository = locationRepository,
            placesRepository = placesRepository
        )

        listPlacesViewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

            listPlacesViewModel.onLocationPermissionChanged(true)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(
                ListPlacesUiState(
                    isLocationPermissionEnabled = true,
                    dataState = ListPlacesUiState.DataState.Loading,
                    location = null
                )
            )

            assertThat(awaitItem()).isEqualTo(
                ListPlacesUiState(
                    isLocationPermissionEnabled = true,
                    dataState = ListPlacesUiState.DataState.Loading,
                    location = expectedLocation
                )
            )

            assertThat(awaitItem()).isEqualTo(
                ListPlacesUiState(
                    isLocationPermissionEnabled = true,
                    dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                    location = expectedLocation
                )
            )

            listPlacesViewModel.onLocationPermissionChanged(false)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(
                ListPlacesUiState(
                    isLocationPermissionEnabled = false,
                    dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                    location = null
                )
            )
        }
    }

    @Test
    fun `onLocationPermissionChanged updates uiState to Failure when location lookup fails`() {

    }

    @Test
    fun `search loads nearby places when query is blank and location is already available`() {

    }

    @Test
    fun `search requests query results when query is not blank and location is already available`() {

    }

    @Test
    fun `search updates location and then requests query results when cached location is missing and location lookup succeeds`() {

    }

    @Test
    fun `search updates uiState to Failure when cached location is missing and location lookup fails`() {

    }

    @Test
    fun `search updates uiState to Success with one nearby place when blank query returns a single result`() {

    }

    @Test
    fun `search updates uiState to Success with all nearby places when blank query returns many results`() {

    }

    @Test
    fun `search updates uiState to Success with an empty list when blank query returns no places`() {

    }

    @Test
    fun `search updates uiState to Failure when query search returns a network error`() {

    }

    @Test
    fun `search updates uiState to Failure when nearby search returns a backend error`() {

    }

    companion object {
        val BaseLocation = Location(
            latitude = 100.0,
            longitude = -100.0
        )
        val BaseExamplePlacePreview = PlacePreview(
            restaurantName = "Cafe",
            id = "123",
            rating = 4.5,
            userRatingCount = 100,
            shortFormattedAddress = "123 Main St",
            location = BaseLocation,
            iconBaseUri = "https://example.com/icon.png"
        )
    }
}
