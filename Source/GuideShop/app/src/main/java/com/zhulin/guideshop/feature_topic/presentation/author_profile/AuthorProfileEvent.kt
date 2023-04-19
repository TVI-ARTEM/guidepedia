package com.zhulin.guideshop.feature_topic.presentation.author_profile

import com.zhulin.guideshop.feature_topic.presentation.topics.TopicEvent

sealed class AuthorProfileEvent {
    object Refresh : AuthorProfileEvent()
    object ShowProfile : AuthorProfileEvent()
    object ShowArticles : AuthorProfileEvent()
    object Subscribe : AuthorProfileEvent()
    object Unsubscribe : AuthorProfileEvent()

    data class Like(val value: Int) : AuthorProfileEvent()
    data class Unlike(val value: Int) : AuthorProfileEvent()

    data class Save(val value: Int) : AuthorProfileEvent()
    data class Unsave(val value: Int) : AuthorProfileEvent()
}