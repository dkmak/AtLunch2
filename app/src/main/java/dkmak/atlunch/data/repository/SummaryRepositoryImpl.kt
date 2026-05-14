package dkmak.atlunch.data.repository

import dkmak.atlunch.data.IoDispatcher
import dkmak.atlunch.data.network.OpenAiClient
import dkmak.atlunch.data.network.OpenAiRequest
import dkmak.atlunch.data.toSummaryDomainError
import dkmak.atlunch.domain.PlaceDetails
import dkmak.atlunch.domain.SummaryRepository
import dkmak.atlunch.domain.SummaryResult
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

class SummaryRepositoryImpl
    @Inject
    constructor(
        val openAiClient: OpenAiClient,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : SummaryRepository {
        override fun getSummary(placeDetails: PlaceDetails): Flow<SummaryResult> =
            flow<SummaryResult> {
                val request =
                    OpenAiRequest(
                        model = OPEN_AI_MODEL,
                        instructions = OPEN_SUMMARY_PROMPT,
                        input = placeDetails.toSummaryInput(),
                    )
                val response = openAiClient.generatePlacesSummary(request)
                val summaryText =
                    response.output
                        .asSequence()
                        .flatMap { outputItem -> outputItem.content.asSequence() }
                        .mapNotNull { contentItem -> contentItem.text?.trim() }
                        .filter { text -> text.isNotEmpty() }
                        .joinToString(separator = " ")

                emit(SummaryResult.SummarySuccess(summaryText))
            }.catch { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }

                emit(throwable.toSummaryDomainError())
            }.flowOn(ioDispatcher)

        private fun PlaceDetails.toSummaryInput(): String {
            val openingHoursText =
                openingHours
                    ?.takeIf { it.isNotEmpty() }
                    ?.joinToString(separator = "; ")
                    ?: "Unavailable"

            return buildString {
                appendLine("Name: $restaurantName")
                appendLine("Rating: ${rating ?: "Unavailable"}")
                appendLine("Reviews: ${userRatingCount ?: "Unavailable"}")
                appendLine("Address: ${formattedAddress ?: "Unavailable"}")
                appendLine("Phone: ${nationalPhoneNumber ?: "Unavailable"}")
                append("Hours: $openingHoursText")
            }
        }

        companion object {
            const val OPEN_AI_MODEL = "gpt-5.4"
            const val OPEN_SUMMARY_PROMPT =
                "Write one short, helpful sentence explaining why this restaurant may be a good lunch choice. Recommend a few lunch items."
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SummaryRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSummaryRepository(summaryRepositoryImpl: SummaryRepositoryImpl): SummaryRepository
}
