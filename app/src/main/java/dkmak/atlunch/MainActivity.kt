package dkmak.atlunch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dkmak.atlunch.ui.listplaces.PlaceDestination
import dkmak.atlunch.ui.listplaces.PlacesScreen
import dkmak.atlunch.ui.placedetails.PlaceDetailsScreen
import dkmak.atlunch.ui.theme.AtLunchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

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
    val placeId: String,
) : NavKey

@Composable
fun AtLunchApp() {
    val navBackStack = rememberNavBackStack(PlaceDestination)
    NavDisplay(
        backStack = navBackStack,
        entryProvider =
            entryProvider {
                entry<PlaceDestination> {
                    PlacesScreen(
                        onPlacePreviewClicked = { id -> navBackStack.add(DetailsDestination(placeId = id)) },
                    )
                }

                entry<DetailsDestination> { detailsDestination ->
                    PlaceDetailsScreen(
                        placeId = detailsDestination.placeId,
                        onBackClicked = { navBackStack.removeLastOrNull() },
                    )
                }
            },
        transitionSpec = {
            slideInHorizontally { it } togetherWith
                slideOutHorizontally { -it }
        },
        popTransitionSpec = {
            slideInHorizontally { -it } togetherWith
                slideOutHorizontally { it }
        },
    )
}
