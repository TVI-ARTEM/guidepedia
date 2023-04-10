package com.zhulin.guideshop.core.presentation.util

sealed class Screen(val route: String) {
    object TopicsScreen : Screen("topics")
    object EditTopicScreen : Screen("edit_topic")
    object AuthorizationScreen : Screen("authorization")
    object UserProfileScreen : Screen("user_profile")
    object AuthorProfileScreen : Screen("author_profile")
    object WatchTopicScreen : Screen("watch_topic")
    object SettingsScreen : Screen("settings")
}
