package com.example.criticaltech.domain

import com.example.criticaltech.domain.repository.NewsRepository
import com.example.criticaltech.domain.usecase.GetTopHeadlinesUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetTopHeadlinesUseCaseTest {

    @Mock
    private lateinit var repository: NewsRepository
    private lateinit var getTopHeadlinesUseCase: GetTopHeadlinesUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getTopHeadlinesUseCase = GetTopHeadlinesUseCase(repository)
    }

    @Test
    fun `invoke should return articles from repository`() = runTest {
        // Given
        val source = "bbc-news"
        val articles = listOf(
            Article(
                source = Source("bbc-news", "BBC News"),
                author = "Test Author",
                title = "Test Title",
                description = "Test Description",
                url = "https://test.com",
                urlToImage = "https://test.com/image.jpg",
                publishedAt = "2024-01-01T00:00:00Z",
                content = "Test Content"
            )
        )
        whenever(repository.getTopHeadlines(source)).thenReturn(flowOf(articles))

        // When
        val result = getTopHeadlinesUseCase(source).toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(articles, result[0])
    }
}