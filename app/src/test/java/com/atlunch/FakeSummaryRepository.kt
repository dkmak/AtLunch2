package com.atlunch

import com.atlunch.domain.SummaryRepository
import com.atlunch.domain.SummaryResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeSummaryRepository : SummaryRepository {
    var summaryResult: SummaryResult = SummaryResult.SummaryError.Unknown

    override fun getSummary(): Flow<SummaryResult> {
        return flowOf(summaryResult)
    }
}
