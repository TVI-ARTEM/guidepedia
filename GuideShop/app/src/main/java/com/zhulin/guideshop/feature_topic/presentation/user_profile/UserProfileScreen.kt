package com.zhulin.guideshop.feature_topic.presentation.user_profile

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.components.AddOneParamsDialog
import com.zhulin.guideshop.core.presentation.components.Keyboard
import com.zhulin.guideshop.core.presentation.components.TransparentHintTextField
import com.zhulin.guideshop.core.presentation.components.keyboardAsState
import com.zhulin.guideshop.core.presentation.util.Screen
import com.zhulin.guideshop.core.utils.StringResourceUtils
import com.zhulin.guideshop.ui.theme.NewBlue
import com.zhulin.guideshop.ui.theme.NewRed
import com.zhulin.guideshop.ui.theme.NewWhite
import com.zhulin.guideshop.ui.theme.NewYellow
import com.zhulin.guideshop.ui.theme.TextColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterial3Api
@Composable
fun UserProfileScreen(
    navController: NavHostController,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val screenState = viewModel.screenState.value
    val userScreenState = viewModel.userScreenState.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var nicknameField by remember { mutableStateOf(TextFieldValue(userScreenState.nickname)) }
    var hintVisibleNickname by remember { mutableStateOf(userScreenState.nickname.isEmpty()) }

    var profileField by remember { mutableStateOf(TextFieldValue(userScreenState.profile)) }
    var hintVisibleProfile by remember { mutableStateOf(userScreenState.profile.isEmpty()) }

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val state = rememberPullRefreshState(screenState.refreshing, {
        viewModel.onEvent(UserProfileEvent.Refresh)
    })

    val isKeyboardOpen by keyboardAsState()
    val onBack = {
        if (screenState.editing) {
            viewModel.onEvent(UserProfileEvent.ChangeEdit)
        } else {
            navController.navigate(Screen.TopicsScreen.route)
        }
    }
    BackHandler(onBack = onBack)

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UserProfileViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                UserProfileViewModel.UiEvent.Error -> {
                    navController.navigate(Screen.TopicsScreen.route)
                }

                UserProfileViewModel.UiEvent.Logout -> {
                    navController.navigate(Screen.TopicsScreen.route)
                }

                is UserProfileViewModel.UiEvent.UpdateFields -> {
                    nicknameField = TextFieldValue(event.nickname)
                    hintVisibleNickname = event.nickname.isEmpty()

                    profileField = TextFieldValue(event.profile)
                    hintVisibleProfile = event.profile.isEmpty()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            val configuration = LocalConfiguration.current

            if ((isKeyboardOpen == Keyboard.Closed || configuration.orientation == Configuration.ORIENTATION_PORTRAIT)) {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(weight = 1f, fill = false)
                            ) {
                                Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.my_profile_top,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Menu", modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            val configuration = LocalConfiguration.current

            if ((isKeyboardOpen == Keyboard.Closed || configuration.orientation == Configuration.ORIENTATION_PORTRAIT)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (screenState.editing) {
                        FloatingActionButton(
                            onClick = {
                                viewModel.onEvent(
                                    UserProfileEvent.SaveInfo(
                                        userScreenState.avatar,
                                        userScreenState.nickname,
                                        userScreenState.profile
                                    )
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp),
                            containerColor = NewBlue,
                            contentColor = NewWhite
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Edit",
                                modifier = Modifier.fillMaxSize(0.5f),
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = {
                            viewModel.onEvent(UserProfileEvent.ChangeEdit)
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp),
                        containerColor = if (screenState.editing) NewRed else NewBlue,
                        contentColor = NewWhite
                    ) {
                        Icon(
                            imageVector = if (screenState.editing) Icons.Default.Clear else Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.fillMaxSize(0.5f),
                        )
                    }
                }

            }

        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->

        Box(
            Modifier
                .pullRefresh(state)
                .padding(innerPadding)
        ) {
            LazyColumn(
                content = {
                    item {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(userScreenState.avatar)
                                .crossfade(true)
                                .transformations(
                                    CircleCropTransformation()
                                )
                                .build(),
                            contentDescription = "User avatar",
                            modifier = Modifier
                                .size(192.dp)
                                .clip(CircleShape)
                                .border(BorderStroke(2.dp, NewBlue), shape = CircleShape)
                                .background(TextColor)
                                .clickable {
                                    if (!screenState.editing) {
                                        viewModel.onEvent(UserProfileEvent.ChangeEdit)
                                    }
                                    viewModel.onEvent(UserProfileEvent.ChangeShowAvatarDialog)
                                },
                            loading = {
                                CircularProgressIndicator()
                            }
                        )
                    }

                    if (screenState.editing) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.nickname_hint,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                                )

                                TransparentHintTextField(
                                    text = nicknameField,
                                    hint = StringResourceUtils.getStringResource(
                                        R.string.nickname_hint,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    onValueChange = {
                                        if (!it.text.contains("\n") &&
                                            !it.text.contains(" ") &&
                                            !it.text.contains("\t") &&
                                            it.text.length < 21
                                        ) {
                                            nicknameField = it
                                            viewModel.onEvent(UserProfileEvent.ChangeNickname(it.text))
                                        }

                                    },
                                    onFocusChange = {
                                        hintVisibleNickname =
                                            !it.isFocused && nicknameField.text.isEmpty()
                                    },
                                    isHintVisible = hintVisibleNickname,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Blue.copy(alpha = 0.1f))
                                        .padding(10.dp),
                                    singleLine = true
                                )
                            }

                        }

                    } else {
                        item {
                            Text(
                                text = userScreenState.nickname,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(UserProfileEvent.ChangeEdit)
                                }
                            )
                        }
                    }

                    item {
                        Text(
                            text = "${userScreenState.followers} " + StringResourceUtils.getStringResource(
                                R.string.followers_profile,
                                screenState.language,
                                LocalContext.current
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (screenState.editing) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.profile_hint,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                                )

                                TransparentHintTextField(
                                    text = profileField,
                                    hint = StringResourceUtils.getStringResource(
                                        R.string.profile_hint,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    onValueChange = {

                                        profileField = it
                                        viewModel.onEvent(UserProfileEvent.ChangeProfile(it.text))

                                    },
                                    onFocusChange = {
                                        hintVisibleProfile =
                                            !it.isFocused && profileField.text.isEmpty()
                                    },
                                    isHintVisible = hintVisibleProfile,
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Blue.copy(alpha = 0.1f))
                                        .padding(10.dp),
                                )
                            }

                        }
                    } else {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {

                                Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.about_profile,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                                )


                                Text(
                                    text = userScreenState.profile,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.clickable {
                                        viewModel.onEvent(UserProfileEvent.ChangeEdit)
                                    }
                                )
                            }
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = StringResourceUtils.getStringResource(
                                    R.string.bank_details,
                                    screenState.language,
                                    LocalContext.current
                                ),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }

                    }
                    items(userScreenState.bankDetailsList.details) {
                        Row(
                            Modifier
                                .fillMaxWidth()
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
                                Text(
                                    text = StringResourceUtils.getStringResource(
                                        R.string.bank_detail,
                                        screenState.language,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                )

                                Text(
                                    text = it.number,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.clickable {
                                        clipboardManager.setText(AnnotatedString(it.number))
                                    }
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(UserProfileEvent.RemoveBank(it.id))
                                    },
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                        }
                    }
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(UserProfileEvent.ChangeShowBankDialog)
                                }
                            ) {
                                Icon(
                                    Icons.Default.AddCircle,
                                    contentDescription = "Delete", modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
            )

            PullRefreshIndicator(screenState.refreshing, state, Modifier.align(Alignment.TopCenter))

        }

        if (screenState.showAvatarDialog) {
            AddOneParamsDialog(
                title = StringResourceUtils.getStringResource(
                    R.string.avatar_title,
                    screenState.language,
                    LocalContext.current
                ),
                hintFirst = StringResourceUtils.getStringResource(
                    R.string.avatar_hint,
                    screenState.language,
                    LocalContext.current
                ),
                initialString = userScreenState.avatar,
                onDismissRequest = {
                    viewModel.onEvent(UserProfileEvent.ChangeShowAvatarDialog)
                },
                onConfirm = { avatar ->
                    viewModel.onEvent(
                        UserProfileEvent.ChangeAvatar(
                            avatar
                        )
                    )
                    viewModel.onEvent(UserProfileEvent.ChangeShowAvatarDialog)
                }
            )
        }

        if (screenState.showBankDialog) {
            AddOneParamsDialog(
                title = StringResourceUtils.getStringResource(
                    R.string.bank_title,
                    screenState.language,
                    LocalContext.current
                ),
                hintFirst = StringResourceUtils.getStringResource(
                    R.string.bank_hint,
                    screenState.language,
                    LocalContext.current
                ),
                onDismissRequest = {
                    viewModel.onEvent(UserProfileEvent.ChangeShowBankDialog)
                },
                onConfirm = { detail ->
                    viewModel.onEvent(
                        UserProfileEvent.AddBank(
                            detail
                        )
                    )
                    viewModel.onEvent(UserProfileEvent.ChangeShowBankDialog)
                }
            )
        }
    }
}