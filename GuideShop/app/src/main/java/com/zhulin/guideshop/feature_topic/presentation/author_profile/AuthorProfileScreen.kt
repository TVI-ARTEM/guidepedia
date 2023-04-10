package com.zhulin.guideshop.feature_topic.presentation.author_profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Screen
import com.zhulin.guideshop.core.utils.DateUtils.formatDateTime
import com.zhulin.guideshop.core.utils.DateUtils.toFormat
import com.zhulin.guideshop.core.utils.StringResourceUtils
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleLikeCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.CheckArticleLikeResponse
import com.zhulin.guideshop.core.presentation.components.ArticleItem
import com.zhulin.guideshop.ui.theme.NewBlue
import com.zhulin.guideshop.ui.theme.NewYellow
import com.zhulin.guideshop.ui.theme.TextColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterial3Api
@Composable
fun AuthorProfileScreen(
    navController: NavHostController,
    viewModel: AuthorProfileViewModel = hiltViewModel()
) {

    val screenState = viewModel.screenState.value
    val authorState = viewModel.authorState.value
    val userState = viewModel.userState.value
    val articlesState = viewModel.articlesState.value
    val onBack = {
        navController.popBackStack()
    }
    BackHandler(onBack = { onBack() })

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val state = rememberPullRefreshState(screenState.refreshing, {
        viewModel.onEvent(AuthorProfileEvent.Refresh)
    })

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AuthorProfileViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                AuthorProfileViewModel.UiEvent.Error -> navController.popBackStack()

            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Menu", modifier = Modifier.fillMaxSize()
                        )
                    }
                })
        }
    ) { innerPadding ->
        Box(
            Modifier
                .pullRefresh(state)
                .padding(innerPadding)
        ) {
            LazyColumn(
                content = {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    5.dp,
                                    alignment = Alignment.Start
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(authorState.avatar)
                                        .crossfade(true)
                                        .transformations(
                                            CircleCropTransformation()
                                        )
                                        .build(),
                                    contentDescription = "User avatar",
                                    modifier = Modifier
                                        .size(64.dp)
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


                                Column {
                                    Text(
                                        text = authorState.nickname,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 24.sp, fontWeight = FontWeight.Medium
                                        ),
                                        color = TextColor,
                                    )

                                    Text(
                                        text = "${authorState.followers} " +
                                                StringResourceUtils.getStringResource(
                                                    R.string.followers_profile,
                                                    screenState.language,
                                                    LocalContext.current
                                                ),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.sp
                                        ),
                                        color = TextColor,
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    10.dp,
                                    alignment = Alignment.End
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                if (userState.login != authorState.login && userState.login.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            if (userState.followed) viewModel.onEvent(
                                                AuthorProfileEvent.Unsubscribe
                                            ) else viewModel.onEvent(AuthorProfileEvent.Subscribe)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            painter = if (userState.followed) painterResource(id = R.drawable.unfollow_icon) else painterResource(
                                                id = R.drawable.follow_icon
                                            ),
                                            contentDescription = "Follow",
                                            tint = TextColor,
                                        )
                                    }
                                }

                            }

                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 2.dp),
                        ) {
                            TextButton(
                                onClick = { viewModel.onEvent(AuthorProfileEvent.ShowArticles) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.articles_name,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        textDecoration = if (screenState.articlesShow)
                                            TextDecoration.Underline else TextDecoration.None
                                    ),
                                    color = TextColor,
                                )
                            }

                            TextButton(
                                onClick = { viewModel.onEvent(AuthorProfileEvent.ShowProfile) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.profile_name,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        textDecoration = if (screenState.articlesShow)
                                            TextDecoration.None else TextDecoration.Underline
                                    ),
                                    color = TextColor,
                                )
                            }
                        }

                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 2.dp,
                            color = TextColor
                        )
                    }


                    if (screenState.articlesShow) {
                        when (articlesState.all.articles.size) {
                            0 -> item {
                                Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.no_article_hint,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    color = TextColor,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            else -> items(articlesState.all.articles) { article ->

                                var commentCount by remember {
                                    mutableStateOf(ArticleCommentCountResponse(0))
                                }

                                var likeCount by remember {
                                    mutableStateOf(ArticleLikeCountResponse(0))
                                }

                                var changeLike by remember {
                                    mutableStateOf(false)
                                }

                                var isLiked by remember {
                                    mutableStateOf(CheckArticleLikeResponse(false))
                                }

                                val scope = rememberCoroutineScope()


                                LaunchedEffect(key1 = true) {
                                    commentCount = viewModel.getArticleCommentsCount(article.id)
                                }

                                LaunchedEffect(key1 = changeLike) {
                                    likeCount = viewModel.getArticleLikesCount(article.id)
                                    isLiked = viewModel.checkArticleLike(article.id)
                                }

                                ArticleItem(
                                    onClick = { navController.navigate(Screen.WatchTopicScreen.route + "?topicId=${article.id}") },
                                    onAuthorClick = {  },
                                    onSubscribe = {},
                                    onEdit = {
                                        navController.navigate(
                                            Screen.EditTopicScreen.route + "?topicId=${article.id}"
                                        )
                                    },
                                    onLike = {
                                        if (screenState.logged) {
                                            if (isLiked.liked) {
                                                viewModel.onEvent(AuthorProfileEvent.Unlike(article.id))
                                            } else {
                                                viewModel.onEvent(AuthorProfileEvent.Like(article.id))
                                            }
                                            scope.launch {
                                                delay(250)
                                                changeLike = !changeLike
                                            }
                                        }
                                    },
                                    onComment = { navController.navigate(Screen.WatchTopicScreen.route + "?topicId=${article.id}") },
                                    preview = article.preview,
                                    authorAvatar = "",
                                    authorNickname = "",
                                    authorFollowers = "",
                                    showSubscribe = false,
                                    subscribed = false,
                                    userOwn = userState.login == authorState.login,
                                    createdAt = article.createdAt.formatDateTime().toFormat(),
                                    title = article.title,
                                    description = article.description,
                                    isLiked = isLiked.liked,
                                    likeCount = "${likeCount.count}",
                                    commentCount = "${commentCount.count}",
                                    showAuthor = false,
                                    bookmarked = false,
                                    onBookmark = { /*TODO()*/ },
                                    logged = userState.login != authorState.login && userState.login.isNotEmpty()
                                )
                            }
                        }


                    } else {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {

                                androidx.compose.material.Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.about_profile,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                                )


                                Text(
                                    text = authorState.profile,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }


                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material.Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.bank_details,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                                )
                            }

                        }

                        items(authorState.bankDetailsList.details) {
                            Row(
                                Modifier
                                    .padding(vertical = 5.dp)
                                    .fillMaxWidth(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(BorderStroke(2.dp, NewBlue), RoundedCornerShape(10.dp))
                                    .background(NewYellow)
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(9f)
                                ) {
                                    androidx.compose.material.Text(
                                        text = StringResourceUtils.getStringResource(
                                            R.string.bank_detail,
                                            screenState.language,
                                            LocalContext.current
                                        ),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                    )

                                    androidx.compose.material.Text(
                                        text = it.number,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.clickable {
                                            clipboardManager.setText(AnnotatedString(it.number))
                                        }
                                    )
                                }
                            }
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
            )
            PullRefreshIndicator(screenState.refreshing, state, Modifier.align(Alignment.TopCenter))
        }
    }
}
