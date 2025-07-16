package com.example.criticaltech.domain.repository

import com.example.criticaltech.domain.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getTopHeadlines(source: String): Flow<List<Article>>
    suspend fun refreshTopHeadlines(source: String)
}