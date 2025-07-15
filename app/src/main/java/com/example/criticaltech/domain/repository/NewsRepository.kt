package com.example.criticaltech.domain.repository

import com.example.criticaltech.data.ArticleDto

interface NewsRepository {
    suspend fun getTopHeadlines(source: String): List<ArticleDto>
}