package com.zhulin.guideshop.feature_topic.presentation.watch_topic

sealed class WatchTopicEvent {
    object ChangeRaw : WatchTopicEvent()
    object Refresh : WatchTopicEvent()
    data class AddComment(val value: String): WatchTopicEvent()
    data class RemoveComment(val value: Int): WatchTopicEvent()
}