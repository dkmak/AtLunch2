package com.atlunch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.atlunch.domain.PlacePreview
import com.atlunch.ui.listplaces.DisplayPlacesList
import com.atlunch.ui.listplaces.ListDestination
import com.atlunch.ui.listplaces.ListPlacesScreen
import com.atlunch.ui.listplaces.ListPlacesUiState
import com.atlunch.ui.listplaces.ListViewModel
import com.atlunch.ui.placedetails.DetailsUiState
import com.atlunch.ui.placedetails.PlaceDetailsScreen
import com.atlunch.ui.placedetails.PlaceDetailsViewModel
import com.atlunch.ui.theme.AtLunchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import java.util.Map.entry

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtLunchTheme {
                AtLunchApp()
            }
        }
    }
}

@Serializable
data class DetailsDestination(
    val placeId: String
): NavKey


@Composable
fun AtLunchApp() {
    val navBackStack = rememberNavBackStack(ListDestination)
    NavDisplay(
        backStack = navBackStack,
        entryProvider = entryProvider {
            entry<ListDestination> {
                ListPlacesScreen(
                    onPlacePreviewClicked = {id ->navBackStack.add(DetailsDestination(placeId = id))}
                )
            }

            entry<DetailsDestination> { detailsDestination ->
                PlaceDetailsScreen(
                    placeId = detailsDestination.placeId,
                    onBackClicked = {navBackStack.removeLastOrNull()}
                )
            }
        },
        transitionSpec = {
            slideInHorizontally() togetherWith
                    scaleOut()
        },
        popTransitionSpec = {
            scaleIn() togetherWith
                    slideOutHorizontally()
        }
    )
}