package com.zhulin.guideshop.feature_topic.presentation.topics

import android.content.pm.ActivityInfo
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.components.ArticleItem
import com.zhulin.guideshop.core.presentation.util.Screen
import com.zhulin.guideshop.core.utils.DateUtils.formatDateTime
import com.zhulin.guideshop.core.utils.DateUtils.toFormat
import com.zhulin.guideshop.core.utils.ScreenUtils
import com.zhulin.guideshop.core.utils.StringResourceUtils
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleCommentCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.ArticleLikeCountResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.CheckArticleLikeResponse
import com.zhulin.guideshop.feature_topic.data.remote.responses.UserInfoResponse
import com.zhulin.guideshop.ui.theme.NewBlue
import com.zhulin.guideshop.ui.theme.NewWhite
import com.zhulin.guideshop.ui.theme.TextColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterial3Api
@Composable
fun TopicsScreen(
    navController: NavHostController,
    viewModel: TopicsViewModel = hiltViewModel()
) {
    ScreenUtils.LockScreenOrientation(
        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    )
    val screenState = viewModel.screenState.value
    val userState = viewModel.userState.value
    val categoryState = viewModel.categoryState.value
    val subscriptionState = viewModel.subscriptionState.value
    val filterState = viewModel.filterState.value

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val refreshState = rememberPullRefreshState(screenState.refreshing, {
        viewModel.onEvent(TopicEvent.ArticleRefresh)
    })

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is TopicsViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is TopicsViewModel.UiEvent.CreateArticle -> {
                    navController.navigate(Screen.EditTopicScreen.route + "?topicId=${event.id}")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (screenState.logged) {
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(TopicEvent.CreateArticle)
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp),
                    containerColor = NewBlue,
                    contentColor = NewWhite
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create New",
                        modifier = Modifier.fillMaxSize(0.75f),
                    )
                }
            }
        },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(
                        5.dp,
                        Alignment.CenterVertically
                    )
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.SettingsScreen.route)
                        }, modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.fillMaxSize(1f)
                        )
                    }

                    if (screenState.logged) {
                        Text(
                            text = String.format(
                                StringResourceUtils.getStringResource(
                                    R.string.hello_main_menu,
                                    screenState.language,
                                    LocalContext.current
                                ), userState.nickname
                            ),
                            color = TextColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (screenState.logged) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userState.avatar)
                            .crossfade(true)
                            .transformations(
                                CircleCropTransformation()
                            )
                            .build(),
                        contentDescription = "User avatar",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(
                                BorderStroke(2.dp, NewBlue),
                                shape = CircleShape
                            )
                            .background(TextColor)
                            .clickable { navController.navigate(Screen.UserProfileScreen.route) },
                        loading = {
                            CircularProgressIndicator()
                        }
                    )
                }
            }

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = filterState.search,
                    onValueChange = {
                        if (!it.contains("\n")) {
                            viewModel.onEvent(TopicEvent.ChangeSearch(it))
                        }
                    },
                    placeholder = {
                        Text(
                            text = StringResourceUtils.getStringResource(
                                R.string.search_input_hint,
                                screenState.language,
                                LocalContext.current
                            )
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.fillMaxWidth(0.7f),
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
                    textStyle = MaterialTheme.typography.titleMedium

                )

                IconButton(
                    onClick = { viewModel.onEvent(TopicEvent.ArticleRefresh) },

                    ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = NewBlue
                    )
                }
            }

            LazyRow(
                content = {
                    items(if (screenState.logged) ScreenFilterEnum.getFilters() else ScreenFilterEnum.getUnregisteredFilters()) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    when (it) {
                                        ScreenFilterEnum.ALL -> {
                                            viewModel.onEvent(
                                                TopicEvent.ChangeScreenFilter(
                                                    ScreenFilterEnum.ALL
                                                )
                                            )

                                            viewModel.onEvent(
                                                TopicEvent.ChangeCategoryId(
                                                    -1
                                                )
                                            )
                                        }

                                        ScreenFilterEnum.SUBSCRIPTION -> viewModel.onEvent(
                                            TopicEvent.ChangeScreenFilter(
                                                ScreenFilterEnum.SUBSCRIPTION
                                            )
                                        )

                                        ScreenFilterEnum.SAVED -> viewModel.onEvent(
                                            TopicEvent.ChangeScreenFilter(
                                                ScreenFilterEnum.SAVED
                                            )
                                        )

                                        ScreenFilterEnum.LIKED -> viewModel.onEvent(
                                            TopicEvent.ChangeScreenFilter(
                                                ScreenFilterEnum.LIKED
                                            )
                                        )

                                        ScreenFilterEnum.MY_ARTICLES -> viewModel.onEvent(
                                            TopicEvent.ChangeScreenFilter(
                                                ScreenFilterEnum.MY_ARTICLES
                                            )
                                        )

                                        ScreenFilterEnum.MY_DRAFTS -> viewModel.onEvent(
                                            TopicEvent.ChangeScreenFilter(
                                                ScreenFilterEnum.MY_DRAFTS
                                            )
                                        )

                                        ScreenFilterEnum.CATEGORIES -> viewModel.onEvent(
                                            TopicEvent.ChangeScreenFilter(
                                                ScreenFilterEnum.CATEGORIES
                                            )
                                        )
                                    }

                                    viewModel.onEvent(TopicEvent.ArticleRefresh)
                                },
                            color = if (it == filterState.type) NewBlue.copy(alpha = 0.9f) else TextColor.copy(
                                alpha = 0.9f
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .widthIn(min = 50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = when (it) {
                                        ScreenFilterEnum.ALL -> StringResourceUtils.getStringResource(
                                            R.string.all_screen_filter_name_hint,
                                            screenState.language,
                                            LocalContext.current
                                        )

                                        ScreenFilterEnum.SUBSCRIPTION -> StringResourceUtils.getStringResource(
                                            R.string.subscriptions_screen_filter_name_hint,
                                            screenState.language,
                                            LocalContext.current
                                        )

                                        ScreenFilterEnum.SAVED -> StringResourceUtils.getStringResource(
                                            R.string.saved_screen_filter_name_hint,
                                            screenState.language,
                                            LocalContext.current
                                        )

                                        ScreenFilterEnum.LIKED -> StringResourceUtils.getStringResource(
                                            R.string.liked_screen_filter_name_hint,
                                            screenState.language,
                                            LocalContext.current
                                        )

                                        ScreenFilterEnum.MY_ARTICLES -> StringResourceUtils.getStringResource(
                                            R.string.my_articles_screen_filter_name_hint,
                                            screenState.language,
                                            LocalContext.current
                                        )

                                        ScreenFilterEnum.MY_DRAFTS -> StringResourceUtils.getStringResource(
                                            R.string.my_drafts_screen_filter_name_hint,
                                            screenState.language,
                                            LocalContext.current
                                        )

                                        ScreenFilterEnum.CATEGORIES -> StringResourceUtils.getStringResource(
                                            R.string.categories_screen_filter_name_hint,
                                            screenState.language,
                                            LocalContext.current
                                        )
                                    },
                                    color = NewWhite,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        }

                    }
                },
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,

                modifier = Modifier
                    .fillMaxWidth()
            )

            if (filterState.type == ScreenFilterEnum.ALL || filterState.type == ScreenFilterEnum.CATEGORIES) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = StringResourceUtils.getStringResource(
                            R.string.categories_name,
                            screenState.language,
                            LocalContext.current
                        ),
                        color = TextColor,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp
                        )
                    )
                }

                LazyRow(
                    content = {
                        items(categoryState.categories) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (it.id != filterState.categoryId) {
                                            viewModel.onEvent(
                                                TopicEvent.ChangeScreenFilter(
                                                    ScreenFilterEnum.CATEGORIES
                                                )
                                            )
                                            viewModel.onEvent(TopicEvent.ChangeCategoryId(it.id))
                                            viewModel.onEvent(TopicEvent.ArticleRefresh)
                                        }

                                    },
                                color = if (it.id == filterState.categoryId) NewBlue.copy(alpha = 0.9f) else TextColor.copy(
                                    alpha = 0.5f
                                ),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .widthIn(min = 50.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = it.name,
                                        color = NewWhite,
                                        style = MaterialTheme.typography.bodyMedium,

                                        )
                                }

                            }

                        }
                    },
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,

                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            if (filterState.type == ScreenFilterEnum.SUBSCRIPTION) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = StringResourceUtils.getStringResource(
                            R.string.subscription_name,
                            screenState.language,
                            LocalContext.current
                        ),
                        color = TextColor,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp
                        )
                    )
                }

                LazyRow(
                    content = {
                        items(subscriptionState.subscriptions) { authorId ->
                            val authorScope = rememberCoroutineScope()
                            var author by remember {
                                mutableStateOf(UserInfoResponse(0, "", "", "", "", 0))
                            }
                            LaunchedEffect(subscriptionState.subscriptions) {
                                authorScope.launch {
                                    author = viewModel.getUserInfo(authorId)
                                }
                            }

                            Column(
                                modifier = Modifier.widthIn(max = 100.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    5.dp,
                                    Alignment.CenterVertically
                                )
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
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(TextColor)
                                        .border(
                                            BorderStroke(2.dp, NewBlue),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            viewModel.onEvent(TopicEvent.ChangeAuthorLogin(authorId))
                                            viewModel.onEvent(TopicEvent.OnlyArticleRefresh)
                                        },
                                    loading = {
                                        CircularProgressIndicator()
                                    }
                                )


                                Text(
                                    text = author.nickname,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp
                                    ),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    color = if (filterState.subscriptionId == authorId) NewBlue else TextColor,
                                )

                            }

                        }
                    },
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,

                    modifier = Modifier
                        .fillMaxWidth()
                )

                if (subscriptionState.subscriptions.isNotEmpty()) {
                    val authorScope = rememberCoroutineScope()

                    TextButton(
                        onClick = {
                            authorScope.launch {
                                val author = viewModel.getUserInfo(filterState.subscriptionId)
                                navController.navigate(
                                    Screen.AuthorProfileScreen.route + "?authorLogin=${author.login}"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = StringResourceUtils.getStringResource(
                                R.string.go_to_channel_name,
                                screenState.language,
                                LocalContext.current
                            ),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            color = NewBlue,
                        )
                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = StringResourceUtils.getStringResource(
                        R.string.articles_name,
                        screenState.language,
                        LocalContext.current
                    ),
                    color = TextColor,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 24.sp
                    )
                )
            }

            Box(
                Modifier
                    .pullRefresh(refreshState)
                    .zIndex(-1f)

            ) {
                ArticlesList(
                    viewModel,
                    navController,
                )

                PullRefreshIndicator(
                    screenState.refreshing,
                    refreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = NewWhite,
                    contentColor = TextColor
                )
            }

        }
    }
}

@Composable
private fun ArticlesList(
    viewModel: TopicsViewModel,
    navController: NavHostController
) {
    val screenState = viewModel.screenState.value
    val articlesState = viewModel.articlesState.value
    val userState = viewModel.userState.value
    val subscriptionState = viewModel.subscriptionState.value
    val filterState = viewModel.filterState.value

    LazyColumn(
        content = {
            if (screenState.refreshing) return@LazyColumn

            if (articlesState.all.articles.isEmpty()) {
                item {
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
            } else {

                items(articlesState.all.articles) { article ->
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

                    var changeSave by remember {
                        mutableStateOf(false)
                    }

                    var isSaved by remember {
                        mutableStateOf(CheckArticleLikeResponse(false))
                    }

                    val scope = rememberCoroutineScope()
                    var author by remember {
                        mutableStateOf(UserInfoResponse(0, "", "", "", "", 0))
                    }
                    LaunchedEffect(screenState.keyAuthorRefresh.size) {
                        author = viewModel.getUserInfo(article.userId)
                    }

                    LaunchedEffect(key1 = true) {
                        commentCount = viewModel.getArticleCommentsCount(article.id)
                    }

                    LaunchedEffect(key1 = changeLike) {
                        likeCount = viewModel.getArticleLikesCount(article.id)
                        isLiked = viewModel.checkArticleLike(article.id)
                    }

                    LaunchedEffect(key1 = changeSave) {
                        isSaved = viewModel.checkArticleSave(article.id)
                    }

                    ArticleItem(
                        onClick = { navController.navigate(Screen.WatchTopicScreen.route + "?topicId=${article.id}") },
                        onAuthorClick = { navController.navigate(Screen.AuthorProfileScreen.route + "?authorLogin=${author.login}") },
                        onSubscribe = {
                            if (subscriptionState.subscriptions.contains(author.id)) {
                                viewModel.onEvent(TopicEvent.Unsubscribe(author.login))
                            } else {
                                viewModel.onEvent(TopicEvent.Subscribe(author.login))
                            }
                            if (filterState.type == ScreenFilterEnum.SUBSCRIPTION) {
                                viewModel.onEvent(TopicEvent.ArticleRefresh)
                            } else {
                                scope.launch {
                                    delay(250)
                                    screenState.keyAuthorRefresh.add(0)
                                }
                            }
                        },
                        onEdit = {
                            navController.navigate(
                                Screen.EditTopicScreen.route + "?topicId=${article.id}"
                            )
                        },
                        onLike = {
                            if (screenState.logged) {
                                if (isLiked.liked) {
                                    viewModel.onEvent(TopicEvent.Unlike(article.id))
                                } else {
                                    viewModel.onEvent(TopicEvent.Like(article.id))
                                }
                                scope.launch {
                                    delay(250)
                                    changeLike = !changeLike
                                }
                            }
                        },
                        onComment = { navController.navigate(Screen.WatchTopicScreen.route + "?topicId=${article.id}") },
                        preview = article.preview,
                        authorAvatar = author.avatar,
                        authorNickname = author.nickname,
                        authorFollowers = "${author.subscriptions} " +
                                StringResourceUtils.getStringResource(
                                    R.string.followers_profile,
                                    screenState.language,
                                    LocalContext.current
                                ),
                        showSubscribe = userState.login != author.login && userState.login.isNotEmpty(),
                        subscribed = subscriptionState.subscriptions.contains(author.id),
                        userOwn = userState.login == author.login,
                        createdAt = article.createdAt.formatDateTime().toFormat(),
                        title = article.title,
                        description = article.description,
                        isLiked = isLiked.liked,
                        likeCount = "${likeCount.count}",
                        commentCount = "${commentCount.count}",
                        showAuthor = true,
                        bookmarked = isSaved.liked,
                        onBookmark = {
                            if (screenState.logged) {
                                if (isSaved.liked) {
                                    viewModel.onEvent(TopicEvent.Unsave(article.id))
                                } else {
                                    viewModel.onEvent(TopicEvent.Save(article.id))
                                }
                                scope.launch {
                                    delay(250)
                                    changeSave = !changeSave
                                }
                            }
                        },
                        logged = userState.login != author.login && userState.login.isNotEmpty()
                    )
                }
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    )
}
