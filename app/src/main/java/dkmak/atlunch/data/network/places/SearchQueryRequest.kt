package dkmak.atlunch.data.network.places

import kotlinx.serialization.Serializable

@Serializable
data class SearchQueryRequest(
    val textQuery: String,
    val includedType: String? = null,
    val pageSize: Int? = null,
    val locationBias: LocationBias? = null,
)

@Serializable
data class LocationBias(
    val circle: Circle,
)