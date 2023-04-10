package com.zhulin.guideshop.feature_topic.presentation.settings

sealed class SettingsEvent {
    object Login : SettingsEvent()
    object Logout : SettingsEvent()
    object ChangeLanguage : SettingsEvent()
}