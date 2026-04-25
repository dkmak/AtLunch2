package com.atlunch.domain

import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    fun getSummary(): Flow<SummaryResult> // pass getDetails
}