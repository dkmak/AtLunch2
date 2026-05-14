package dkmak.atlunch

import dkmak.atlunch.domain.PlaceDetails
import dkmak.atlunch.domain.SummaryRepository
import dkmak.atlunch.domain.SummaryResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeSummaryRepository : SummaryRepository {
    var summaryResult: SummaryResult = SummaryResult.SummaryError.Unknown

    override fun getSummary(placeDetails: PlaceDetails): Flow<SummaryResult> = flowOf(summaryResult)
}
