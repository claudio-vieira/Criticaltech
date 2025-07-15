package com.example.criticaltech.data.repository

import com.example.criticaltech.data.ArticleDto
import com.example.criticaltech.data.remote.NewsApi
import com.example.criticaltech.domain.repository.NewsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi
) : NewsRepository {

    override suspend fun getTopHeadlines(source: String): List<ArticleDto> {
        val response = newsApi.getTopHeadlines(source)
        val entities = response.articles
        return entities
    }
}