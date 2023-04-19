package com.zhulin.guideshop.feature_topic.data.remote.request

data class UpdateArticleRequest(
    val content: String,
    val description: String,
    val id: Int,
    val preview: String,
    val published: Boolean,
    val title: String
)