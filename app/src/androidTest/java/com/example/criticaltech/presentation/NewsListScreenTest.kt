package com.example.criticaltech.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.criticaltech.domain.Article
import com.example.criticaltech.domain.Source
import com.example.criticaltech.presentation.screen.NewsItem
import com.example.criticaltech.presentation.ui.theme.CriticaltechTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun newsItem_displaysCorrectContent() {
        // Given
        val article = Article(
            source = Source("bbc-news", "BBC News"),
            author = "Test Author",
            title = "Test Article Title",
            description = "Test article description",
            url = "https://test.com",
            urlToImage = "https://test.com/image.jpg",
            publishedAt = "2024-01-01T00:00:00Z",
            content = "Test content"
        )

        // When
        composeTestRule.setContent {
            CriticaltechTheme {
                NewsItem(
                    article = article,
                    onClick = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Test Article Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test article description").assertIsDisplayed()
    }

    @Test
    fun newsItem_clickable() {
        // Given
        val article = Article(
            source = Source("bbc-news", "BBC News"),
            author = "Test Author",
            title = "Test Article Title",
            description = "Test article description",
            url = "https://test.com",
            urlToImage = null,
            publishedAt = "2024-01-01T00:00:00Z",
            content = "Test content"
        )
        var clicked = false

        // When
        composeTestRule.setContent {
            CriticaltechTheme {
                NewsItem(
                    article = article,
                    onClick = { clicked = true }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Test Article Title").performClick()
        assert(clicked)
    }
}