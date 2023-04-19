package com.zhulin.guideshop.feature_topic.presentation.authorization

data class SignInState(
    val login: String = "",
    val password: String = "",
    val hintLogin: String,
    val hintPassword: String
)
