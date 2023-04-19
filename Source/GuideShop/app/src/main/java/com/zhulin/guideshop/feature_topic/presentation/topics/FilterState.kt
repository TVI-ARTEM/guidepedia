package com.zhulin.guideshop.feature_topic.presentation.topics

data class FilterState(
    val type: ScreenFilterEnum = ScreenFilterEnum.ALL,
    val categoryId: Int = 0,
    val subscriptionId: Int = 0,
    val search: String = ""
)
