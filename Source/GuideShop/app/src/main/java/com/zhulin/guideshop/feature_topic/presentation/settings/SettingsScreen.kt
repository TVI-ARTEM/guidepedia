package com.zhulin.guideshop.feature_topic.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.presentation.util.Screen
import com.zhulin.guideshop.core.utils.StringResourceUtils
import com.zhulin.guideshop.ui.theme.NewBlue
import com.zhulin.guideshop.ui.theme.NewRed
import com.zhulin.guideshop.ui.theme.NewWhite
import com.zhulin.guideshop.ui.theme.NewYellow
import com.zhulin.guideshop.ui.theme.TextColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val screenState = viewModel.screenState.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val onBack = {
        navController.navigate(Screen.TopicsScreen.route)

    }
    BackHandler(onBack = onBack)
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SettingsViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                SettingsViewModel.UiEvent.Login -> {
                    navController.navigate(Screen.AuthorizationScreen.route)
                }
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },

        topBar = {
            TopAppBar(
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Menu", modifier = Modifier.fillMaxSize()
                        )
                    }
                },
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
                                    R.string.settings_top,
                                    screenState.language,
                                    LocalContext.current
                                ),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize(1f)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
        ) {
            Text(
                text = StringResourceUtils.getStringResource(
                    R.string.guidepedia,
                    Language.EN,
                    LocalContext.current
                ), color = TextColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 48.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onEvent(SettingsEvent.ChangeLanguage) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NewYellow),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(
                    text = StringResourceUtils.getStringResource(
                        R.string.language_button_text,
                        screenState.language,
                        LocalContext.current
                    ), color = TextColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
            }

            if (screenState.logged) {
                Button(
                    onClick = { viewModel.onEvent(SettingsEvent.Logout) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NewRed),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Text(
                        text = StringResourceUtils.getStringResource(
                            R.string.logout_button_text,
                            screenState.language,
                            LocalContext.current
                        ), color = NewWhite,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            } else {
                Button(
                    onClick = { viewModel.onEvent(SettingsEvent.Login) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NewBlue),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Text(
                        text = StringResourceUtils.getStringResource(
                            R.string.login_button_text,
                            screenState.language,
                            LocalContext.current
                        ), color = NewWhite,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }

    }
}