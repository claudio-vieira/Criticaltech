package com.example.criticaltech.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criticaltech.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    init {
        println("comeco:")
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            val list = newsRepository.getTopHeadlines("bbc-news")
            list.forEach {
                println("titulo: "+it.title)
                println(it.toString())
            }
        }
    }
}