package com.example.criticaltech.data.repository

import com.example.criticaltech.data.local.NewsDao
import com.example.criticaltech.data.mapper.toArticleEntity
import com.example.criticaltech.data.mapper.toDomain
import com.example.criticaltech.data.remote.NewsApi
import com.example.criticaltech.domain.Article
import com.example.criticaltech.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val newsDao: NewsDao
) : NewsRepository {

    override fun getTopHeadlines(source: String): Flow<List<Article>> {
        return newsDao.getAllArticles().map { entities ->
            entities.map { it.toDomain() }
        }.onStart {
            // Auto-refresh if database is empty
            if (newsDao.getAllArticles().first().isEmpty()) {
                refreshTopHeadlines(source)
            }
        }
    }

    override suspend fun refreshTopHeadlines(source: String) {
        try {
            val response = newsApi.getTopHeadlines(source)
            val entities = response.articles.map { it.toArticleEntity() }
            newsDao.clearAllArticles()
            newsDao.insertArticles(entities)
        } catch (e: Exception) {
            // TODO Handle error later
            throw e
        }
    }
}