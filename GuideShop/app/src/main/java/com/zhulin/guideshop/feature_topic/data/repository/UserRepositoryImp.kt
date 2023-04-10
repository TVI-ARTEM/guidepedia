package com.zhulin.guideshop.feature_topic.data.repository

import com.google.gson.Gson
import com.zhulin.guideshop.core.presentation.util.Resource
import com.zhulin.guideshop.feature_topic.data.remote.GuideAPI
import com.zhulin.guideshop.feature_topic.data.remote.request.BankAddRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.BankRemoveRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.LoginRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.RegistrationRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.UserInfoRequest
import com.zhulin.guideshop.feature_topic.data.remote.responses.AuthResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.BankDetailsList
import com.zhulin.guideshop.feature_topic.data.remote.responses.ErrorResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.LoginResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.RegistrationResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.SubscriptionList
import com.zhulin.guideshop.feature_topic.data.remote.responses.UserInfoResponse
import com.zhulin.guideshop.feature_topic.domain.repository.UserRepository
import retrofit2.HttpException
import java.net.SocketTimeoutException

class UserRepositoryImp(private val userAPI: GuideAPI) : UserRepository {
    override suspend fun registration(
        login: String,
        nickname: String,
        password: String
    ): Resource<RegistrationResponse> {
        val response = try {
            userAPI.registration(RegistrationRequest(login, nickname, password))
        } catch (e: Exception) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                (e as HttpException).response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        }

        return Resource.Success(response)
    }

    override suspend fun login(login: String, password: String): Resource<LoginResponse> {
        val response = try {
            userAPI.login(LoginRequest(login, password))
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun auth(token: String): Resource<AuthResponse> {
        val response = try {
            userAPI.auth("Bearer $token")
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }


    override suspend fun getUserInfo(id: Int): Resource<UserInfoResponse> {
        val response = try {
            userAPI.getUserInfo(id)
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {

            println(e)
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun getUserInfo(login: String): Resource<UserInfoResponse> {
        val response = try {
            userAPI.getUserInfoLogin(login)
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {

            println(e)
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun updateUserInfo(
        token: String,
        nickname: String,
        profile: String,
        avatar: String
    ): Resource<UserInfoResponse> {
        val response = try {
            userAPI.updateUserInfo(
                "Bearer $token",
                UserInfoRequest(nickname = nickname, profile = profile, avatar = avatar)
            )
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {

            println(e)
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun getBankDetails(login: String): Resource<BankDetailsList> {
        val response = try {
            userAPI.getBankDetails(login)
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {

            println(e)
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun addBank(token: String, number: String): Resource<BankDetailsList> {
        val response = try {
            userAPI.addBank("Bearer $token", BankAddRequest(number))
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {

            println(e)
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun removeBank(token: String, id: Int): Resource<BankDetailsList> {
        val response = try {
            userAPI.removeBank("Bearer $token", BankRemoveRequest(id))
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {

            println(e)
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun getSubscriptions(token: String): Resource<SubscriptionList> {
        val response = try {
            userAPI.getSubscriptions("Bearer $token")
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {

            println(e)
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun subscribe(token: String, login: String): Resource<SubscriptionList> {
        val response = try {
            userAPI.subscribe("Bearer $token", login)
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {

            println(e)
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun unsubscribe(token: String, login: String): Resource<SubscriptionList> {
        val response = try {
            userAPI.unsubscribe("Bearer $token", login)
        } catch (e: HttpException) {
            val gson = Gson()

            val errorResponse = gson.fromJson(
                e.response()?.errorBody()?.string()
                    ?: "{\"message\": \"Error occurred.\"}", ErrorResponse::class.java
            )

            return Resource.Error(errorResponse.message)
        } catch (e: SocketTimeoutException) {
            return Resource.TimeOut("No connection.")
        } catch (e: Exception) {

            println(e)
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }
}