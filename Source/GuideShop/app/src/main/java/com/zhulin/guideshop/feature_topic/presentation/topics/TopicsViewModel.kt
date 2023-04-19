package com.zhulin.guideshop.feature_topic.presentation.topics

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.presentation.util.Resource
import com.zhulin.guideshop.feature_topic.data.local.UserStore
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleLikeCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleList
import com.zhulin.guideshop.feature_topic.data.remote.responses.CheckArticleLikeResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.UserInfoResponse
import com.zhulin.guideshop.feature_topic.domain.repository.ArticleRepository
import com.zhulin.guideshop.feature_topic.domain.repository.StringResourceRepository
import com.zhulin.guideshop.feature_topic.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val userStore: UserStore,
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val stringResourceRepository: StringResourceRepository
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _screenState = mutableStateOf(ScreenState())
    val screenState: State<ScreenState> = _screenState

    private val _userState = mutableStateOf(UserState())
    val userState: State<UserState> = _userState

    private val _articlesState = mutableStateOf(ArticlesState())
    val articlesState: State<ArticlesState> = _articlesState

    private val _categoryState = mutableStateOf(CategoryState())
    val categoryState: State<CategoryState> = _categoryState

    private val _subscriptionState = mutableStateOf(SubscriptionState())
    val subscriptionState: State<SubscriptionState> = _subscriptionState

    private val _filterState = mutableStateOf(FilterState())
    val filterState: State<FilterState> = _filterState

    init {
        viewModelScope.launch {
            _screenState.value = screenState.value.copy(
                refreshing = true
            )
            refreshLanguage()
            refreshUserState()
            refreshCategories()
            refreshSubscription()
            refreshArticles()
            _screenState.value = screenState.value.copy(
                refreshing = false
            )
        }
    }

    fun onEvent(event: TopicEvent) {
        when (event) {
            TopicEvent.ArticleRefresh -> {
                viewModelScope.launch {
                    _screenState.value = screenState.value.copy(
                        refreshing = true
                    )
                    refreshSubscription()
                    refreshCategories()
                    refreshArticles()
                    _screenState.value = screenState.value.copy(
                        refreshing = false
                    )
                }
            }

            TopicEvent.OnlyArticleRefresh -> {
                viewModelScope.launch {
                    _screenState.value = screenState.value.copy(
                        refreshing = true
                    )
                    refreshArticles()
                    _screenState.value = screenState.value.copy(
                        refreshing = false
                    )
                }
            }

            is TopicEvent.ChangeSearch -> {
                _filterState.value = _filterState.value.copy(
                    search = event.value
                )
            }

            is TopicEvent.ChangeAuthorLogin -> _filterState.value = _filterState.value.copy(
                subscriptionId = event.value
            )

            is TopicEvent.ChangeCategoryId -> _filterState.value = _filterState.value.copy(
                categoryId = event.value
            )


            is TopicEvent.ChangeScreenFilter -> {
                _filterState.value = _filterState.value.copy(
                    type = event.value
                )
            }

            TopicEvent.CreateArticle -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = articleRepository.createArticle(
                        token = token
                    )) {
                        is Resource.Error -> {
                            Log.i("API", result.message!!)
                        }

                        is Resource.Success -> _eventFlow.emit(UiEvent.CreateArticle(result.data!!.projectId))
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

            is TopicEvent.Subscribe -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = userRepository.subscribe(token, event.value)) {
                        is Resource.Error -> {
                            Log.i("API", result.message!!)
                        }

                        is Resource.Success -> {
                            _subscriptionState.value =
                                subscriptionState.value.copy(
                                    subscriptions = result.data!!
                                )

                            if (result.data.isEmpty()) {
                                _filterState.value = filterState.value.copy(
                                    subscriptionId = -1
                                )
                            }
                        }

                        is Resource.TimeOut -> UiEvent.ShowSnackbar(
                            stringResourceRepository.getResourceString(
                                R.string.no_connection_hint,
                                screenState.value.language
                            )
                        )
                    }
                }
            }

            is TopicEvent.Unsubscribe -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = userRepository.unsubscribe(token, event.value)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                    }

                    is Resource.Success -> {
                        _subscriptionState.value =
                            subscriptionState.value.copy(
                                subscriptions = result.data!!
                            )


                        if (result.data.isNotEmpty()) {
                            _filterState.value = filterState.value.copy(
                                subscriptionId = result.data.first()
                            )
                        } else {
                            _filterState.value = filterState.value.copy(
                                subscriptionId = -1
                            )
                        }
                    }

                    is Resource.TimeOut -> UiEvent.ShowSnackbar(
                        stringResourceRepository.getResourceString(
                            R.string.no_connection_hint,
                            screenState.value.language
                        )
                    )
                }
            }

            is TopicEvent.Like -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = articleRepository.addLike(token, event.value)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                    }

                    is Resource.Success -> {
                    }

                    is Resource.TimeOut -> UiEvent.ShowSnackbar(
                        stringResourceRepository.getResourceString(
                            R.string.no_connection_hint,
                            screenState.value.language
                        )
                    )
                }
            }

            is TopicEvent.Unlike -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = articleRepository.removeLike(token, event.value)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                    }

                    is Resource.Success -> {
                    }

                    is Resource.TimeOut -> UiEvent.ShowSnackbar(
                        stringResourceRepository.getResourceString(
                            R.string.no_connection_hint,
                            screenState.value.language
                        )
                    )
                }
            }

            is TopicEvent.Save -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = articleRepository.addSave(token, event.value)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                    }

                    is Resource.Success -> {
                    }

                    is Resource.TimeOut -> UiEvent.ShowSnackbar(
                        stringResourceRepository.getResourceString(
                            R.string.no_connection_hint,
                            screenState.value.language
                        )
                    )
                }
            }
            is TopicEvent.Unsave -> viewModelScope.launch {
                val token = userStore.getToken.first()
                when (val result = articleRepository.removeSave(token, event.value)) {
                    is Resource.Error -> {
                        Log.i("API", result.message!!)
                    }

                    is Resource.Success -> {
                    }

                    is Resource.TimeOut -> UiEvent.ShowSnackbar(
                        stringResourceRepository.getResourceString(
                            R.string.no_connection_hint,
                            screenState.value.language
                        )
                    )
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

    private suspend fun refreshLanguage() {
        val language = userStore.getLanguage.first()
        _screenState.value = screenState.value.copy(
            language = Language.from(language)
        )
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
            }
        }
    }

    private suspend fun refreshCategories() {
        when (val result = articleRepository.getCategories()) {
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
            }

            is Resource.Success -> {
                _categoryState.value = categoryState.value.copy(
                    categories = result.data!!
                )
            }

            is Resource.TimeOut -> {
                UiEvent.ShowSnackbar(
                    stringResourceRepository.getResourceString(
                        R.string.no_connection_hint,
                        screenState.value.language
                    )
                )
            }
        }
    }

    private suspend fun refreshSubscription() {
        val token = userStore.getToken.first()

        when (val result = userRepository.getSubscriptions(token)) {
            is Resource.Error -> {
                Log.i("API", result.message!!)
            }

            is Resource.Success -> {
                _subscriptionState.value = subscriptionState.value.copy(
                    subscriptions = result.data!!
                )

                if (result.data.isNotEmpty()) {
                    _filterState.value = filterState.value.copy(
                        subscriptionId = result.data.first()
                    )
                } else {
                    _filterState.value = filterState.value.copy(
                        subscriptionId = -1
                    )
                }
            }

            is Resource.TimeOut -> {
                UiEvent.ShowSnackbar(
                    stringResourceRepository.getResourceString(
                        R.string.no_connection_hint,
                        screenState.value.language
                    )
                )
            }
        }
    }

    private suspend fun refreshArticles() {
        _articlesState.value = articlesState.value.copy(
            all = ArticleList(emptyList())
        )

        val result = when (filterState.value.type) {
            ScreenFilterEnum.ALL -> {
                articleRepository.getAll(filterState.value.search)
            }

            ScreenFilterEnum.SUBSCRIPTION -> {
                val login = when (val result =
                    userRepository.getUserInfo(filterState.value.subscriptionId)) {
                    is Resource.Error -> ""
                    is Resource.Success -> result.data?.login ?: ""
                    is Resource.TimeOut -> ""
                }
                articleRepository.getAuthorArticle(
                    login,
                    filterState.value.search
                )
            }

            ScreenFilterEnum.LIKED -> {
                val token = userStore.getToken.first()
                articleRepository.getLikedArticle(
                    token = token,
                    search = filterState.value.search
                )
            }

            ScreenFilterEnum.SAVED -> {
                val token = userStore.getToken.first()
                articleRepository.getSavedArticle(
                    token = token,
                    search = filterState.value.search
                )
            }
            ScreenFilterEnum.MY_ARTICLES -> {
                val token = userStore.getToken.first()

                articleRepository.getUserArticle(token = token, search = filterState.value.search)
            }

            ScreenFilterEnum.MY_DRAFTS -> {
                val token = userStore.getToken.first()

                articleRepository.getUserArticleUnpublished(
                    token = token,
                    search = filterState.value.search
                )
            }

            ScreenFilterEnum.CATEGORIES -> {
                articleRepository.getCategoryArticles(
                    categoryId = filterState.value.categoryId,
                    search = filterState.value.search
                )
            }
        }

        when (result) {
            is Resource.Error -> {
                Log.i("API", result.message!!)
                _articlesState.value = articlesState.value.copy(
                    all = ArticleList(emptyList())
                )
            }

            is Resource.Success -> {
                _articlesState.value = articlesState.value.copy(
                    all = result.data!!
                )
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

                _articlesState.value = articlesState.value.copy(
                    all = ArticleList(emptyList())
                )
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class CreateArticle(val id: Int) : UiEvent()
    }
}