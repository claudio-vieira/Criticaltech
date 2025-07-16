package com.example.criticaltech.domain.usecase

import com.example.criticaltech.domain.repository.NewsRepository
import javax.inject.Inject

class RefreshNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(source: String) {
        repository.refreshTopHeadlines(source)
    }
}