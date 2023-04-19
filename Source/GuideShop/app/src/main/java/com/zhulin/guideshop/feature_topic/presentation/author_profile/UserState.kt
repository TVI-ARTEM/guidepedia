package com.zhulin.guideshop.feature_topic.presentation.author_profile

data class UserState(
    val login: String = "",
    val nickname: String = "",
    val avatar: String = "",
    val followed: Boolean = false
)