package com.zhulin.guideshop.feature_topic.presentation.user_profile

import com.zhulin.guideshop.feature_topic.data.remote.responses.BankDetailsList

data class UserScreenState(
    val profile: String = "",
    val nickname: String = "",
    val avatar: String = "",
    val followers: Int = 0,
    val bankDetailsList: BankDetailsList = BankDetailsList(details = emptyList())
)