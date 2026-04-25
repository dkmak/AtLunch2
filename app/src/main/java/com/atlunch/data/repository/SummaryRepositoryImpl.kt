package com.atlunch.data.repository

import com.atlunch.domain.SummaryRepository
import com.atlunch.domain.SummaryResult
import kotlinx.coroutines.flow.Flow

class SummaryRepositoryImpl: SummaryRepository {
    override fun getSummary(): Flow<SummaryResult> {
        TODO("Not yet implemented")
    }
}