package com.example.criticaltech.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.criticaltech.domain.Article
import com.example.criticaltech.domain.Source
import com.example.criticaltech.domain.usecase.GetTopHeadlinesUseCase
import com.example.criticaltech.domain.usecase.RefreshNewsUseCase
import com.example.criticaltech.presentation.viewmodel.NewsViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class NewsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var getTopHeadlinesUseCase: GetTopHeadlinesUseCase

    @Mock
    private lateinit var refreshNewsUseCase: RefreshNewsUseCase

    private lateinit var viewModel: NewsViewModel

    // Test data constants
    private val testArticles = listOf(
        Article(
            source = Source("bbc-news", "BBC News"),
            author = "Test Author",
            title = "Test Title",
            description = "Test Description",
            url = "https://test.com",
            urlToImage = "https://test.com/image.jpg",
            publishedAt = "2024-01-01T00:00:00Z",
            content = "Test Content"
        ),
        Article(
            source = Source("cnn", "CNN"),
            author = "Another Author",
            title = "Another Title",
            description = "Another Description",
            url = "https://cnn.com",
            urlToImage = "https://cnn.com/image.jpg",
            publishedAt = "2024-01-02T00:00:00Z",
            content = "Another Content"
        )
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load empty articles and stop loading`() = runTest {
        // Given
        whenever(getTopHeadlinesUseCase("bbc-news"))
            .thenReturn(flowOf(emptyList()))

        // When
        viewModel = NewsViewModel(getTopHeadlinesUseCase, refreshNewsUseCase)

        // Then
        with(viewModel.uiState.value) {
            assertFalse(isLoading)
            assertTrue(articles.isEmpty())
            assertEquals("", error)
        }
    }

    @Test
    fun `loadNews should update state with articles and stop loading`() = runTest {
        // Given
        whenever(getTopHeadlinesUseCase("bbc-news"))
            .thenReturn(flowOf(testArticles))

        // When
        viewModel = NewsViewModel(getTopHeadlinesUseCase, refreshNewsUseCase)

        // Then
        with(viewModel.uiState.value) {
            assertEquals(testArticles.sortedByDescending { it.publishedAt }, articles)
            assertFalse(isLoading)
            assertEquals("", error)
        }
    }

    @Test
    fun `loadNews should handle empty response correctly`() = runTest {
        // Given
        whenever(getTopHeadlinesUseCase("bbc-news"))
            .thenReturn(flowOf(emptyList()))

        // When
        viewModel = NewsViewModel(getTopHeadlinesUseCase, refreshNewsUseCase)

        // Then
        with(viewModel.uiState.value) {
            assertTrue(articles.isEmpty())
            assertFalse(isLoading)
            assertEquals("", error)
        }
    }

    @Test
    fun `loadNews should handle flow errors and update error state`() = runTest {
        // Given
        val errorMessage = "Network error"
        val errorFlow = flow<List<Article>> {
            throw Exception(errorMessage)
        }
        whenever(getTopHeadlinesUseCase("bbc-news"))
            .thenReturn(errorFlow)

        // When
        viewModel = NewsViewModel(getTopHeadlinesUseCase, refreshNewsUseCase)

        // Then
        with(viewModel.uiState.value) {
            assertTrue(articles.isEmpty())
            assertFalse(isLoading)
            assertEquals(errorMessage, error)
        }
    }

    @Test
    fun `loadNews should handle multiple emissions from flow`() = runTest {
        // Given
        val cachedArticles = listOf(testArticles[0])
        val freshArticles = testArticles
        val multiEmissionFlow = flow {
            emit(cachedArticles) // First emission (cached)
            emit(freshArticles)  // Second emission (fresh from network)
        }
        whenever(getTopHeadlinesUseCase("bbc-news"))
            .thenReturn(multiEmissionFlow)

        // When
        viewModel = NewsViewModel(getTopHeadlinesUseCase, refreshNewsUseCase)

        // Then - Should have the latest emission
        with(viewModel.uiState.value) {
            assertEquals(freshArticles.sortedByDescending { it.publishedAt }, articles)
            assertFalse(isLoading)
            assertEquals("", error)
        }
    }

    @Test
    fun `refreshNews should handle refresh errors`() = runTest {
        // Given
        whenever(getTopHeadlinesUseCase("bbc-news"))
            .thenReturn(flowOf(testArticles))
        whenever(refreshNewsUseCase("bbc-news")).thenAnswer {
            throw Exception("Refresh failed")
        }
        viewModel = NewsViewModel(getTopHeadlinesUseCase, refreshNewsUseCase)

        // When
        viewModel.refreshNews()

        // Then
        verify(refreshNewsUseCase).invoke("bbc-news")
        with(viewModel.uiState.value) {
            assertFalse(isLoading)
            assertEquals("Refresh failed", error)
        }
    }

}