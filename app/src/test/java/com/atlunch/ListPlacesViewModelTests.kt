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
    fun `loadInitialNearbyPlaces requests location and loads nearby places when permission is enabled`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedPlaces = listOf(BasePlacePreview)

            // fill the mock with data
            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            // create the viewModel
            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            // assert the UiState
            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )

                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                        location = expectedLocation,
                    ),
                )
                assertThat(locationRepository.getCurrentLocationCallCount).isEqualTo(1)
                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadInitialNearbyPlaces only loads once while permission remains enabled`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedPlaces = listOf(BasePlacePreview)

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.onLocationPermissionChanged(true)
            listPlacesViewModel.loadInitialNearbyPlaces()
            advanceUntilIdle()

            listPlacesViewModel.loadInitialNearbyPlaces()
            advanceUntilIdle()

            assertThat(locationRepository.getCurrentLocationCallCount).isEqualTo(1)
        }

    @Test
    fun `onLocationPermissionChanged clears stored location and does not request location when permission is disabled`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedPlaces = listOf(BasePlacePreview)

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )

                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                        location = expectedLocation,
                    ),
                )

                assertThat(locationRepository.getCurrentLocationCallCount).isEqualTo(1)

                listPlacesViewModel.onLocationPermissionChanged(false)
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = false,
                        dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                        location = null,
                    ),
                )
                assertThat(locationRepository.getCurrentLocationCallCount).isEqualTo(1)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadInitialNearbyPlaces updates uiState to Failure when location lookup fails`() =
        runTest {
            placesRepository = FakePlacesRepository()

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationError.Unknown
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )

                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Failure("We couldn't determine your current location."),
                        location = null,
                    ),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search loads nearby places when query is blank and location is already available`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedPlaces = listOf(BasePlacePreview)

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )
                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                val locationLoadedState = awaitItem()
                val initialNearbySuccessState = awaitItem()

                assertThat(initialNearbySuccessState).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                        location = expectedLocation,
                    ),
                )

                listPlacesViewModel.search("")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                        location = expectedLocation,
                    ),
                )

                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                assertThat(placesRepository.lastSearchQuery).isNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search requests query results when query is not blank and location is available`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedPlaces = listOf(BasePlacePreview)
            val expectSearchPlaces =
                listOf(
                    PlacePreview(
                        restaurantName = "Cafe From Search Query",
                        id = "123",
                        rating = 4.5,
                        userRatingCount = 100,
                        shortFormattedAddress = "123 Main St",
                        location = BaseLocation,
                        iconBaseUri = "https://example.com/icon.png",
                    ),
                )

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedPlaces)
                    queryResult = PlacesResult.PlacesSuccess(expectSearchPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )
                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                val locationLoadedState = awaitItem()
                val initialNearbySuccessState = awaitItem()

                assertThat(initialNearbySuccessState).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                        location = expectedLocation,
                    ),
                )

                listPlacesViewModel.search("query")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectSearchPlaces),
                        location = expectedLocation,
                    ),
                )

                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                assertThat(placesRepository.lastSearchQuery).isEqualTo("query")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search updates location and then requests query results when cached location is missing and location lookup succeeds`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedPlaces = listOf(BasePlacePreview)

            placesRepository =
                FakePlacesRepository().apply {
                    queryResult = PlacesResult.PlacesSuccess(expectedPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.search("query")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = false,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = false,
                        dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                        location = expectedLocation,
                    ),
                )

                assertThat(locationRepository.getCurrentLocationCallCount).isEqualTo(1)
                assertThat(placesRepository.lastSearchQuery).isEqualTo("query")
                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search updates uiState to Failure when cached location is missing and location lookup fails`() =
        runTest {
            val expectedPlaces = listOf(BasePlacePreview)

            placesRepository =
                FakePlacesRepository().apply {
                    queryResult = PlacesResult.PlacesSuccess(expectedPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationError.Unknown
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.search("query")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = false,
                        dataState = ListPlacesUiState.DataState.Failure("We couldn't determine your current location."),
                        location = null,
                    ),
                )
                assertThat(locationRepository.getCurrentLocationCallCount).isEqualTo(1)
                assertThat(placesRepository.lastSearchQuery).isEqualTo(null)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search updates uiState to Success with one place when query returns a single result`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedPlaces = listOf(BasePlacePreview)

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedPlaces)
                    queryResult = PlacesResult.PlacesSuccess(expectedPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )
                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                val locationLoadedState = awaitItem()
                val initialNearbySuccessState = awaitItem()

                assertThat(initialNearbySuccessState).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                        location = expectedLocation,
                    ),
                )

                listPlacesViewModel.search("query")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedPlaces),
                        location = expectedLocation,
                    ),
                )

                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                assertThat(placesRepository.lastSearchQuery).isEqualTo("query")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search updates uiState to Success with all queried places when query returns many results`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedNearbyPlaces = listOf(BasePlacePreview)
            val expectedQueryPlaces =
                listOf(
                    BasePlacePreview,
                    PlacePreview(
                        restaurantName = "Bistro",
                        id = "456",
                        rating = 4.1,
                        userRatingCount = 58,
                        shortFormattedAddress = "456 Oak St",
                        location = BaseLocation,
                        iconBaseUri = "https://example.com/bistro.png",
                    ),
                    PlacePreview(
                        restaurantName = "Deli",
                        id = "789",
                        rating = 3.9,
                        userRatingCount = 25,
                        shortFormattedAddress = "789 Pine St",
                        location = BaseLocation,
                        iconBaseUri = "https://example.com/deli.png",
                    ),
                )

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedNearbyPlaces)
                    queryResult = PlacesResult.PlacesSuccess(expectedQueryPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )
                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                val locationLoadedState = awaitItem()
                val initialNearbySuccessState = awaitItem()

                assertThat(initialNearbySuccessState).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedNearbyPlaces),
                        location = expectedLocation,
                    ),
                )

                listPlacesViewModel.search("query")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedQueryPlaces),
                        location = expectedLocation,
                    ),
                )

                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                assertThat(placesRepository.lastSearchQuery).isEqualTo("query")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search updates uiState to Success with an empty list when query returns no places`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedNearbyPlaces = listOf(BasePlacePreview)
            val expectedQueryPlaces = emptyList<PlacePreview>()

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedNearbyPlaces)
                    queryResult = PlacesResult.PlacesSuccess(expectedQueryPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )
                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                val locationLoadedState = awaitItem()
                val initialNearbySuccessState = awaitItem()

                assertThat(initialNearbySuccessState).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedNearbyPlaces),
                        location = expectedLocation,
                    ),
                )

                listPlacesViewModel.search("query")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedQueryPlaces),
                        location = expectedLocation,
                    ),
                )

                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                assertThat(placesRepository.lastSearchQuery).isEqualTo("query")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search updates uiState to Failure when nearby search returns a network error`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedNearbyPlaces = listOf(BasePlacePreview)

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedNearbyPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )
                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                val locationLoadedState = awaitItem()
                val initialNearbySuccessState = awaitItem()

                assertThat(initialNearbySuccessState).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedNearbyPlaces),
                        location = expectedLocation,
                    ),
                )

                placesRepository.nearbyResult = PlacesResult.PlacesError.Network

                listPlacesViewModel.search("")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState =
                            ListPlacesUiState.DataState.Failure(
                                "Please check your internet connection and try again.",
                            ),
                        location = expectedLocation,
                    ),
                )

                assertThat(placesRepository.lastSearchQuery).isNull()
                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search updates uiState to Failure when nearby search returns a backend error`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedNearbyPlaces = listOf(BasePlacePreview)

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedNearbyPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )
                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                val locationLoadedState = awaitItem()
                val initialNearbySuccessState = awaitItem()

                assertThat(initialNearbySuccessState).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedNearbyPlaces),
                        location = expectedLocation,
                    ),
                )

                placesRepository.nearbyResult = PlacesResult.PlacesError.Backend

                listPlacesViewModel.search("")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState =
                            ListPlacesUiState.DataState.Failure(
                                "We're having trouble reaching the Google API servers right now. Please try again in a moment.",
                            ),
                        location = expectedLocation,
                    ),
                )

                assertThat(placesRepository.lastSearchQuery).isNull()
                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `search updates uiState to Failure when nearby search returns a unknown error`() =
        runTest {
            val expectedLocation = BaseLocation
            val expectedNearbyPlaces = listOf(BasePlacePreview)

            placesRepository =
                FakePlacesRepository().apply {
                    nearbyResult = PlacesResult.PlacesSuccess(expectedNearbyPlaces)
                }

            locationRepository =
                FakeLocationRepository().apply {
                    locationResult = LocationResult.LocationSuccess(expectedLocation)
                }

            listPlacesViewModel =
                ListPlacesViewModel(
                    locationRepository = locationRepository,
                    placesRepository = placesRepository,
                )

            listPlacesViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(ListPlacesUiState())

                listPlacesViewModel.onLocationPermissionChanged(true)
                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = null,
                    ),
                )
                listPlacesViewModel.loadInitialNearbyPlaces()
                advanceUntilIdle()

                val locationLoadedState = awaitItem()
                val initialNearbySuccessState = awaitItem()

                assertThat(initialNearbySuccessState).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Success(expectedNearbyPlaces),
                        location = expectedLocation,
                    ),
                )

                placesRepository.nearbyResult = PlacesResult.PlacesError.Unknown

                listPlacesViewModel.search("")
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState = ListPlacesUiState.DataState.Loading,
                        location = expectedLocation,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    ListPlacesUiState(
                        isLocationPermissionEnabled = true,
                        dataState =
                            ListPlacesUiState.DataState.Failure(
                                "An unknown error occurred.",
                            ),
                        location = expectedLocation,
                    ),
                )

                assertThat(placesRepository.lastSearchQuery).isNull()
                assertThat(placesRepository.lastNearbyLat).isEqualTo(expectedLocation.latitude)
                assertThat(placesRepository.lastNearbyLong).isEqualTo(expectedLocation.longitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    companion object {
        val BaseLocation =
            Location(
                latitude = 100.0,
                longitude = -100.0,
            )
        val BasePlacePreview =
            PlacePreview(
                restaurantName = "Cafe",
                id = "123",
                rating = 4.5,
                userRatingCount = 100,
                shortFormattedAddress = "123 Main St",
                location = BaseLocation,
                iconBaseUri = "https://example.com/icon.png",
            )
    }
}
