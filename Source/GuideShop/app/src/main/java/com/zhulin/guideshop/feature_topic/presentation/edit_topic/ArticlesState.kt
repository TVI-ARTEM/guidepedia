package com.zhulin.guideshop.feature_topic.presentation.edit_topic

import com.zhulin.guideshop.feature_topic.data.remote.responses.Article

data class ArticlesState(
    val article: Article = Article(
        content = "",
        title = "",
        createdAt = "",
        description = "",
        id = 0,
        preview = "",
        published = false,
        updatedAt = "",
        userId = 0
    ),
)
