package com.zhulin.guideshop.feature_topic.presentation.settings

import android.util.Log
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
class SettingsViewModel @Inject constructor(
    private val userStore: UserStore,
    private val userRepository: UserRepository,
    stringResourceRepository: StringResourceRepository
) : ViewModel() {
    private val _screenState = mutableStateOf(ScreenState())
    val screenState: State<ScreenState> = _screenState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            val language = userStore.getLanguage.first()

            _screenState.value = screenState.value.copy(
                language = Language.from(language)
            )

            val token = userStore.getToken.first()
            when (val result = userRepository.auth(token)) {
                is Resource.Error -> {
                    Log.i("API", result.message!!)
                }

                is Resource.Success -> {
                    userStore.updateToken(result.data!!.token).also {
                        _screenState.value = screenState.value.copy(
                            logged = userStore.getLogged.first()
                        )
                    }
                }

                is Resource.TimeOut -> {
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

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.Login -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.Login)
                }
            }

            SettingsEvent.Logout -> {
                viewModelScope.launch {
                    userStore.resetAll()
                    _screenState.value = screenState.value.copy(
                        logged = userStore.getLogged.first()
                    )
                }
            }

            SettingsEvent.ChangeLanguage -> {
                viewModelScope.launch {
                    userStore.updateLanguage(
                        if (screenState.value.language == Language.EN)
                            Language.RU.language else Language.EN.language
                    )

                    val language = userStore.getLanguage.first()
                    _screenState.value = screenState.value.copy(
                        language = Language.from(language)
                    )
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object Login : UiEvent()

    }

}