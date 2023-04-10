package com.zhulin.guideshop.feature_topic.presentation.authorization


data class SignUpState(
    val login: String = "",
    val nickname: String = "",
    val password: String = "",
    val hintLogin: String,
    val hintNickname: String,
    val hintPassword: String
)
