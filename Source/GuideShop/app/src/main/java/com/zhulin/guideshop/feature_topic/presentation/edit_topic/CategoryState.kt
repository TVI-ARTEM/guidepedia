package com.zhulin.guideshop.feature_topic.presentation.edit_topic

import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCategoriesIds
import com.zhulin.guideshop.feature_topic.data.remote.responses.CategoriesList

data class CategoryState(
    val categories: CategoriesList = CategoriesList(),
    val articleCategoriesIds: ArticleCategoriesIds = ArticleCategoriesIds()
)
