package com.zhulin.guideshop.feature_topic.data.remote.responses

data class ArticleCommentsListItem(
    val articleId: Int,
    val content: String,
    val createdAt: String,
    val id: Int,
    val updatedAt: String,
    val userId: Int
)