package com.example.criticaltech.presentation.navigation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.criticaltech.domain.Article
import com.example.criticaltech.domain.Source
import com.example.criticaltech.presentation.screen.ArticleDetailScreen
import com.example.criticaltech.presentation.ui.NewsListScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleParcelable(
    val sourceId: String?,
    val sourceName: String?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
) : Parcelable

fun Article.toParcelable(): ArticleParcelable {
    return ArticleParcelable(
        sourceId = source.id,
        sourceName = source.name,
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

@Composable
fun NewsNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "news_list"
    ) {
        composable("news_list") {
            NewsListScreen(
                modifier = modifier,
                onArticleClick = { article ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "article",
                        article.toParcelable()
                    )
                    navController.navigate("article_detail")
                }
            )
        }

        composable("article_detail") {
            val article = navController.previousBackStackEntry?.savedStateHandle?.get<ArticleParcelable>("article")
            article?.let { articleParcelable ->
                ArticleDetailScreen(
                    modifier = modifier,
                    article = Article(
                        source = Source(
                            articleParcelable.sourceId,
                            articleParcelable.sourceName
                        ),
                        author = articleParcelable.author,
                        title = articleParcelable.title,
                        description = articleParcelable.description,
                        url = articleParcelable.url,
                        urlToImage = articleParcelable.urlToImage,
                        publishedAt = articleParcelable.publishedAt,
                        content = articleParcelable.content
                    ),
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}