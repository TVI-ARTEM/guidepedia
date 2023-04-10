package com.zhulin.guideshop.feature_topic.presentation.author_profile

import com.zhulin.guideshop.core.presentation.util.Language


data class ScreenState(
    val logged: Boolean = false,
    val language: Language = Language.EN,
    val refreshing: Boolean = false,
    val articlesShow: Boolean = true
)
