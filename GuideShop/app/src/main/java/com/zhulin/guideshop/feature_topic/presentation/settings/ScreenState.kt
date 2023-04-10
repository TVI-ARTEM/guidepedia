package com.zhulin.guideshop.feature_topic.presentation.settings

import com.zhulin.guideshop.core.presentation.util.Language


data class ScreenState(
    val language: Language = Language.EN,
    val logged: Boolean = false,

    )
