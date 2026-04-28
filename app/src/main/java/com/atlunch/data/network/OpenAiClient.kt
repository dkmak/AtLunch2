package com.atlunch.data.network

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject

class OpenAiClient
    @Inject
    constructor(
        val openAiClient: OpenApiService,
    ) {
        suspend fun generatePlacesSummary(request: OpenAiRequest): OpenAiResponse = openAiClient.generatePlacesSummary(request)
    }

interface OpenApiService {
    @POST("v1/responses")
    suspend fun generatePlacesSummary(
        @Body request: OpenAiRequest,
    ): OpenAiResponse
}

@Serializable
data class OpenAiResponse(
    val output: List<OpenAiOutputItem> = emptyList(),
)

@Serializable
data class OpenAiOutputItem(
    val content: List<OpenAiOutputDTO> = emptyList(),
)

@Serializable
data class OpenAiOutputDTO(
    val text: String? = null,
)

@Serializable
data class OpenAiRequest(
    val model: String,
    val instructions: String,
    val input: String,
)
