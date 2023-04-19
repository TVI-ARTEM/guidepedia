package com.zhulin.guideshop.feature_topic.presentation.author_profile

import com.zhulin.guideshop.feature_topic.data.remote.responses.BankDetailsList


data class AuthorState(
    val id: Int = 0,
    val login: String = "",
    val avatar: String = "",
    val nickname: String = "",
    val profile: String = "",
    val followers: Int = 0,
    val bankDetailsList: BankDetailsList = BankDetailsList(details = emptyList())
)
