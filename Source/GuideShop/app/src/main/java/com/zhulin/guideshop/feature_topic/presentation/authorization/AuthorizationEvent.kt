package com.zhulin.guideshop.feature_topic.presentation.authorization

sealed class AuthorizationEvent {
    data class ChangeSingInLogin(val value: String) : AuthorizationEvent()
    data class ChangeSingInPassword(val value: String) : AuthorizationEvent()

    data class ChangeSingUpLogin(val value: String) : AuthorizationEvent()
    data class ChangeSingUpNickname(val value: String) : AuthorizationEvent()
    data class ChangeSingUpPassword(val value: String) : AuthorizationEvent()

    object ChangeSingIn : AuthorizationEvent()
    object SignIn : AuthorizationEvent()
    object SignUp : AuthorizationEvent()
}