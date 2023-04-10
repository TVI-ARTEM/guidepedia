package com.zhulin.guideshop.feature_topic.presentation.watch_topic

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.presentation.util.Resource
import com.zhulin.guideshop.feature_topic.data.local.UserStore
import com.zhulin.guideshop.feature_topic.data.remote.responses.UserInfoResponse
import com.zhulin.guideshop.feature_topic.domain.repository.ArticleRepository
import com.zhulin.guideshop.feature_topic.domain.repository.StringResourceRepository
import com.zhulin.guideshop.feature_topic.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchTopicViewModel @Inject constructor(
    private val userStore: UserStore,
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val stringResourceRepository: StringResourceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var _topicId = -1

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _articlesState = mutableStateOf(ArticleState())
    val articlesState: State<ArticleState> = _articlesState

    private val _userState = mutableStateOf(UserState())
    val userState: State<UserState> = _userState

    private val _screenState = mutableStateOf(ScreenState())
    val screenState: State<ScreenState> = _screenState

    init {
        savedStateHandle.get<Int>("topicId")?.let { topicId ->
            _topicId = topicId
            viewModelScope.launch {
                _screenState.value = screenState.value.copy(
                    downloading = true
                )

                refreshLanguage()
                refreshArticle()
                refreshComments()
                refreshAuthor()
                refreshUserState()

                delay(1000)
                _screenState.value = screenState.value.copy(
                    downloading = false
                )
            }
        }
    }

    fun onEvent(event: WatchTopicEvent) {
        when (event) {
            WatchTopicEvent.ChangeRaw -> {
                _screenState.value = screenState.value.copy(
                    raw = !screenState.value.raw
                )
            }

            WatchTopicEvent.Refresh -> {
                viewModelScope.launch {
                    _screenState.value = screenState.value.copy(
                        downloading = true
                    )

                    refreshArticle()

                    delay(1000)
                    _screenState.value = screenState.value.copy(
                        downloading = false
                    )
                }
            }

            is WatchTopicEvent.AddComment -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = articleRepository.addComment(
                        token = token,
                        id = _topicId,
                        content = event.value
                    )) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                        }

                        is Resource.Success ->
                            _articlesState.value = articlesState.value.copy(
                                comments = result.data!!
                            )

                        is Resource.TimeOut -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                        }
                    }
                }
            }

            is WatchTopicEvent.RemoveComment -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = articleRepository.removeComment(
                    token = token,
                    id = _topicId,
                    commentId = event.value
                )) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                    }

                    is Resource.Success ->
                        _articlesState.value = articlesState.value.copy(
                            comments = result.data!!
                        )

                    is Resource.TimeOut -> {
                        _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                    }
                }
            }
        }
    }

    suspend fun getUserInfo(id: Int): UserInfoResponse {
        return when (val result = userRepository.getUserInfo(id)) {
            is Resource.Error -> UserInfoResponse(0, "", "", "", "", 0)
            is Resource.Success -> result.data!!
            is Resource.TimeOut -> UserInfoResponse(0, "", "", "", "", 0)
        }
    }

    private suspend fun refreshUserState() {
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

                    _userState.value = userState.value.copy(
                        login = userStore.getLogin.first(),
                        nickname = userStore.getNickname.first(),
                        avatar = userStore.getAvatar.first()
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
                _eventFlow.emit(UiEvent.Error)
            }
        }
    }

    private suspend fun refreshAuthor() {
        when (val result =
            userRepository.getUserInfo(_articlesState.value.article.userId)) {
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success -> {
                _articlesState.value = articlesState.value.copy(
                    authorInfo = result.data!!
                )
            }


            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }
        }
    }

    private suspend fun refreshArticle() {
        when (val result = articleRepository.getArticle(_topicId)) {
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success ->
                _articlesState.value = articlesState.value.copy(
                    article = result.data!!
                )

            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }
        }
    }

    private suspend fun refreshComments() {
        when (val result = articleRepository.getComments(_topicId)) {
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success ->
                _articlesState.value = articlesState.value.copy(
                    comments = result.data!!
                )

            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }
        }
    }

    private suspend fun refreshLanguage() {
        val language = userStore.getLanguage.first()
        _screenState.value = screenState.value.copy(
            language = Language.from(language)
        )
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object Error : UiEvent()
    }
}