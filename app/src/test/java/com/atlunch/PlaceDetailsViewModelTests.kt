package dkmak.atlunch

import app.cash.turbine.test
import dkmak.atlunch.domain.Photo
import dkmak.atlunch.domain.PlaceDetails
import dkmak.atlunch.domain.PlaceDetailsResult
import dkmak.atlunch.domain.SummaryResult
import dkmak.atlunch.ui.placedetails.PlaceDetailsUIState
import dkmak.atlunch.ui.placedetails.PlaceDetailsViewModel
import dkmak.atlunch.ui.placedetails.PlacesDetailDataState
import dkmak.atlunch.ui.placedetails.PlacesDetailSummaryDataState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceDetailsViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakePlacesRepository
    private lateinit var summaryRepository: FakeSummaryRepository
    private lateinit var placeDetailsViewModel: PlaceDetailsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        summaryRepository = FakeSummaryRepository()
    }

    @After
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDetails updates uiState to Success when repository returns place details`() =
        runTest {
            val expectedPlaceDetails = BaseExamplePlaceDetails
            val expectedPhotos = BaseExamplePhotos

            repository =
                FakePlacesRepository().apply {
                    placeDetailsResult =
                        PlaceDetailsResult.DetailsSuccess(
                            placeDetails = expectedPlaceDetails,
                            photos = expectedPhotos,
                            favorite = false,
                        )
                }

            placeDetailsViewModel = PlaceDetailsViewModel(repository, summaryRepository)
            placeDetailsViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PlaceDetailsUIState())

                placeDetailsViewModel.loadDetails("")
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    PlaceDetailsUIState(
                        placeDetailsDataState =
                            PlacesDetailDataState.Success(
                                placeDetails = expectedPlaceDetails,
                                photos = expectedPhotos,
                                isFavorite = false,
                            ),
                    ),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDetails updates uiState with an empty photo list when repository returns success with no photos`() =
        runTest {
            val expectedPlaceDetails = BaseExamplePlaceDetails
            val expectedPhotos = emptyList<Photo>()

            repository =
                FakePlacesRepository().apply {
                    placeDetailsResult =
                        PlaceDetailsResult.DetailsSuccess(
                            placeDetails = expectedPlaceDetails,
                            photos = expectedPhotos,
                            favorite = false,
                        )
                }

            placeDetailsViewModel = PlaceDetailsViewModel(repository, summaryRepository)
            placeDetailsViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PlaceDetailsUIState())

                placeDetailsViewModel.loadDetails("")
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    PlaceDetailsUIState(
                        placeDetailsDataState =
                            PlacesDetailDataState.Success(
                                placeDetails = expectedPlaceDetails,
                                photos = expectedPhotos,
                                isFavorite = false,
                            ),
                    ),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDetails preserves all photos when repository returns multiple photos`() =
        runTest {
            val expectedPlaceDetails = BaseExamplePlaceDetails
            val expectedPhotos =
                listOf(
                    Photo("https://example.com/photo.jpg"),
                    Photo("https://example.com/photo2.jpg"),
                )

            repository =
                FakePlacesRepository().apply {
                    placeDetailsResult =
                        PlaceDetailsResult.DetailsSuccess(
                            placeDetails = expectedPlaceDetails,
                            photos = expectedPhotos,
                            favorite = false,
                        )
                }

            placeDetailsViewModel = PlaceDetailsViewModel(repository, summaryRepository)
            placeDetailsViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PlaceDetailsUIState())

                placeDetailsViewModel.loadDetails("")
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    PlaceDetailsUIState(
                        placeDetailsDataState =
                            PlacesDetailDataState.Success(
                                placeDetails = expectedPlaceDetails,
                                photos = expectedPhotos,
                                isFavorite = false,
                            ),
                    ),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDetails requests place details using the provided place id`() =
        runTest {
            repository =
                FakePlacesRepository().apply {
                    placeDetailsResult = PlaceDetailsResult.DetailsError.Unknown
                }
            placeDetailsViewModel = PlaceDetailsViewModel(repository, summaryRepository)

            placeDetailsViewModel.loadDetails("place-123")
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(repository.lastRequestedPlaceId).isEqualTo("place-123")
        }

    @Test
    fun `loadDetails updates uiState to Failure when repository returns a network error`() =
        runTest {
            repository =
                FakePlacesRepository().apply {
                    placeDetailsResult = PlaceDetailsResult.DetailsError.Network
                }
            placeDetailsViewModel = PlaceDetailsViewModel(repository, summaryRepository)

            placeDetailsViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PlaceDetailsUIState())

                placeDetailsViewModel.loadDetails(id = "")
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    PlaceDetailsUIState(
                        placeDetailsDataState =
                            PlacesDetailDataState.Failure(
                                "Please check your internet connection and try again.",
                            ),
                    ),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDetails updates uiState to Failure when repository returns a backend error`() =
        runTest {
            val fakePlacesRepository =
                FakePlacesRepository().apply {
                    placeDetailsResult = PlaceDetailsResult.DetailsError.Backend
                }
            placeDetailsViewModel = PlaceDetailsViewModel(fakePlacesRepository, summaryRepository)

            placeDetailsViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PlaceDetailsUIState())

                placeDetailsViewModel.loadDetails("")
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    PlaceDetailsUIState(
                        placeDetailsDataState =
                            PlacesDetailDataState.Failure(
                                "We're having trouble reaching the Google API servers right now. Please try again in a moment.",
                            ),
                    ),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDetails updates uiState to Failure when repository returns an unknown error`() =
        runTest {
            val fakePlacesRepository =
                FakePlacesRepository().apply {
                    placeDetailsResult = PlaceDetailsResult.DetailsError.Unknown
                }
            placeDetailsViewModel = PlaceDetailsViewModel(fakePlacesRepository, summaryRepository)

            placeDetailsViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PlaceDetailsUIState())

                placeDetailsViewModel.loadDetails("")
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    PlaceDetailsUIState(
                        placeDetailsDataState =
                            PlacesDetailDataState.Failure(
                                "An unknown error occurred.",
                            ),
                    ),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onBackClicked resets uiState to Loading`() =
        runTest {
            val fakePlacesRepository =
                FakePlacesRepository().apply {
                    placeDetailsResult =
                        PlaceDetailsResult.DetailsSuccess(
                            placeDetails = BaseExamplePlaceDetails,
                            photos = BaseExamplePhotos,
                            favorite = false,
                        )
                }

            placeDetailsViewModel = PlaceDetailsViewModel(fakePlacesRepository, summaryRepository)

            placeDetailsViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PlaceDetailsUIState())

                placeDetailsViewModel.loadDetails("")
                testDispatcher.scheduler.advanceUntilIdle()
                awaitItem()

                placeDetailsViewModel.onBackClicked()
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(PlaceDetailsUIState())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `askAi updates summaryDataState to Success when summary repository returns text`() =
        runTest {
            repository =
                FakePlacesRepository().apply {
                    placeDetailsResult =
                        PlaceDetailsResult.DetailsSuccess(
                            placeDetails = BaseExamplePlaceDetails,
                            photos = BaseExamplePhotos,
                            favorite = false,
                        )
                }

            summaryRepository =
                FakeSummaryRepository().apply {
                    summaryResult =
                        SummaryResult.SummarySuccess(
                            summaryText = "Popular lunch spot with strong ratings.",
                        )
                }

            placeDetailsViewModel = PlaceDetailsViewModel(repository, summaryRepository)

            placeDetailsViewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PlaceDetailsUIState())

                placeDetailsViewModel.loadDetails("")
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isEqualTo(
                    PlaceDetailsUIState(
                        placeDetailsDataState =
                            PlacesDetailDataState.Success(
                                placeDetails = BaseExamplePlaceDetails,
                                photos = BaseExamplePhotos,
                                isFavorite = false,
                            ),
                    ),
                )

                placeDetailsViewModel.askAi()
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(
                    PlaceDetailsUIState(
                        placeDetailsDataState =
                            PlacesDetailDataState.Success(
                                placeDetails = BaseExamplePlaceDetails,
                                photos = BaseExamplePhotos,
                                isFavorite = false,
                            ),
                        summaryDataState = PlacesDetailSummaryDataState.Loading,
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    PlaceDetailsUIState(
                        placeDetailsDataState =
                            PlacesDetailDataState.Success(
                                placeDetails = BaseExamplePlaceDetails,
                                photos = BaseExamplePhotos,
                                isFavorite = false,
                            ),
                        summaryDataState =
                            PlacesDetailSummaryDataState.Success(
                                summaryText = "Popular lunch spot with strong ratings.",
                            ),
                    ),
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    companion object {
        val BaseExamplePlaceDetails =
            PlaceDetails(
                restaurantName = "Cafe",
                id = "123",
                rating = 4.5,
                googleMapsUri = null,
                userRatingCount = 100,
                formattedAddress = "123 Main St",
                nationalPhoneNumber = "555-1234",
                openingHours =
                    listOf(
                        "Monday: 8:30AM–3:30PM",
                        "Tuesday: 8:30AM–3:30PM",
                        "Wednesday: 8:30AM–3:30PM, 5:30–10:00PM",
                        "Thursday: 8:30AM–3:30PM, 5:30–10:00PM",
                        "Friday: 8:30AM–3:30PM, 5:30–10:00PM",
                        "Saturday: 8:30AM–4:00PM, 5:30–10:00PM",
                        "Sunday: 8:30AM–4:00PM",
                    ),
            )

        val BaseExamplePhotos =
            listOf(
                Photo("https://example.com/photo.jpg"),
            )
    }
}
