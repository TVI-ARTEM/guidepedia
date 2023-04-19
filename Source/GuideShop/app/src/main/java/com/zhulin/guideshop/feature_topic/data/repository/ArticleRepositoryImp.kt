package com.zhulin.guideshop.feature_topic.data.repository

import com.google.gson.Gson
import com.zhulin.guideshop.core.presentation.util.Resource
import com.zhulin.guideshop.feature_topic.data.remote.GuideAPI
import com.zhulin.guideshop.feature_topic.data.remote.request.ArticleAddCommentRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.ArticleRemoveCommentRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.LikeChangeRequest
import com.zhulin.guideshop.feature_topic.data.remote.request.UpdateArticleRequest
import com.zhulin.guideshop.feature_topic.data.remote.responses.Article
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCategoriesIds
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleChangeResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentsList
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleLikeCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleList
import com.zhulin.guideshop.feature_topic.data.remote.responses.CategoriesList
import com.zhulin.guideshop.feature_topic.data.remote.responses.CheckArticleLikeResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ErrorResponse
import com.zhulin.guideshop.feature_topic.domain.repository.ArticleRepository
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ArticleRepositoryImp(private val articleAPI: GuideAPI) : ArticleRepository {
    override suspend fun getAll(search: String): Resource<ArticleList> {
        val response = try {
            articleAPI.getAll(search = search)
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

    override suspend fun getAuthorArticle(login: String, search: String): Resource<ArticleList> {
        val response = try {
            articleAPI.getAuthorArticle(login = login, search = search)
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

    override suspend fun getCategoryArticles(
        categoryId: Int,
        search: String
    ): Resource<ArticleList> {
        val response = try {
            articleAPI.getCategoryArticles(categoryId = categoryId, search = search)
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

    override suspend fun getUserArticle(token: String, search: String): Resource<ArticleList> {
        val response = try {
            articleAPI.getUserArticle(authHeader = "Bearer $token", search = search)
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

    override suspend fun getUserArticleUnpublished(
        token: String,
        search: String
    ): Resource<ArticleList> {
        val response = try {
            articleAPI.getUserArticleUnpublished(authHeader = "Bearer $token", search = search)
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

    override suspend fun getCategories(): Resource<CategoriesList> {
        val response = try {
            articleAPI.getCategories()
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

    override suspend fun getArticleCategories(id: Int): Resource<ArticleCategoriesIds> {
        val response = try {
            articleAPI.getArticleCategories(id)
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

    override suspend fun addArticleCategories(
        token: String,
        id: Int,
        categoryId: Int
    ): Resource<ArticleCategoriesIds> {
        val response = try {
            articleAPI.addArticleCategories(
                authHeader = "Bearer $token",
                id = id,
                categoryId = categoryId
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun removeArticleCategories(
        token: String,
        id: Int,
        categoryId: Int
    ): Resource<ArticleCategoriesIds> {
        val response = try {
            articleAPI.removeArticleCategories(
                authHeader = "Bearer $token",
                id = id,
                categoryId = categoryId
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun getArticle(id: Int): Resource<Article> {
        val response = try {
            articleAPI.getArticle(id)
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

    override suspend fun createArticle(
        token: String,

        ): Resource<ArticleChangeResponse> {
        val response = try {
            articleAPI.createArticle(
                "Bearer $token"
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun updateArticle(
        token: String,
        id: Int,
        title: String,
        content: String,
        preview: String,
        description: String,
        published: Boolean
    ): Resource<ArticleChangeResponse> {
        val response = try {
            articleAPI.updateArticle(
                "Bearer $token", UpdateArticleRequest(
                    id = id,
                    content = content,
                    description = description,
                    preview = preview,
                    published = published,
                    title = title
                )
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun getComments(id: Int): Resource<ArticleCommentsList> {
        val response = try {
            articleAPI.getComments(id = id)
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

    override suspend fun getCommentsCount(id: Int): Resource<ArticleCommentCountResponse> {
        val response = try {
            articleAPI.getCommentsCount(id = id)
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

    override suspend fun addComment(
        token: String,
        id: Int,
        content: String
    ): Resource<ArticleCommentsList> {
        val response = try {
            articleAPI.addComment(
                authHeader = "Bearer $token",
                request = ArticleAddCommentRequest(id = id, content = content)
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun removeComment(
        token: String,
        id: Int,
        commentId: Int
    ): Resource<ArticleCommentsList> {
        val response = try {
            articleAPI.removeComment(
                authHeader = "Bearer $token",
                request = ArticleRemoveCommentRequest(id = id, commentId = commentId)
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun getLikedArticle(token: String, search: String): Resource<ArticleList> {
        val response = try {
            articleAPI.getLikedArticle(
                authHeader = "Bearer $token",
                search = search
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun getArticleLikes(id: Int): Resource<ArticleLikeCountResponse> {
        val response = try {
            articleAPI.getArticleLikes(
                id = id
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun checkLikedArticle(
        token: String,
        id: Int
    ): Resource<CheckArticleLikeResponse> {
        val response = try {
            articleAPI.checkLikedArticle(
                authHeader = "Bearer $token",
                id = id
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun addLike(token: String, id: Int): Resource<ArticleLikeCountResponse> {
        val response = try {
            articleAPI.addLike(
                authHeader = "Bearer $token",
                request = LikeChangeRequest(id)
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun removeLike(token: String, id: Int): Resource<ArticleLikeCountResponse> {
        val response = try {
            articleAPI.removeLike(
                authHeader = "Bearer $token",
                request = LikeChangeRequest(id)
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }


    override suspend fun getSavedArticle(token: String, search: String): Resource<ArticleList> {
        val response = try {
            articleAPI.getSavedArticle(
                authHeader = "Bearer $token",
                search = search
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun checkSavedArticle(
        token: String,
        id: Int
    ): Resource<CheckArticleLikeResponse> {
        val response = try {
            articleAPI.checkSavedArticle(
                authHeader = "Bearer $token",
                id = id
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun addSave(token: String, id: Int): Resource<ArticleLikeCountResponse> {
        val response = try {
            articleAPI.addSave(
                authHeader = "Bearer $token",
                request = LikeChangeRequest(id)
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

    override suspend fun removeSave(token: String, id: Int): Resource<ArticleLikeCountResponse> {
        val response = try {
            articleAPI.removeSave(
                authHeader = "Bearer $token",
                request = LikeChangeRequest(id)
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
            return Resource.Error("Error occurred.")
        }

        return Resource.Success(response)
    }

}