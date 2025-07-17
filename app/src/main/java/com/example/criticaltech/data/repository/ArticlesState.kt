package com.example.criticaltech.data.repository

import com.example.criticaltech.domain.Article

sealed class ArticlesState {
    data class Success(val articles: List<Article>) : ArticlesState()
    data class Error(val articles: List<Article>, val message: String) : ArticlesState()
    data class NoNetwork(val articles: List<Article>, val message: String) : ArticlesState()
}