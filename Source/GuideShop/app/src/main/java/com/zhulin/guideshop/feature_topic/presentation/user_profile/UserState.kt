package com.zhulin.guideshop.feature_topic.presentation.user_profile


data class UserState(
    val avatar: String = "",
    val nickname: String = "",
    val profile: String = "",
    val followers: Int = 0,
)
