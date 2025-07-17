package com.example.criticaltech.domain.usecase

import com.example.criticaltech.data.repository.ArticlesState
import com.example.criticaltech.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * This use case is responsible for getting the top headlines from the repository.
 * It takes a source parameter and returns a flow of articles.
 * */
class GetTopHeadlinesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(source: String): Flow<ArticlesState> {
        return repository.getTopHeadlines(source)
    }
}