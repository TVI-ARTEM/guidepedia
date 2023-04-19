package com.zhulin.guideshop.feature_topic.presentation.watch_topic

import com.zhulin.guideshop.feature_topic.data.remote.responses.Article
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentsList
import com.zhulin.guideshop.feature_topic.data.remote.responses.UserInfoResponse

data class ArticleState(
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

    val authorInfo: UserInfoResponse = UserInfoResponse(
        id = 0,
        avatar = "",
        login = "",
        nickname = "",
        profile = "",
        subscriptions = 0
    ),

    val comments: ArticleCommentsList = ArticleCommentsList()
)
