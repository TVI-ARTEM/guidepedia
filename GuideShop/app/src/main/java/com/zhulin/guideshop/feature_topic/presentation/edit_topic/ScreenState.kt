package com.zhulin.guideshop.feature_topic.presentation.edit_topic

import com.zhulin.guideshop.core.presentation.util.Language


data class ScreenState(
    val openRender: Boolean = false,
    val showMenu: Boolean = false,
    val showLinkDialog: Boolean = false,
    val showCodeDialog: Boolean = false,
    val showImageDialog: Boolean = false,
    val showUpdateDialog: Boolean = false,
    val language: Language = Language.EN
)
