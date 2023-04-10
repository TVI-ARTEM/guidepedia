package com.zhulin.guideshop.feature_topic.data.remote.request

data class RegistrationRequest(
    val login: String,
    val nickname: String,
    val password: String
)