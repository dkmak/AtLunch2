package com.atlunch.ui.placedetails

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailsShareSheet(
    sheetState: SheetState,
    placeDetailsDataState: PlacesDetailDataState.Success,
    onDismiss: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        val placeDetailsDataState = placeDetailsDataState
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Share ${placeDetailsDataState?.placeDetails?.restaurantName ?: ""}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )

            Row {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion { onDismiss() }

                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, placeDetailsDataState?.placeDetails?.formattedAddress?:"No Address Found.")
                            putExtra(Intent.EXTRA_TITLE, "Share Restaurant Address")
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("Share Address")
                }
                Button(
                    onClick = {
                        val googleMapsUri = placeDetailsDataState?.placeDetails?.googleMapsUri

                        if(googleMapsUri.isNullOrBlank()){
                            Toast
                                .makeText(context, "No Google Maps Uri Found.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val uri = Uri.parse(googleMapsUri)
                            val mapsIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage(GOOGLE_MAPS_URI_PACKAGE)
                            }

                            try {
                                context.startActivity(mapsIntent)
                            } catch (e: ActivityNotFoundException){
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        }
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("Open In Google Maps")
                }
            }
        }
    }
}

private const val GOOGLE_MAPS_URI_PACKAGE = "com.google.android.apps.maps"
