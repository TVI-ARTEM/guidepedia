package com.zhulin.guideshop.feature_topic.presentation.watch_topic

import com.zhulin.guideshop.core.presentation.util.Language


data class ScreenState(
    val language: Language = Language.EN,
    val refreshing: Boolean = false,
    val downloading: Boolean = true,
    val raw: Boolean = false,
    val logged: Boolean = false
)
