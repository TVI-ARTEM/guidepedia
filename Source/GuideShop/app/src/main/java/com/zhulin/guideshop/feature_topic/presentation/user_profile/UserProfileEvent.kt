package com.zhulin.guideshop.feature_topic.presentation.user_profile

sealed class UserProfileEvent {
    object Logout : UserProfileEvent()
    object ChangeEdit : UserProfileEvent()
    object ChangeShowAvatarDialog : UserProfileEvent()
    object ChangeShowBankDialog : UserProfileEvent()
    object Refresh : UserProfileEvent()
    data class SaveInfo(val avatar: String, val nickname: String, val profile: String) :
        UserProfileEvent()

    data class ChangeAvatar(val value: String) : UserProfileEvent()
    data class ChangeNickname(val value: String) : UserProfileEvent()
    data class ChangeProfile(val value: String) : UserProfileEvent()
    data class RemoveBank(val value: Int) : UserProfileEvent()
    data class AddBank(val value: String) : UserProfileEvent()

}