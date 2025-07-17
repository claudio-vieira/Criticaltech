package com.example.criticaltech.domain.repository

import com.example.criticaltech.data.repository.ArticlesState
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getTopHeadlines(source: String): Flow<ArticlesState>
    suspend fun refreshTopHeadlines(source: String)
}