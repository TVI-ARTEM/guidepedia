package com.zhulin.guideshop.feature_topic.presentation.topics

enum class ScreenFilterEnum {
    ALL, SUBSCRIPTION, SAVED, LIKED, MY_ARTICLES, MY_DRAFTS, CATEGORIES;

    companion object {
        fun getFilters(): List<ScreenFilterEnum> =
            listOf(ALL, SUBSCRIPTION, SAVED, LIKED, MY_ARTICLES, MY_DRAFTS)

        fun getUnregisteredFilters(): List<ScreenFilterEnum> =
            listOf(ALL)

    }
}