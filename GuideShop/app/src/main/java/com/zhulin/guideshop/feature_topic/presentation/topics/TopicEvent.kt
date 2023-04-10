package com.zhulin.guideshop.feature_topic.presentation.topics


sealed class TopicEvent {
    data class ChangeSearch(val value: String) : TopicEvent()
    data class ChangeScreenFilter(val value: ScreenFilterEnum) : TopicEvent()
    data class ChangeCategoryId(val value: Int) : TopicEvent()
    data class ChangeAuthorLogin(val value: Int) : TopicEvent()
    data class Subscribe(val value: String) : TopicEvent()
    data class Unsubscribe(val value: String) : TopicEvent()

    data class Like(val value: Int) : TopicEvent()
    data class Unlike(val value: Int) : TopicEvent()
    object ArticleRefresh : TopicEvent()
    object OnlyArticleRefresh : TopicEvent()
    object CreateArticle : TopicEvent()

}