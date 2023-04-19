package com.zhulin.guideshop.feature_topic.presentation.topics

import com.zhulin.guideshop.feature_topic.data.remote.responses.CategoriesList

data class CategoryState(
    val categories: CategoriesList = CategoriesList(),
)
