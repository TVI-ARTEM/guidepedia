package com.zhulin.guideshop.feature_topic.data.remote.responses

data class Article(
    val content: String,
    val createdAt: String,
    val description: String,
    val id: Int,
    val preview: String,
    val published: Boolean,
    val title: String,
    val updatedAt: String,
    val userId: Int
)