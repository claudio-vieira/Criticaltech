package com.example.criticaltech.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.criticaltech.BuildConfig
import com.example.criticaltech.R
import com.example.criticaltech.domain.Article
import com.example.criticaltech.presentation.viewmodel.NewsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    modifier: Modifier = Modifier,
    onArticleClick: (Article) -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = BuildConfig.SOURCE_NAME,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        PullToRefreshBox(
            isRefreshing = viewModel.uiState.collectAsState().value.isLoading,
            onRefresh = {
                viewModel.loadNews()
            },
        ) {

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.articles.isEmpty() -> {
                    if (uiState.error.isNotEmpty()) {
                        Toast.makeText(
                            LocalContext.current,
                            uiState.error,
                            Toast.LENGTH_LONG).show()
                    }
                    EmptyScreen(
                        onRetry = { viewModel.loadNews() }
                    )
                }

                else -> {
                    if (uiState.error.isNotEmpty()) {
                        Toast.makeText(
                            LocalContext.current,
                            uiState.error,
                            Toast.LENGTH_LONG).show()
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(dimensionResource(R.dimen.content_padding)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.item_spacing))
                    ) {
                        items(uiState.articles) { article ->
                            NewsItem(
                                article = article,
                                onClick = { onArticleClick(article) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsItem(
    article: Article,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.card_padding))
        ) {
            article.urlToImage?.let { imageUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.article_image_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.article_image_height))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.image_corner_radius))),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            }

            article.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            article.publishedAt?.let {
                Text(
                    text = formatDate(it),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            article.description?.let { description ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EmptyScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.content_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_data_message),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry_button))
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm", Locale.getDefault())
        instant.atZone(ZoneId.systemDefault()).format(formatter)
    } catch (e: Exception) {
        dateString
    }
}