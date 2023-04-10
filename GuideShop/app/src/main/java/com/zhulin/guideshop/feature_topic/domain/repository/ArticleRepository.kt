package com.zhulin.guideshop.feature_topic.domain.repository

import com.zhulin.guideshop.core.presentation.util.Resource
import com.zhulin.guideshop.feature_topic.data.remote.responses.Article
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCategoriesIds
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleChangeResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentsList
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleLikeCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleList
import com.zhulin.guideshop.feature_topic.data.remote.responses.CategoriesList
import com.zhulin.guideshop.feature_topic.data.remote.responses.CheckArticleLikeResponse

interface ArticleRepository {
    suspend fun getAll(search: String): Resource<ArticleList>
    suspend fun getAuthorArticle(login: String, search: String): Resource<ArticleList>
    suspend fun getCategoryArticles(categoryId: Int, search: String): Resource<ArticleList>
    suspend fun getUserArticle(token: String, search: String): Resource<ArticleList>
    suspend fun getUserArticleUnpublished(token: String, search: String): Resource<ArticleList>
    suspend fun getCategories(): Resource<CategoriesList>
    suspend fun getArticleCategories(id: Int): Resource<ArticleCategoriesIds>
    suspend fun addArticleCategories(
        token: String,
        id: Int,
        categoryId: Int
    ): Resource<ArticleCategoriesIds>

    suspend fun removeArticleCategories(
        token: String,
        id: Int,
        categoryId: Int
    ): Resource<ArticleCategoriesIds>

    suspend fun getArticle(id: Int): Resource<Article>

    suspend fun createArticle(
        token: String,

        ): Resource<ArticleChangeResponse>

    suspend fun updateArticle(
        token: String,
        id: Int,
        title: String,
        content: String,
        preview: String,
        description: String,
        published: Boolean
    ): Resource<ArticleChangeResponse>


    suspend fun getComments(id: Int): Resource<ArticleCommentsList>
    suspend fun getCommentsCount(id: Int): Resource<ArticleCommentCountResponse>
    suspend fun addComment(
        token: String,
        id: Int,
        content: String
    ): Resource<ArticleCommentsList>

    suspend fun removeComment(
        token: String,
        id: Int,
        commentId: Int
    ): Resource<ArticleCommentsList>


    suspend fun getLikedArticle(
        token: String,
        search: String
    ): Resource<ArticleList>

    suspend fun getArticleLikes(id: Int): Resource<ArticleLikeCountResponse>
    suspend fun checkLikedArticle(
        token: String,
        id: Int,
    ): Resource<CheckArticleLikeResponse>

    suspend fun addLike(
        token: String,
        id: Int,
    ): Resource<ArticleLikeCountResponse>

    suspend fun removeLike(
        token: String,
        id: Int,
    ): Resource<ArticleLikeCountResponse>
}