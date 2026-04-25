package com.atlunch.data.repository

import com.atlunch.data.IoDispatcher
import com.atlunch.data.network.OpenAiClient
import com.atlunch.data.network.OpenAiRequest
import com.atlunch.data.toPlacesDomainError
import com.atlunch.data.toSummaryDomainError
import com.atlunch.domain.SummaryRepository
import com.atlunch.domain.SummaryResult
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

class SummaryRepositoryImpl @Inject constructor(
    val openAiClient: OpenAiClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SummaryRepository {

    override fun getSummary(): Flow<SummaryResult>  = flow<SummaryResult> {
        val request = OpenAiRequest(
            model = OPEN_AI_MODEL,
            instructions = OPEN_SUMMARY_PROMPT,
            input = OPEN_SUMMARY_PROMPT
        )
        val response = openAiClient.generatePlacesSummary(request)
        emit(SummaryResult.SummarySuccess(response.output.first().content.first().text?:"ChatGPT response string not found.")) // update later to domain
    }.catch { throwable ->
        if (throwable is CancellationException){
            throw throwable
        }

        emit(throwable.toSummaryDomainError())
    }.flowOn(ioDispatcher)

    companion object {
        const val OPEN_AI_MODEL = "gpt-5.4"
        const val OPEN_SUMMARY_PROMPT =
            "Write one short, helpful sentence explaining why Disney World is so wonderful."
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SummaryRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSummaryRepository(summaryRepositoryImpl: SummaryRepositoryImpl): SummaryRepository
}