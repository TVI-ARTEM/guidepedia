package com.zhulin.guideshop.feature_topic.presentation.edit_topic

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.markdown.basic.FormatterEnum
import com.zhulin.guideshop.core.markdown.basic.Formatters
import com.zhulin.guideshop.core.markdown.complex.ComplexFormatterEnum
import com.zhulin.guideshop.core.markdown.complex.ComplexFormatters
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.presentation.util.Resource
import com.zhulin.guideshop.feature_topic.data.local.UserStore
import com.zhulin.guideshop.feature_topic.domain.repository.ArticleRepository
import com.zhulin.guideshop.feature_topic.domain.repository.StringResourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTopicViewModel @Inject constructor(
    stringResourceRepository: StringResourceRepository,
    private val formatters: Formatters,
    private val complexFormatters: ComplexFormatters,
    private val userStore: UserStore,
    private val articleRepository: ArticleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var _topicId: Int = 0

    private val _screenState = mutableStateOf(ScreenState())

    val screenState: State<ScreenState> = _screenState

    private val _articleState = mutableStateOf(ArticlesState())

    val articleState: State<ArticlesState> = _articleState

    private val _categoryState = mutableStateOf(CategoryState())

    val categoryState: State<CategoryState> = _categoryState

    private val _topicTitle = mutableStateOf(
        NoteTextFieldState(
            hint = stringResourceRepository.getResourceString(
                R.string.topic_title_hint,
                Language.EN
            )
        )
    )

    val topicTitle: State<NoteTextFieldState> = _topicTitle

    private val _topicContent = mutableStateOf(
        NoteTextFieldState(
            hint = stringResourceRepository.getResourceString(
                R.string.topic_content_hint,
                Language.EN
            )
        )
    )

    val topicContent: State<NoteTextFieldState> = _topicContent

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("topicId")?.let { topicId ->
            _topicId = topicId
            viewModelScope.launch {
                refreshArticle()

                val language = userStore.getLanguage.first()
                _topicTitle.value = topicTitle.value.copy(
                    hint = stringResourceRepository.getResourceString(
                        R.string.topic_title_hint,
                        Language.from(language)
                    )
                )

                _topicContent.value = topicContent.value.copy(
                    hint = stringResourceRepository.getResourceString(
                        R.string.topic_content_hint,
                        Language.from(language)
                    )
                )

                _screenState.value = screenState.value.copy(
                    language = Language.from(language)
                )


                refreshCategories()
            }
        }
    }

    private suspend fun refreshCategories() {
        when (val result = articleRepository.getCategories()) {
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success -> {
                _categoryState.value = categoryState.value.copy(
                    categories = result.data!!
                )
            }

            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }
        }

        when (val result = articleRepository.getArticleCategories(_topicId)) {
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }

            is Resource.Success -> {
                _categoryState.value = categoryState.value.copy(
                    articleCategoriesIds = result.data!!
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

            is Resource.Success -> {
                _topicTitle.value = topicTitle.value.copy(
                    text = result.data!!.title,
                    isHintVisible = result.data.title.isEmpty()
                )

                _topicContent.value = topicContent.value.copy(
                    text = result.data.content,
                    isHintVisible = result.data.content.isEmpty()
                )
                _articleState.value = articleState.value.copy(
                    article = result.data
                )

                _eventFlow.emit(
                    UiEvent.Updated(
                        title = _topicTitle.value.text,
                        content = _topicContent.value.text,
                        preview = articleState.value.article.preview,
                        description = articleState.value.article.description
                    )
                )

            }

            is Resource.TimeOut -> {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                _eventFlow.emit(UiEvent.Error)
            }
        }


    }

    fun onEvent(event: EditTopicEvent) {
        when (event) {
            is EditTopicEvent.EnteredTitle -> {
                _topicTitle.value = topicTitle.value.copy(
                    text = event.text,
                    selection = event.selection
                )

            }

            is EditTopicEvent.ChangeTitleFocus -> {
                _topicTitle.value = topicTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            topicTitle.value.text.isEmpty()
                )
            }

            is EditTopicEvent.EnteredContent -> {
                _topicContent.value = topicContent.value.copy(
                    text = event.text,
                    selection = event.selection

                )
            }

            is EditTopicEvent.ChangeContentFocus -> {
                _topicContent.value = topicContent.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            topicContent.value.text.isEmpty()
                )
            }


            is EditTopicEvent.ApplyFormatter -> {
                val formatter = when (event.formatter) {
                    FormatterEnum.HEADER -> formatters.headerFormatter
                    FormatterEnum.BOLD -> formatters.boldFormatter
                    FormatterEnum.ITALIC -> formatters.italicFormatter
                    FormatterEnum.STRIKE_THROUGH -> formatters.strikethroughFormatter
                    FormatterEnum.ROUND_LIST -> formatters.roundListFormatter
                    FormatterEnum.NUMERIC_LIST -> formatters.numericListFormatter
                    FormatterEnum.NEW_LINE -> formatters.newLineFormatter
                    FormatterEnum.SELECTED -> formatters.selectedFormatter
                    FormatterEnum.HORIZONTAL_LINE -> formatters.horizontalLineFormatter
                }

                val (content, selection) = formatter.format(
                    topicContent.value.text,
                    topicContent.value.selection
                )

                _topicContent.value = topicContent.value.copy(
                    text = content,
                    selection = selection,
                    isHintVisible = content.isEmpty()
                )

            }

            is EditTopicEvent.ApplyComplexFormatter -> {
                val formatter = when (event.formatter) {
                    ComplexFormatterEnum.LINK -> complexFormatters.linkComplexFormatter
                    ComplexFormatterEnum.IMAGE -> complexFormatters.imageComplexFormatter
                    ComplexFormatterEnum.CODE -> complexFormatters.codeComplexFormatter
                }


                val (content, selection) = formatter.format(
                    topicContent.value.text,
                    topicContent.value.selection,
                    event.params
                )

                _topicContent.value = topicContent.value.copy(
                    text = content,
                    selection = selection,
                    isHintVisible = content.isEmpty()
                )

            }

            is EditTopicEvent.ChangeOpenRender -> {
                _screenState.value = screenState.value.copy(
                    openRender = event.value
                )
            }

            is EditTopicEvent.ChangeShowCodeDialog -> {
                _screenState.value = screenState.value.copy(
                    showCodeDialog = event.value
                )
            }

            is EditTopicEvent.ChangeShowImageDialog -> {
                _screenState.value = screenState.value.copy(
                    showImageDialog = event.value
                )
            }

            is EditTopicEvent.ChangeShowLinkDialog -> {
                _screenState.value = screenState.value.copy(
                    showLinkDialog = event.value
                )
            }

            is EditTopicEvent.ChangeShowMenu -> {
                _screenState.value = screenState.value.copy(
                    showMenu = event.value
                )
            }

            is EditTopicEvent.ChangeShowUpdateDialog -> {
                _screenState.value = screenState.value.copy(
                    showUpdateDialog = event.value
                )
            }

            is EditTopicEvent.Update -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = articleRepository.updateArticle(
                        token = token,
                        id = articleState.value.article.id,
                        title = topicTitle.value.text,
                        content = topicContent.value.text,
                        preview = event.preview,
                        description = event.description,
                        published = event.published
                    )) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar("Saved"))
                            refreshArticle()
                        }

                        is Resource.TimeOut -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }
                    }
                }
            }

            is EditTopicEvent.AddCategory -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = articleRepository.addArticleCategories(
                        token = token,
                        id = articleState.value.article.id,
                        categoryId = event.value
                    )) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar("Added"))
                            _categoryState.value = categoryState.value.copy(
                                articleCategoriesIds = result.data!!
                            )
                        }

                        is Resource.TimeOut -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }
                    }
                }
            }

            is EditTopicEvent.RemoveCategory -> {
                viewModelScope.launch {
                    val token = userStore.getToken.first()
                    when (val result = articleRepository.removeArticleCategories(
                        token = token,
                        id = articleState.value.article.id,
                        categoryId = event.value
                    )) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar("Removed"))
                            _categoryState.value = categoryState.value.copy(
                                articleCategoriesIds = result.data!!
                            )
                        }

                        is Resource.TimeOut -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(result.message!!))
                            _eventFlow.emit(UiEvent.Error)
                        }
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object Error : UiEvent()
        data class Updated(
            val title: String,
            val content: String,
            val preview: String,
            val description: String
        ) : UiEvent()
    }
}