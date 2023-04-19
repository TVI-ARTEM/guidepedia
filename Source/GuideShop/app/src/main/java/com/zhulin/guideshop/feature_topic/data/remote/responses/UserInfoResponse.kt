package com.zhulin.guideshop.feature_topic.data.remote.responses

data class UserInfoResponse(
    val id: Int,
    val avatar: String,
    val login: String,
    val nickname: String,
    val profile: String,
    val subscriptions: Int
)