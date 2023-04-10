package com.zhulin.guideshop.feature_topic.presentation.user_profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.presentation.util.Resource
import com.zhulin.guideshop.feature_topic.data.local.UserStore
import com.zhulin.guideshop.feature_topic.domain.repository.StringResourceRepository
import com.zhulin.guideshop.feature_topic.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userStore: UserStore,
    private val userRepository: UserRepository,
    private val stringResourceRepository: StringResourceRepository
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _screenState = mutableStateOf(ScreenState())
    val screenState: State<ScreenState> = _screenState

    private val _userState = mutableStateOf(UserState())
    private val userState: State<UserState> = _userState

    private val _userScreenState = mutableStateOf(UserScreenState())
    val userScreenState: State<UserScreenState> = _userScreenState


    init {
        viewModelScope.launch {
            val language = userStore.getLanguage.first()
            _screenState.value = screenState.value.copy(
                language = Language.from(language)
            )

            refresh()

        }
    }


    fun onEvent(event: UserProfileEvent) {
        when (event) {
            UserProfileEvent.Logout -> {
                viewModelScope.launch {
                    userStore.resetAll()
                    _eventFlow.emit(UiEvent.Logout)
                }
            }

            UserProfileEvent.ChangeEdit -> {
                _screenState.value = screenState.value.copy(
                    editing = !screenState.value.editing
                )

                _userScreenState.value = userScreenState.value.copy(
                    avatar = userState.value.avatar,
                    profile = userState.value.profile,
                    nickname = userState.value.nickname,
                    followers = userState.value.followers
                )

                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.UpdateFields(
                            nickname = userState.value.nickname,
                            profile = userState.value.profile
                        )
                    )
                }
            }

            is UserProfileEvent.SaveInfo -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = userRepository.updateUserInfo(
                        token = token,
                        nickname = userScreenState.value.nickname,
                        profile = userScreenState.value.profile,
                        avatar = userScreenState.value.avatar
                    )) {
                        is Resource.Error -> {
                            _screenState.value = screenState.value.copy(
                                editing = false
                            )

                            _userScreenState.value = userScreenState.value.copy(
                                avatar = userState.value.avatar,
                                profile = userState.value.profile,
                                nickname = userState.value.nickname,
                                followers = userState.value.followers
                            )

                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                        }

                        is Resource.Success -> {
                            _userState.value = userState.value.copy(
                                avatar = result.data!!.avatar,
                                profile = result.data.profile,
                                nickname = result.data.nickname,
                                followers = result.data.subscriptions
                            )

                            _userScreenState.value = userScreenState.value.copy(
                                avatar = result.data.avatar,
                                profile = result.data.profile,
                                nickname = result.data.nickname,
                                followers = result.data.subscriptions
                            )

                            _screenState.value = screenState.value.copy(
                                editing = false
                            )
                        }

                        is Resource.TimeOut -> {
                            _screenState.value = screenState.value.copy(
                                editing = false
                            )

                            _userScreenState.value = userScreenState.value.copy(
                                avatar = userState.value.avatar,
                                profile = userState.value.profile,
                                nickname = userState.value.nickname,
                                followers = userState.value.followers
                            )

                            _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    stringResourceRepository.getResourceString(
                                        R.string.no_connection_hint,
                                        screenState.value.language
                                    )
                                )
                            )


                        }
                    }
                }
            }

            is UserProfileEvent.ChangeAvatar -> {
                _userScreenState.value = userScreenState.value.copy(
                    avatar = event.value,
                )
            }

            is UserProfileEvent.ChangeNickname -> {
                _userScreenState.value = userScreenState.value.copy(
                    nickname = event.value,
                )
            }

            is UserProfileEvent.ChangeProfile -> {
                _userScreenState.value = userScreenState.value.copy(
                    profile = event.value,
                )
            }

            UserProfileEvent.ChangeShowAvatarDialog -> {
                _screenState.value = screenState.value.copy(
                    showAvatarDialog = !screenState.value.showAvatarDialog
                )
            }

            UserProfileEvent.Refresh -> {
                viewModelScope.launch {
                    _screenState.value = screenState.value.copy(
                        refreshing = true
                    )
                    refresh()
                    _screenState.value = screenState.value.copy(
                        refreshing = false
                    )
                }
            }

            is UserProfileEvent.RemoveBank -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = userRepository.removeBank(token, event.value)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }

                        is Resource.Success -> {
                            _userScreenState.value = userScreenState.value.copy(
                                bankDetailsList = result.data!!
                            )
                        }


                        is Resource.TimeOut -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }
                    }
                }
            }

            is UserProfileEvent.AddBank -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = userRepository.addBank(token, event.value)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }

                        is Resource.Success -> {
                            _userScreenState.value = userScreenState.value.copy(
                                bankDetailsList = result.data!!
                            )
                        }


                        is Resource.TimeOut -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }
                    }
                }
            }

            UserProfileEvent.ChangeShowBankDialog -> {
                _screenState.value = screenState.value.copy(
                    showBankDialog = !screenState.value.showBankDialog
                )
            }
        }
    }
    private suspend fun refresh() {
        val userId = userStore.getId.first()
        when (val result = userRepository.getUserInfo(userId)) {
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success -> {
                _userState.value = userState.value.copy(
                    avatar = result.data!!.avatar,
                    profile = result.data.profile,
                    nickname = result.data.nickname,
                    followers = result.data.subscriptions
                )

                _userScreenState.value = userScreenState.value.copy(
                    avatar = result.data.avatar,
                    profile = result.data.profile,
                    nickname = result.data.nickname,
                    followers = result.data.subscriptions
                )
            }


            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }
        }
        val userLogin = userStore.getLogin.first()

        when (val result = userRepository.getBankDetails(userLogin)) {
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success -> {
                _userScreenState.value = userScreenState.value.copy(
                    bankDetailsList = result.data!!
                )
            }


            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class UpdateFields(val nickname: String, val profile: String) : UiEvent()
        object Error : UiEvent()
        object Logout : UiEvent()
    }
}