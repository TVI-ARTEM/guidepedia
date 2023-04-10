package com.zhulin.guideshop.feature_topic.data.remote

import com.zhulin.guideshop.feature_topic.data.remote.request.ArticleAddCommentRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.ArticleRemoveCommentRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.BankAddRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.BankRemoveRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.LikeChangeRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.LoginRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.RegistrationRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.UpdateArticleRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.UserInfoRequest
import com.zhulin.guideshop.feature_topic.data.remote.responses.Article
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCategoriesIds
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleChangeResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentsList
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleLikeCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleList
import com.zhulin.guideshop.feature_topic.data.remote.responses.AuthResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.BankDetailsList
import com.zhulin.guideshop.feature_topic.data.remote.responses.CategoriesList
import com.zhulin.guideshop.feature_topic.data.remote.responses.CheckArticleLikeResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.LoginResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.RegistrationResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.SubscriptionList
import com.zhulin.guideshop.feature_topic.data.remote.responses.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GuideAPI {
    // Users
    @POST("user/registration")
    suspend fun registration(
        @Body request: RegistrationRequest,
    ): RegistrationResponse

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): LoginResponse

    @GET("user/auth")
    suspend fun auth(@Header("Authorization") authHeader: String): AuthResponse

    @GET("user/info")
    suspend fun getUserInfo(@Query("id") id: Int): UserInfoResponse

    @GET("user/info/login")
    suspend fun getUserInfoLogin(@Query("login") login: String): UserInfoResponse

    @POST("user/update/info")
    suspend fun updateUserInfo(
        @Header("Authorization") authHeader: String,
        @Body request: UserInfoRequest
    ): UserInfoResponse

    @GET("user/bank")
    suspend fun getBankDetails(@Query("login") login: String): BankDetailsList

    @POST("user/bank/add")
    suspend fun addBank(
        @Header("Authorization") authHeader: String,
        @Body request: BankAddRequest
    ): BankDetailsList

    @POST("user/bank/remove")
    suspend fun removeBank(
        @Header("Authorization") authHeader: String,
        @Body request: BankRemoveRequest
    ): BankDetailsList

    @GET("user/subscriptions")
    suspend fun getSubscriptions(
        @Header("Authorization") authHeader: String,
    ): SubscriptionList

    @POST("user/subscribe")
    suspend fun subscribe(
        @Header("Authorization") authHeader: String,
        @Query("login") login: String
    ): SubscriptionList

    @POST("user/unsubscribe")
    suspend fun unsubscribe(
        @Header("Authorization") authHeader: String,
        @Query("login") login: String
    ): SubscriptionList

    // Articles

    @GET("article/all")
    suspend fun getAll(@Query("search") search: String): ArticleList

    @GET("article/category")
    suspend fun getCategoryArticles(
        @Query("categoryId") categoryId: Int,
        @Query("search") search: String
    ): ArticleList

    @GET("article/author")
    suspend fun getAuthorArticle(
        @Query("login") login: String,
        @Query("search") search: String
    ): ArticleList

    @GET("article/user")
    suspend fun getUserArticle(
        @Header("Authorization") authHeader: String,
        @Query("search") search: String
    ): ArticleList

    @GET("article/user/unpublished")
    suspend fun getUserArticleUnpublished(
        @Header("Authorization") authHeader: String,
        @Query("search") search: String
    ): ArticleList

    @GET("article/get")
    suspend fun getArticle(@Query("id") id: Int): Article

    @GET("article/categories/all")
    suspend fun getCategories(): CategoriesList

    @GET("article/categories/get")
    suspend fun getArticleCategories(@Query("id") id: Int): ArticleCategoriesIds

    @POST("article/categories/add")
    suspend fun addArticleCategories(
        @Header("Authorization") authHeader: String,
        @Query("id") id: Int,
        @Query("categoryId") categoryId: Int
    ): ArticleCategoriesIds

    @POST("article/categories/remove")
    suspend fun removeArticleCategories(
        @Header("Authorization") authHeader: String,
        @Query("id") id: Int,
        @Query("categoryId") categoryId: Int
    ): ArticleCategoriesIds

    @POST("article/create")
    suspend fun createArticle(
        @Header("Authorization") authHeader: String,
    ): ArticleChangeResponse

    @POST("article/update")
    suspend fun updateArticle(
        @Header("Authorization") authHeader: String,
        @Body request: UpdateArticleRequest
    ): ArticleChangeResponse


    @GET("article/comments/get")
    suspend fun getComments(
        @Query("id") id: Int
    ): ArticleCommentsList

    @GET("article/comments/count")
    suspend fun getCommentsCount(
        @Query("id") id: Int
    ): ArticleCommentCountResponse

    @POST("article/comments/add")
    suspend fun addComment(
        @Header("Authorization") authHeader: String,
        @Body request: ArticleAddCommentRequest
    ): ArticleCommentsList

    @POST("article/comments/remove")
    suspend fun removeComment(
        @Header("Authorization") authHeader: String,
        @Body request: ArticleRemoveCommentRequest
    ): ArticleCommentsList

    @GET("article/liked")
    suspend fun getLikedArticle(
        @Header("Authorization") authHeader: String,
        @Query("search") search: String
    ): ArticleList

    @GET("article/like/check")
    suspend fun checkLikedArticle(
        @Header("Authorization") authHeader: String,
        @Query("id") id: Int
    ): CheckArticleLikeResponse

    @GET("article/like/count")
    suspend fun getArticleLikes(@Query("id") id: Int): ArticleLikeCountResponse

    @POST("article/like/add")
    suspend fun addLike(
        @Header("Authorization") authHeader: String,
        @Body request: LikeChangeRequest
    ): ArticleLikeCountResponse

    @POST("article/like/remove")
    suspend fun removeLike(
        @Header("Authorization") authHeader: String,
        @Body request: LikeChangeRequest
    ): ArticleLikeCountResponse
}