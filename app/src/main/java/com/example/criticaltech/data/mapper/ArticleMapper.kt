package com.example.criticaltech.data.mapper

import com.example.criticaltech.data.ArticleDto
import com.example.criticaltech.data.local.ArticleEntity
import com.example.criticaltech.domain.Article
import com.example.criticaltech.domain.Source

fun ArticleDto.toArticleEntity(): ArticleEntity {
    return ArticleEntity(
        url = url,
        sourceId = source.id,
        sourceName = source.name,
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

fun ArticleEntity.toDomain(): Article {
    return Article(
        source = Source(sourceId, sourceName),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}