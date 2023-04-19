package com.zhulin.guideshop.feature_topic.presentation.author_profile

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
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleLikeCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.CheckArticleLikeResponse
import com.zhulin.guideshop.feature_topic.domain.repository.ArticleRepository
import com.zhulin.guideshop.feature_topic.domain.repository.StringResourceRepository
import com.zhulin.guideshop.feature_topic.domain.repository.UserRepository
import com.zhulin.guideshop.feature_topic.presentation.topics.TopicsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorProfileViewModel @Inject constructor(
    private val userStore: UserStore,
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val stringResourceRepository: StringResourceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _screenState = mutableStateOf(ScreenState())
    val screenState: State<ScreenState> = _screenState

    private val _userState = mutableStateOf(UserState())
    val userState: State<UserState> = _userState

    private val _authorState = mutableStateOf(AuthorState())
    val authorState: State<AuthorState> = _authorState

    private val _articlesState = mutableStateOf(ArticlesState())
    val articlesState: State<ArticlesState> = _articlesState

    private val _subscriptionState = mutableStateOf(SubscriptionState())
    val subscriptionState: State<SubscriptionState> = _subscriptionState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var _authorLogin = ""

    init {
        savedStateHandle.get<String>("authorLogin")?.let { authorLogin ->
            _authorLogin = authorLogin
            viewModelScope.launch {
                refreshLanguage()
                refreshAuthorInfo()
                refreshAuthorArticles()
                refreshUserInfo()
                refreshSubscriptions()
                refreshBankDetails()
            }
        }
    }

    fun onEvent(event: AuthorProfileEvent) {
        when (event) {
            AuthorProfileEvent.Refresh -> {
                viewModelScope.launch {
                    _screenState.value = screenState.value.copy(
                        refreshing = true
                    )
                    refreshAuthorInfo()
                    refreshAuthorArticles()
                    refreshBankDetails()
                    refreshSubscriptions()
                    _screenState.value = screenState.value.copy(
                        refreshing = false
                    )
                }
            }

            AuthorProfileEvent.ShowArticles -> _screenState.value = screenState.value.copy(
                articlesShow = true
            )

            AuthorProfileEvent.ShowProfile -> _screenState.value = screenState.value.copy(
                articlesShow = false
            )

            AuthorProfileEvent.Subscribe -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = userRepository.subscribe(token, _authorLogin)) {
                        is Resource.Error -> {
                            Log.i("API", result.message!!)
                            _eventFlow.emit(UiEvent.Error)
                        }

                        is Resource.Success -> {
                            _subscriptionState.value =
                                subscriptionState.value.copy(
                                    subscriptions = result.data!!
                                )

                            _userState.value = userState.value.copy(
                                followed = result.data.contains(authorState.value.id)
                            )
                        }

                        is Resource.TimeOut -> {
                            Log.i("API", result.message!!)
                            _eventFlow.emit(UiEvent.Error)
                        }
                    }

                    refreshAuthorInfo()

                }
            }

            AuthorProfileEvent.Unsubscribe -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = userRepository.unsubscribe(token, _authorLogin)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                        _eventFlow.emit(UiEvent.Error)
                    }

                    is Resource.Success -> {
                        _subscriptionState.value =
                            subscriptionState.value.copy(
                                subscriptions = result.data!!
                            )

                        _userState.value = userState.value.copy(
                            followed = result.data.contains(authorState.value.id)
                        )
                    }

                    is Resource.TimeOut -> {
                        Log.i("API", result.message!!)
                        _eventFlow.emit(UiEvent.Error)
                    }
                }

                refreshAuthorInfo()
            }

            is AuthorProfileEvent.Like -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = articleRepository.addLike(token, event.value)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                    }

                    is Resource.Success -> {
                    }

                    is Resource.TimeOut -> TopicsViewModel.UiEvent.ShowSnackbar(
                        stringResourceRepository.getResourceString(
                            R.string.no_connection_hint,
                            screenState.value.language
                        )
                    )
                }
            }
            is AuthorProfileEvent.Unlike -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = articleRepository.removeLike(token, event.value)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                    }

                    is Resource.Success -> {
                    }

                    is Resource.TimeOut -> TopicsViewModel.UiEvent.ShowSnackbar(
                        stringResourceRepository.getResourceString(
                            R.string.no_connection_hint,
                            screenState.value.language
                        )
                    )
                }
            }

            is AuthorProfileEvent.Save -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = articleRepository.addSave(token, event.value)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                    }

                    is Resource.Success -> {
                    }

                    is Resource.TimeOut -> TopicsViewModel.UiEvent.ShowSnackbar(
                        stringResourceRepository.getResourceString(
                            R.string.no_connection_hint,
                            screenState.value.language
                        )
                    )
                }
            }

            is AuthorProfileEvent.Unsave -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = articleRepository.removeSave(token, event.value)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                    }

                    is Resource.Success -> {
                    }

                    is Resource.TimeOut -> TopicsViewModel.UiEvent.ShowSnackbar(
                        stringResourceRepository.getResourceString(
                            R.string.no_connection_hint,
                            screenState.value.language
                        )
                    )
                }
            }
        }
    }

    suspend fun getArticleCommentsCount(id: Int): ArticleCommentCountResponse {
        return when (val result = articleRepository.getCommentsCount(id)) {
            is Resource.Error -> ArticleCommentCountResponse(0)
            is Resource.Success -> result.data!!
            is Resource.TimeOut -> ArticleCommentCountResponse(0)
        }
    }

    suspend fun getArticleLikesCount(id: Int): ArticleLikeCountResponse {
        return when (val result = articleRepository.getArticleLikes(id)) {
            is Resource.Error -> ArticleLikeCountResponse(0)
            is Resource.Success -> result.data!!
            is Resource.TimeOut -> ArticleLikeCountResponse(0)
        }
    }

    suspend fun checkArticleLike(id: Int): CheckArticleLikeResponse {
        val token = userStore.getToken.first()
        return when (val result = articleRepository.checkLikedArticle(token, id)) {
            is Resource.Error -> {
                CheckArticleLikeResponse(false)
            }
            is Resource.Success -> {
                CheckArticleLikeResponse(result.data!!.liked)
            }
            is Resource.TimeOut -> {
                CheckArticleLikeResponse(false)
            }
        }
    }

    suspend fun checkArticleSave(id: Int): CheckArticleLikeResponse {
        val token = userStore.getToken.first()
        return when (val result = articleRepository.checkSavedArticle(token, id)) {
            is Resource.Error -> {
                CheckArticleLikeResponse(false)
            }
            is Resource.Success -> {
                CheckArticleLikeResponse(result.data!!.liked)
            }
            is Resource.TimeOut -> {
                CheckArticleLikeResponse(false)
            }
        }
    }

    private suspend fun refreshSubscriptions() {
        val token = userStore.getToken.first()
        when (val result = userRepository.getSubscriptions(token)) {
            is Resource.Error -> {
                Log.i("API", result.message!!)
            }

            is Resource.Success -> {
                _subscriptionState.value =
                    subscriptionState.value.copy(
                        subscriptions = result.data!!
                    )

                _userState.value = userState.value.copy(
                    followed = result.data.contains(authorState.value.id)
                )
            }


            is Resource.TimeOut -> {
                Log.i("API", result.message!!)
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

    private suspend fun refreshUserInfo() {
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

    private suspend fun refreshAuthorInfo() {
        when (val result = userRepository.getUserInfo(_authorLogin)) {
            is Resource.Error -> {
                Log.i("API", result.message!!)
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success -> _authorState.value = authorState.value.copy(
                id = result.data!!.id,
                login = result.data.login,
                avatar = result.data.avatar,
                nickname = result.data.nickname,
                profile = result.data.profile,
                followers = result.data.subscriptions,
            )

            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.Error)
            }

        }
    }

    private suspend fun refreshAuthorArticles() {
        when (val result = articleRepository.getAuthorArticle(_authorLogin, "")) {
            is Resource.Error -> {
                Log.i("API", result.message!!)
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success -> {
                _articlesState.value = articlesState.value.copy(
                    all = result.data!!
                )
            }

            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.Error)
            }
        }
    }

    private suspend fun refreshBankDetails() {
        when (val result = userRepository.getBankDetails(authorState.value.login)) {
            is Resource.Error -> {
                Log.i("API", result.message!!)
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success -> {
                _authorState.value = authorState.value.copy(
                    bankDetailsList = result.data!!
                )
            }


            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.Error)
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object Error : UiEvent()
    }
}
