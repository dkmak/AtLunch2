package com.atlunch

import com.atlunch.domain.PlacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceDetailsViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: PlacesRepository
    private lateinit var placeDetailsViewModel: PlacesRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun after() {
        Dispatchers.resetMain()
    }


    @Test
    fun `loadDetails updates uiState to Success when repository returns place details`() {
    }

    @Test
    fun `loadDetails updates uiState with an empty photo list when repository returns success with no photos`(){

    }

    @Test
    fun `loadDetails preserves all photos when repository returns multiple photos`(){

    }

    @Test
    fun `loadDetails requests place details using the provided place id`(){

    }

    @Test
    fun `loadDetails updates uiState to Failure when repository returns a network error`(){

    }

    @Test
    fun `loadDetails updates uiState to Failure when repository returns a backend error`(){

    }

    @Test
    fun `loadDetails updates uiState to Failure when repository returns an unknown error`(){

    }

    @Test
    fun `onBackClicked resets uiState to Loading`(){

    }

}