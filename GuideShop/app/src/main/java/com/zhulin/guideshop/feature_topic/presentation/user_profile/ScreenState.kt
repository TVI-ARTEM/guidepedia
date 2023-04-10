package com.zhulin.guideshop.feature_topic.presentation.user_profile

import com.zhulin.guideshop.core.presentation.util.Language


data class ScreenState(
    val language: Language = Language.EN,
    val editing: Boolean = false,
    val showAvatarDialog: Boolean = false,
    val showBankDialog: Boolean = false,
    val refreshing: Boolean = false
)
