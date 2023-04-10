package com.zhulin.guideshop.feature_topic.presentation.edit_topic

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mukesh.MarkDown
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.markdown.basic.FormatterEnum
import com.zhulin.guideshop.core.markdown.complex.ComplexFormatterEnum
import com.zhulin.guideshop.core.presentation.components.AddOneParamsDialog
import com.zhulin.guideshop.core.presentation.components.AddTwoParamsDialog
import com.zhulin.guideshop.core.presentation.components.CustomIconButton
import com.zhulin.guideshop.core.presentation.components.Keyboard
import com.zhulin.guideshop.core.presentation.components.TransparentHintTextField
import com.zhulin.guideshop.core.presentation.components.keyboardAsState
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.presentation.util.Screen
import com.zhulin.guideshop.core.utils.DateUtils.toFormat
import com.zhulin.guideshop.core.utils.StringResourceUtils
import com.zhulin.guideshop.ui.theme.NewBlue
import com.zhulin.guideshop.ui.theme.NewGreen
import com.zhulin.guideshop.ui.theme.NewRed
import com.zhulin.guideshop.ui.theme.NewWhite
import com.zhulin.guideshop.ui.theme.NewYellow
import com.zhulin.guideshop.ui.theme.TextColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@ExperimentalMaterial3Api
@Composable
fun EditTopicScreen(
    navController: NavHostController,
    viewModel: EditTopicViewModel = hiltViewModel()
) {
    val titleState = viewModel.topicTitle.value
    val contentState = viewModel.topicContent.value
    val screenState = viewModel.screenState.value
    val articlesState = viewModel.articleState.value
    val categoryState = viewModel.categoryState.value

    var titleInput by remember { mutableStateOf(TextFieldValue(titleState.text)) }
    var contentInput by remember { mutableStateOf(TextFieldValue(contentState.text)) }

    var inputPreview by remember { mutableStateOf(TextFieldValue(articlesState.article.preview)) }
    var inputDescription by remember { mutableStateOf(TextFieldValue(articlesState.article.description)) }
    var hintVisiblePreview by remember { mutableStateOf(false) }
    var hintVisibleDescription by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isKeyboardOpen by keyboardAsState()
    val onBack = {
        if (screenState.showUpdateDialog) {
            viewModel.onEvent(EditTopicEvent.ChangeShowUpdateDialog(false))
        } else {
            viewModel.onEvent(EditTopicEvent.ChangeOpenRender(false))
            navController.popBackStack()
        }
    }

    BackHandler(onBack = { onBack() })
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is EditTopicViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                EditTopicViewModel.UiEvent.Error -> navController.navigate(Screen.TopicsScreen.route)
                is EditTopicViewModel.UiEvent.Updated -> {
                    titleInput = TextFieldValue(event.title)
                    contentInput = TextFieldValue(event.content)
                    inputPreview = TextFieldValue(event.preview)
                    inputDescription = TextFieldValue(event.description)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(weight = 1f, fill = false)
                    ) {
                        TransparentHintTextField(
                            text = titleInput,
                            hint = titleState.hint,
                            onValueChange = {
                                if (!it.text.contains("\n")) {
                                    titleInput = it

                                    viewModel.onEvent(
                                        EditTopicEvent.EnteredTitle(
                                            it.text,
                                            it.selection
                                        )
                                    )
                                }
                            },
                            onFocusChange = {
                                viewModel.onEvent(EditTopicEvent.ChangeTitleFocus(it))
                            },
                            isHintVisible = titleState.isHintVisible,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            singleLine = true
                        )
                    }


                }

            }, navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Menu", modifier = Modifier.fillMaxSize()
                    )
                }
            })

        },
        floatingActionButton = {
            if (isKeyboardOpen == Keyboard.Closed && !screenState.showUpdateDialog) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (screenState.showMenu) {
                        FloatingActionButton(
                            onClick = { viewModel.onEvent(EditTopicEvent.ChangeShowUpdateDialog(true)) },
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp),
                            containerColor = NewGreen,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.publish_icon),
                                contentDescription = "Publish"
                            )
                        }

                        FloatingActionButton(
                            onClick = {
                                viewModel.onEvent(EditTopicEvent.ChangeOpenRender(!screenState.openRender))
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp),
                            containerColor = NewYellow,
                        ) {
                            Icon(
                                painter = if (screenState.openRender) painterResource(id = R.drawable.preview_close_icon)
                                else painterResource(id = R.drawable.preview_open_icon),
                                contentDescription = "Show render/raw"
                            )
                        }


                    }
                    FloatingActionButton(
                        onClick = {
                            viewModel.onEvent(EditTopicEvent.ChangeShowMenu(!screenState.showMenu))
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp),
                        containerColor = if (screenState.showMenu) NewRed else NewBlue
                    ) {
                        Icon(
                            imageVector = if (screenState.showMenu) Icons.Default.Close
                            else Icons.Default.MoreVert,
                            contentDescription = "Open/Close menu"
                        )
                    }
                }

            }
        },
        bottomBar = {
            val configuration = LocalConfiguration.current

            if ((isKeyboardOpen == Keyboard.Closed || configuration.orientation == Configuration.ORIENTATION_PORTRAIT) && !screenState.openRender && !screenState.showUpdateDialog) {
                BottomAppBar(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .requiredHeightIn(min = 48.dp)
                        .fillMaxHeight(0.075f),
                    containerColor = Color.Yellow.copy(alpha = 0.75f)
                ) {
                    LazyRow(
                        content = {

                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.ApplyFormatter(
                                                FormatterEnum.HEADER
                                            )
                                        )

                                        contentInput = contentInput.copy(
                                            text = viewModel.topicContent.value.text,
                                            selection = viewModel.topicContent.value.selection
                                        )

                                    },
                                    painter = painterResource(id = R.drawable.header_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_header_style_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }

                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.ApplyFormatter(
                                                FormatterEnum.BOLD
                                            )
                                        )

                                        contentInput = contentInput.copy(
                                            text = viewModel.topicContent.value.text,
                                            selection = viewModel.topicContent.value.selection
                                        )

                                    },
                                    painter = painterResource(id = R.drawable.bold_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_bold_style_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }

                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.ApplyFormatter(
                                                FormatterEnum.ITALIC
                                            )
                                        )

                                        contentInput = contentInput.copy(
                                            text = viewModel.topicContent.value.text,
                                            selection = viewModel.topicContent.value.selection
                                        )

                                    },
                                    painter = painterResource(id = R.drawable.italic_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_italic_style_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }

                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.ApplyFormatter(
                                                FormatterEnum.STRIKE_THROUGH
                                            )
                                        )

                                        contentInput = contentInput.copy(
                                            text = viewModel.topicContent.value.text,
                                            selection = viewModel.topicContent.value.selection
                                        )

                                    },
                                    painter = painterResource(id = R.drawable.strikethrough_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_strikethrough_style_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }




                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.ApplyFormatter(
                                                FormatterEnum.ROUND_LIST
                                            )
                                        )

                                        contentInput = contentInput.copy(
                                            text = viewModel.topicContent.value.text,
                                            selection = viewModel.topicContent.value.selection
                                        )

                                    },
                                    painter = painterResource(id = R.drawable.list_rounded_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_list_rounded_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }
                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.ApplyFormatter(
                                                FormatterEnum.NUMERIC_LIST
                                            )
                                        )

                                        contentInput = contentInput.copy(
                                            text = viewModel.topicContent.value.text,
                                            selection = viewModel.topicContent.value.selection
                                        )

                                    },
                                    painter = painterResource(id = R.drawable.list_numbered_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_list_numeric_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }


                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.ApplyFormatter(
                                                FormatterEnum.NEW_LINE
                                            )
                                        )

                                        contentInput = contentInput.copy(
                                            text = viewModel.topicContent.value.text,
                                            selection = viewModel.topicContent.value.selection
                                        )

                                    },
                                    painter = painterResource(id = R.drawable.new_line_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_new_line_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }

                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.ApplyFormatter(
                                                FormatterEnum.HORIZONTAL_LINE
                                            )
                                        )

                                        contentInput = contentInput.copy(
                                            text = viewModel.topicContent.value.text,
                                            selection = viewModel.topicContent.value.selection
                                        )

                                    },
                                    painter = painterResource(id = R.drawable.horizontal_line_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_horizontal_line_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }

                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.ApplyFormatter(
                                                FormatterEnum.SELECTED
                                            )
                                        )

                                        contentInput = contentInput.copy(
                                            text = viewModel.topicContent.value.text,
                                            selection = viewModel.topicContent.value.selection
                                        )

                                    },
                                    painter = painterResource(id = R.drawable.selected_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_selected_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }

                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(EditTopicEvent.ChangeShowLinkDialog(true))
                                        viewModel.onEvent(EditTopicEvent.ChangeShowCodeDialog(false))
                                        viewModel.onEvent(EditTopicEvent.ChangeShowImageDialog(false))
                                    },
                                    painter = painterResource(id = R.drawable.link_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_link_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }


                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(EditTopicEvent.ChangeShowLinkDialog(false))
                                        viewModel.onEvent(EditTopicEvent.ChangeShowCodeDialog(true))
                                        viewModel.onEvent(EditTopicEvent.ChangeShowImageDialog(false))
                                    },
                                    painter = painterResource(id = R.drawable.code_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_code_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }

                            item {
                                CustomIconButton(
                                    onClick = {
                                        viewModel.onEvent(EditTopicEvent.ChangeShowLinkDialog(false))
                                        viewModel.onEvent(EditTopicEvent.ChangeShowCodeDialog(false))
                                        viewModel.onEvent(EditTopicEvent.ChangeShowImageDialog(true))
                                    },
                                    painter = painterResource(id = R.drawable.image_icon),
                                    contentDescription = StringResourceUtils.getStringResource(
                                        R.string.add_image_content_description,
                                        Language.EN,
                                        LocalContext.current
                                    )
                                )
                            }

                        },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

        }
    ) { innerPadding ->

        LazyColumn(
            content = {
                if (screenState.showUpdateDialog) {
                    item {
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }
                    item {
                        LazyRow(
                            content = {
                                items(categoryState.categories) { it ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.widthIn(max = 200.dp, min = 100.dp)
                                    ) {
                                        Text(text = it.name)
                                        IconButton(
                                            onClick = {
                                                if (categoryState.articleCategoriesIds.contains(it.id)) {
                                                    viewModel.onEvent(
                                                        EditTopicEvent.RemoveCategory(
                                                            it.id
                                                        )
                                                    )
                                                } else {
                                                    viewModel.onEvent(
                                                        EditTopicEvent.AddCategory(
                                                            it.id
                                                        )
                                                    )
                                                }
                                            },
                                        ) {
                                            Icon(
                                                imageVector = if (categoryState.articleCategoriesIds.contains(
                                                        it.id
                                                    )
                                                ) Icons.Default.Delete else Icons.Default.AddCircle,
                                                tint = if (categoryState.articleCategoriesIds.contains(
                                                        it.id
                                                    )
                                                ) NewRed else NewBlue,
                                                contentDescription = "add/remove category"
                                            )
                                        }

                                    }
                                }
                            },
                            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }
                    item {
                        Text(
                            text = "Preview",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .fillMaxWidth(0.95f)
                                .heightIn(min = 150.dp, max = 300.dp)
                                .background(TextColor)

                        ) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(inputPreview.text)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Article Preview",
                                modifier = Modifier
                                    .fillMaxWidth(),
                                loading = {
                                    CircularProgressIndicator()
                                },
                                contentScale = ContentScale.Crop,
                                alpha = 0.75f
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top)
                            ) {


                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = LocalDateTime.now().toFormat(),
                                    color = NewWhite,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp)
                                )
                                Text(
                                    text = titleInput.text,
                                    color = NewWhite,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 24.sp
                                    )
                                )
                                Text(
                                    text = inputDescription.text,
                                    color = NewWhite,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 5,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            text = "Preview link",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }
                    item {
                        TransparentHintTextField(
                            text = inputPreview,
                            hint = "Enter preview link",
                            onValueChange = {
                                if (!it.text.contains("\n")) {
                                    inputPreview = it
                                }
                            },
                            onFocusChange = {
                                hintVisiblePreview =
                                    !it.isFocused && inputPreview.text.isEmpty()
                            },
                            isHintVisible = hintVisiblePreview,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            singleLine = true,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Blue.copy(alpha = 0.1f))
                                .padding(10.dp)
                                .fillMaxWidth(0.95f)
                        )
                    }
                    item {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }
                    item {
                        TransparentHintTextField(
                            text = inputDescription,
                            hint = "Enter description",
                            onValueChange = {
                                inputDescription = it
                            },
                            onFocusChange = {
                                hintVisibleDescription =
                                    !it.isFocused && inputDescription.text.isEmpty()
                            },
                            isHintVisible = hintVisibleDescription,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Blue.copy(alpha = 0.1f))
                                .padding(10.dp)
                                .fillMaxWidth(0.95f)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (articlesState.article.published) {
                                Button(
                                    onClick = {
                                        viewModel.onEvent(
                                            EditTopicEvent.Update(
                                                preview = inputPreview.text,
                                                description = inputDescription.text,
                                                published = false
                                            )
                                        )
                                        viewModel.onEvent(
                                            EditTopicEvent.ChangeShowUpdateDialog(
                                                false
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NewBlue)
                                ) {
                                    Text(
                                        text = "Draft"
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.onEvent(
                                        EditTopicEvent.Update(
                                            preview = inputPreview.text,
                                            description = inputDescription.text,
                                            published = true
                                        )
                                    )
                                    viewModel.onEvent(EditTopicEvent.ChangeShowUpdateDialog(false))
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NewBlue),
                            ) {
                                Text(
                                    text = if (articlesState.article.published) "Save" else "Publish"
                                )
                            }
                        }
                    }
                } else if (screenState.openRender) {
                    item {
                        MarkDown(
                            text = contentState.text,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .padding(10.dp)
                                .fillMaxWidth(0.99f),
                        )
                    }
                } else {
                    item {
                        TransparentHintTextField(
                            text = contentInput,
                            hint = contentState.hint,
                            onValueChange = {
                                contentInput = it
                                viewModel.onEvent(
                                    EditTopicEvent.EnteredContent(
                                        it.text,
                                        it.selection
                                    )
                                )
                            },
                            onFocusChange = {
                                viewModel.onEvent(EditTopicEvent.ChangeContentFocus(it))
                            },
                            isHintVisible = contentState.isHintVisible,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Blue.copy(alpha = 0.01f))
                                .padding(10.dp)
                                .fillMaxWidth(0.95f),
                        )
                    }


                }


            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
        )


        if (screenState.showLinkDialog) {
            AddTwoParamsDialog(
                title = "Add link",
                hintFirst = "Enter link title",
                hintSecond = "Enter link",
                onDismissRequest = {
                    viewModel.onEvent(EditTopicEvent.ChangeShowLinkDialog(false))
                },
                onConfirm = { title, link ->
                    viewModel.onEvent(
                        EditTopicEvent.ApplyComplexFormatter(
                            ComplexFormatterEnum.LINK,
                            listOf(title, link)
                        )
                    )

                    contentInput = contentInput.copy(
                        text = viewModel.topicContent.value.text,
                        selection = viewModel.topicContent.value.selection
                    )

                    viewModel.onEvent(EditTopicEvent.ChangeShowLinkDialog(false))

                }
            )
        }

        if (screenState.showImageDialog) {
            AddTwoParamsDialog(
                title = "Add image",
                hintFirst = "Enter image description",
                hintSecond = "Enter image link",
                onDismissRequest = {
                    viewModel.onEvent(EditTopicEvent.ChangeShowImageDialog(false))
                },
                onConfirm = { description, link ->
                    viewModel.onEvent(
                        EditTopicEvent.ApplyComplexFormatter(
                            ComplexFormatterEnum.IMAGE,
                            listOf(description, link)
                        )
                    )

                    contentInput = contentInput.copy(
                        text = viewModel.topicContent.value.text,
                        selection = viewModel.topicContent.value.selection
                    )

                    viewModel.onEvent(EditTopicEvent.ChangeShowImageDialog(false))

                }
            )
        }

        if (screenState.showCodeDialog) {
            AddOneParamsDialog(
                title = "Add Code",
                hintFirst = "Enter name of code",
                onDismissRequest = {
                    viewModel.onEvent(EditTopicEvent.ChangeShowCodeDialog(false))
                },
                onConfirm = { language ->
                    viewModel.onEvent(
                        EditTopicEvent.ApplyComplexFormatter(
                            ComplexFormatterEnum.CODE,
                            listOf(language)
                        )
                    )

                    contentInput = contentInput.copy(
                        text = viewModel.topicContent.value.text,
                        selection = viewModel.topicContent.value.selection
                    )

                    viewModel.onEvent(EditTopicEvent.ChangeShowCodeDialog(false))
                }
            )
        }

    }
}





