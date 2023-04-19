package com.zhulin.guideshop.feature_topic.presentation.authorization

import com.zhulin.guideshop.core.presentation.util.Language


data class ScreenState(
    val signIn: Boolean = true,
    val language: Language = Language.EN
)
