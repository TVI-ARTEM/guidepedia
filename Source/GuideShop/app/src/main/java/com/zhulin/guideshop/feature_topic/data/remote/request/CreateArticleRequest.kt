package com.zhulin.guideshop.feature_topic.data.remote.request

data class CreateArticleRequest(
    val content: String,
    val description: String,
    val preview: String,
    val published: Boolean,
    val title: String
)