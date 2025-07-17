package com.example.criticaltech.data.repository

import com.example.criticaltech.data.local.NewsDao
import com.example.criticaltech.data.mapper.toArticleEntity
import com.example.criticaltech.data.mapper.toDomain
import com.example.criticaltech.data.remote.NewsApi
import com.example.criticaltech.domain.repository.NewsRepository
import com.example.criticaltech.util.Constants.API_ERROR_MESSAGE
import com.example.criticaltech.util.Constants.NO_INTERNET_CONNECTION
import com.example.criticaltech.util.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val newsDao: NewsDao,
    private val networkUtils: NetworkUtils
) : NewsRepository {

    override fun getTopHeadlines(source: String): Flow<ArticlesState> {
        return flow {
            val cachedArticles = newsDao.getAllArticles().first().map { it.toDomain() }

            if (!networkUtils.isNetworkAvailable()) {
                emit(ArticlesState.NoNetwork(cachedArticles, NO_INTERNET_CONNECTION))
                return@flow
            }

            runCatching {
                refreshTopHeadlines(source)
            }.onFailure { exception ->
                emit(ArticlesState.Error(cachedArticles, API_ERROR_MESSAGE))
                return@flow
            }

            //Emit fresh data after successful refresh
            newsDao.getAllArticles().first().map { it.toDomain() }.let { articles ->
                emit(ArticlesState.Success(articles))
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
            e.printStackTrace()
            throw Exception(API_ERROR_MESSAGE)
        }
    }
}