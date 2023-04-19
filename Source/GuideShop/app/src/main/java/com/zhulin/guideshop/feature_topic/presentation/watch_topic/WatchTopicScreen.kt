package com.zhulin.guideshop.feature_topic.presentation.watch_topic

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.mukesh.MarkDown
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Screen
import com.zhulin.guideshop.core.utils.DateUtils.formatDateTime
import com.zhulin.guideshop.core.utils.DateUtils.toFormat
import com.zhulin.guideshop.core.utils.ScreenUtils
import com.zhulin.guideshop.core.utils.StringResourceUtils
import com.zhulin.guideshop.feature_topic.data.remote.responses.UserInfoResponse
import com.zhulin.guideshop.ui.theme.NewBlue
import com.zhulin.guideshop.ui.theme.NewWhite
import com.zhulin.guideshop.ui.theme.TextColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun WatchTopicScreen(
    navController: NavHostController,
    viewModel: WatchTopicViewModel = hiltViewModel()
) {
    ScreenUtils.LockScreenOrientation(
        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    )
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val screenState = viewModel.screenState.value
    val userState = viewModel.userState.value
    val articleState = viewModel.articlesState.value

    val onBack =
        {
            if (screenState.raw) {
                viewModel.onEvent(WatchTopicEvent.ChangeRaw)
            } else {
                viewModel.onEvent(WatchTopicEvent.ChangeRaw)
                navController.popBackStack()
            }

        }


    BackHandler(onBack = { onBack() })

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is WatchTopicViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                WatchTopicViewModel.UiEvent.Error -> navController.navigate(Screen.TopicsScreen.route)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(weight = 6f, fill = false)
                        ) {
                            Text(
                                text = articleState.article.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(weight = 2f, fill = false)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    5.dp,
                                    alignment = Alignment.Start
                                )
                            ) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(articleState.authorInfo.avatar)
                                        .crossfade(true)
                                        .transformations(
                                            CircleCropTransformation()
                                        )
                                        .build(),
                                    contentDescription = "User avatar",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(TextColor)
                                        .border(
                                            BorderStroke(2.dp, NewBlue),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            if (screenState.downloading) return@clickable
                                            navController.navigate(Screen.AuthorProfileScreen.route + "?authorLogin=${articleState.authorInfo.login}")
                                        },
                                    loading = {
                                        CircularProgressIndicator()
                                    }
                                )
                            }
                        }


                    }

                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Menu", modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            content = {
                if (!screenState.downloading) {
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
                                    .data(articleState.article.preview)
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
                                    text = articleState.article.createdAt.formatDateTime()
                                        .toFormat(),
                                    color = NewWhite,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp)
                                )
                                Text(
                                    text = articleState.article.title,
                                    color = NewWhite,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 24.sp
                                    )
                                )
                                Text(
                                    text = articleState.article.description,
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

                        Divider(
                            color = NewBlue,
                            thickness = 2.dp,
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }

                    if (screenState.raw) {
                        item {
                            SelectionContainer {
                                Column {
                                    Text(
                                        text = articleState.article.content,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                            .fillMaxWidth(0.99f),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                                        color = TextColor
                                    )
                                }
                            }
                        }
                    } else {
                        item {
                            MarkDown(
                                text = articleState.article.content,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                                    .fillMaxWidth(0.99f),
                            )
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.End
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.onEvent(WatchTopicEvent.ChangeRaw)
                                },
                                modifier = Modifier.padding(horizontal = 10.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (screenState.raw) StringResourceUtils.getStringResource(
                                        R.string.render_name,
                                        screenState.language,
                                        LocalContext.current
                                    ) else StringResourceUtils.getStringResource(
                                        R.string.raw_name,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NewBlue,
                                )
                            }
                        }

                    }

                    if (!screenState.raw) {
                        item {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.comments_name,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 24.sp
                                    ),
                                    color = TextColor
                                )

                            }
                        }
                        if (screenState.logged) {

                            item {
                                var comment by remember {
                                    mutableStateOf("")
                                }

                                Column(
                                    Modifier
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Row(
                                        Modifier
                                            .fillMaxWidth(0.95f).padding(horizontal = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        OutlinedTextField(
                                            value = comment,
                                            onValueChange = {
                                                comment = it
                                            },
                                            placeholder = {
                                                Text(
                                                    text = StringResourceUtils.getStringResource(
                                                        R.string.comments_hint,
                                                        screenState.language,
                                                        LocalContext.current
                                                    )
                                                )
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(8f),
                                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                                textColor = TextColor,
                                                cursorColor = TextColor,
                                                focusedBorderColor = TextColor,
                                                focusedLabelColor = TextColor,
                                                selectionColors = TextSelectionColors(
                                                    backgroundColor = NewBlue,
                                                    handleColor = NewBlue
                                                ),
                                            ),
                                            textStyle = MaterialTheme.typography.bodyMedium
                                        )

                                        IconButton(
                                            onClick = {
                                                viewModel.onEvent(
                                                    WatchTopicEvent.AddComment(
                                                        comment
                                                    )
                                                )
                                                comment = ""
                                            },
                                            modifier = Modifier
                                                .size(24.dp)
                                                .weight(1f),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Send,
                                                contentDescription = "Send",
                                                tint = TextColor
                                            )
                                        }
                                    }
                                }

                            }
                        }
                        items(articleState.comments) { comment ->
                            var author by remember {
                                mutableStateOf(UserInfoResponse(0, "", "", "", "", 0))
                            }
                            LaunchedEffect(true) {
                                author = viewModel.getUserInfo(comment.userId)
                            }

                            Column(
                                modifier = Modifier
                                    .padding(vertical = 5.dp)
                                    .fillMaxWidth(0.9f)
                                    .heightIn(min = 75.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .padding(5.dp)
                                    .border(
                                        border = BorderStroke(
                                            2.dp,
                                            TextColor.copy(alpha = 0.1f)
                                        )
                                    )
                                    .background(TextColor.copy(alpha = 0.01f))
                                    .padding(10.dp)

                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        5.dp,
                                        alignment = Alignment.Start
                                    ),
                                    modifier = Modifier
                                        .padding(bottom = 5.dp)
                                        .clickable {
                                            navController.navigate(Screen.AuthorProfileScreen.route + "?authorLogin=${author.login}")
                                        }
                                ) {
                                    SubcomposeAsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(author.avatar)
                                            .crossfade(true)
                                            .transformations(
                                                CircleCropTransformation()
                                            )
                                            .build(),
                                        contentDescription = "User avatar",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(TextColor)
                                            .border(
                                                BorderStroke(2.dp, NewBlue),
                                                shape = CircleShape
                                            ),
                                        loading = {
                                            CircularProgressIndicator()
                                        }
                                    )


                                    Text(
                                        text = author.nickname,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 16.sp, fontWeight = FontWeight.Medium
                                        ),
                                        color = TextColor,
                                    )
                                }

                                Text(
                                    text = comment.content,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 18.sp
                                    ),
                                    color = TextColor,
                                    modifier = Modifier.heightIn(min = 50.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = comment.createdAt.formatDateTime().toFormat(),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.sp
                                        ),
                                        color = TextColor,
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (author.login == userState.login) {
                                        IconButton(
                                            onClick = {
                                                viewModel.onEvent(
                                                    WatchTopicEvent.RemoveComment(
                                                        comment.id
                                                    )
                                                )
                                            },
                                            modifier = Modifier.size(24.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = TextColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            },
            modifier = Modifier.padding(innerPadding).padding(bottom = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
    }
}