package com.zhulin.guideshop.feature_topic.presentation.author_profile

import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleList

data class ArticlesState(
    val all: ArticleList = ArticleList(emptyList())
)
