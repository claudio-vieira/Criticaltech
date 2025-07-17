package com.example.criticaltech.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criticaltech.BuildConfig
import com.example.criticaltech.data.repository.ArticlesState
import com.example.criticaltech.domain.Article
import com.example.criticaltech.domain.usecase.GetTopHeadlinesUseCase
import com.example.criticaltech.util.Constants.API_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    /**
     * These use cases are responsible to get the top headlines from the repository
     * and refresh the news. This makes the code easier to understand, test, and maintain.
     * */
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            getTopHeadlinesUseCase(BuildConfig.NEWS_SOURCE)
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: API_ERROR_MESSAGE
                    )
                }
                .collect { articles ->
                    when (val state = articles) {
                        is ArticlesState.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                articles = state.articles,
                                error = state.message
                            )
                        }
                        is ArticlesState.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                articles = state.articles.sortedByDescending { it.publishedAt },
                                error = ""
                            )
                        }
                        is ArticlesState.NoNetwork -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                articles = state.articles,
                                error = state.message
                            )
                        }
                    }
                }
        }
    }
}

data class NewsUiState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val error: String = ""
)