package com.zhulin.guideshop.feature_topic.domain.repository

import com.zhulin.guideshop.core.presentation.util.Resource
import com.zhulin.guideshop.feature_topic.data.remote.responses.AuthResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.BankDetailsList
import com.zhulin.guideshop.feature_topic.data.remote.responses.LoginResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.RegistrationResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.SubscriptionList
import com.zhulin.guideshop.feature_topic.data.remote.responses.UserInfoResponse

interface UserRepository {
    suspend fun registration(
        login: String,
        nickname: String,
        password: String
    ): Resource<RegistrationResponse>

    suspend fun login(
        login: String,
        password: String
    ): Resource<LoginResponse>

    suspend fun auth(token: String): Resource<AuthResponse>

    suspend fun getUserInfo(id: Int): Resource<UserInfoResponse>
    suspend fun getUserInfo(login: String): Resource<UserInfoResponse>

    suspend fun updateUserInfo(
        token: String,
        nickname: String,
        profile: String,
        avatar: String
    ): Resource<UserInfoResponse>

    suspend fun getBankDetails(login: String): Resource<BankDetailsList>

    suspend fun addBank(
        token: String,
        number: String
    ): Resource<BankDetailsList>

    suspend fun removeBank(
        token: String,
        id: Int
    ): Resource<BankDetailsList>

    suspend fun getSubscriptions(
        token: String,
    ): Resource<SubscriptionList>

    suspend fun subscribe(
        token: String,
        login: String
    ): Resource<SubscriptionList>

    suspend fun unsubscribe(
        token: String,
        login: String
    ): Resource<SubscriptionList>


}