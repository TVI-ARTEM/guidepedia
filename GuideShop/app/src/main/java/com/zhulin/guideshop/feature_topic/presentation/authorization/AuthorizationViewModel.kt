package com.zhulin.guideshop.feature_topic.presentation.authorization

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
class AuthorizationViewModel @Inject constructor(
    private val userStore: UserStore,
    private val userRepository: UserRepository,
    stringResourceRepository: StringResourceRepository
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _screenState = mutableStateOf(ScreenState())
    val screenState: State<ScreenState> = _screenState


    private val _signInState = mutableStateOf(
        SignInState(
            hintLogin = stringResourceRepository.getResourceString(
                R.string.sign_in_login_hint,
                Language.EN
            ),
            hintPassword = stringResourceRepository.getResourceString(
                R.string.sign_in_password_hint,
                Language.EN
            )
        )
    )

    val signInState: State<SignInState> = _signInState


    private val _signUpState = mutableStateOf(
        SignUpState(
            hintLogin = stringResourceRepository.getResourceString(
                R.string.sign_up_login_hint,
                Language.EN
            ),
            hintNickname = stringResourceRepository.getResourceString(
                R.string.sign_up_nickname_hint,
                Language.EN
            ),
            hintPassword = stringResourceRepository.getResourceString(
                R.string.sign_up_password_hint,
                Language.EN
            )
        )
    )

    val signUpState: State<SignUpState> = _signUpState

    init {
        viewModelScope.launch {
            val language = userStore.getLanguage.first()
            _signInState.value = signInState.value.copy(
                hintLogin = stringResourceRepository.getResourceString(
                    R.string.sign_in_login_hint,
                    Language.from(language)
                ),
                hintPassword = stringResourceRepository.getResourceString(
                    R.string.sign_in_password_hint,
                    Language.from(language)
                )
            )

            _signUpState.value = signUpState.value.copy(
                hintLogin = stringResourceRepository.getResourceString(
                    R.string.sign_up_login_hint,
                    Language.from(language)
                ),
                hintNickname = stringResourceRepository.getResourceString(
                    R.string.sign_up_nickname_hint,
                    Language.from(language)
                ),
                hintPassword = stringResourceRepository.getResourceString(
                    R.string.sign_up_password_hint,
                    Language.from(language)
                )
            )

            _screenState.value = screenState.value.copy(
                language = Language.from(language)
            )
        }
    }

    fun onEvent(event: AuthorizationEvent) {
        when (event) {
            AuthorizationEvent.ChangeSingIn -> {
                _screenState.value = screenState.value.copy(
                    signIn = !screenState.value.signIn
                )
            }

            is AuthorizationEvent.ChangeSingInLogin -> {
                _signInState.value = signInState.value.copy(
                    login = event.value
                )
            }

            is AuthorizationEvent.ChangeSingInPassword -> {
                _signInState.value = signInState.value.copy(
                    password = event.value
                )
            }

            is AuthorizationEvent.ChangeSingUpLogin -> {
                _signUpState.value = signUpState.value.copy(
                    login = event.value
                )
            }

            is AuthorizationEvent.ChangeSingUpNickname -> {
                _signUpState.value = signUpState.value.copy(
                    nickname = event.value
                )
            }

            is AuthorizationEvent.ChangeSingUpPassword -> {
                _signUpState.value = signUpState.value.copy(
                    password = event.value
                )
            }

            AuthorizationEvent.SignIn -> {
                viewModelScope.launch {
                    when (val result =
                        userRepository.login(signInState.value.login, signInState.value.password)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(message = result.message!!))
                        }

                        is Resource.Success -> {
                            userStore.updateToken(result.data!!.token)
                            _eventFlow.emit(UiEvent.SignIn)
                        }

                        is Resource.TimeOut -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(message = result.message!!))
                            _eventFlow.emit(UiEvent.BackMenu)
                        }
                    }
                }
            }

            AuthorizationEvent.SignUp -> {
                viewModelScope.launch {

                    when (val result =
                        userRepository.registration(
                            signUpState.value.login,
                            signUpState.value.nickname,
                            signUpState.value.password
                        )) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(message = result.message!!))
                        }

                        is Resource.Success -> {
                            userStore.updateToken(result.data!!.token)
                            _eventFlow.emit(UiEvent.SignIn)
                        }

                        is Resource.TimeOut -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(message = result.message!!))
                            _eventFlow.emit(UiEvent.BackMenu)
                        }
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SignIn : UiEvent()
        object BackMenu : UiEvent()
    }
}