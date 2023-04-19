package com.zhulin.guideshop.feature_topic.presentation.authorization

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.presentation.util.Screen
import com.zhulin.guideshop.core.utils.ScreenUtils
import com.zhulin.guideshop.core.utils.StringResourceUtils
import com.zhulin.guideshop.ui.theme.NewBlue
import com.zhulin.guideshop.ui.theme.NewCoffee
import com.zhulin.guideshop.ui.theme.NewWhite
import com.zhulin.guideshop.ui.theme.TextColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun AuthorizationScreen(
    navController: NavHostController,
    viewModel: AuthorizationViewModel = hiltViewModel()
) {
    val screenState = viewModel.screenState.value
    val signInState = viewModel.signInState.value
    val signUpState = viewModel.signUpState.value

    ScreenUtils.LockScreenOrientation(
        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    BackHandler {
        navController.navigate(Screen.SettingsScreen.route)
    }
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AuthorizationViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                AuthorizationViewModel.UiEvent.SignIn -> {
                    navController.navigate(Screen.TopicsScreen.route)
                }

                AuthorizationViewModel.UiEvent.BackMenu -> {
                    navController.navigate(Screen.TopicsScreen.route)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NewWhite,
                            NewCoffee
                        )
                    )

                )
                .padding(horizontal = 40.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                10.dp,
                alignment = Alignment.CenterVertically
            ),
        ) {
            Spacer(modifier = Modifier.weight(1f))
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
            if (screenState.signIn) {
                OutlinedTextField(
                    value = signInState.login,
                    onValueChange = {
                        if (!it.contains("\n") && !it.contains(" ") && !it.contains("\t") && it.length < 21) {
                            viewModel.onEvent(AuthorizationEvent.ChangeSingInLogin(it))
                        }
                    },
                    placeholder = {
                        Text(text = signInState.hintLogin)
                    },
                    label = {
                        Text(text = signInState.hintLogin)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
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

                OutlinedTextField(
                    value = signInState.password,
                    onValueChange = {
                        if (!it.contains("\n") && !it.contains(" ") && !it.contains("\t") && it.length < 21) {
                            viewModel.onEvent(AuthorizationEvent.ChangeSingInPassword(it))
                        }
                    },
                    placeholder = {
                        Text(text = signInState.hintPassword)
                    },
                    label = {
                        Text(text = signInState.hintPassword)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
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

                Button(
                    onClick = { viewModel.onEvent(AuthorizationEvent.SignIn) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NewBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = StringResourceUtils.getStringResource(
                            R.string.sign_in_button_text,
                            screenState.language,
                            LocalContext.current
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                LazyRow(
                    content = {
                        item {
                            Text(
                                text = StringResourceUtils.getStringResource(
                                    R.string.no_account_hint,
                                    screenState.language,
                                    LocalContext.current
                                )
                            )
                        }
                        item {
                            Text(
                                text = StringResourceUtils.getStringResource(
                                    R.string.create_one_hint,
                                    screenState.language,
                                    LocalContext.current
                                ),
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(
                                        AuthorizationEvent.ChangeSingIn
                                    )
                                },
                                color = NewBlue
                            )
                        }
                    },
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {

                OutlinedTextField(
                    value = signUpState.login,
                    onValueChange = {
                        if (!it.contains("\n") && !it.contains(" ") && !it.contains("\t") && it.length < 21) {
                            viewModel.onEvent(AuthorizationEvent.ChangeSingUpLogin(it))
                        }
                    },
                    placeholder = {
                        Text(text = signUpState.hintLogin)
                    },
                    label = {
                        Text(text = signUpState.hintLogin)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
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

                OutlinedTextField(
                    value = signUpState.nickname,
                    onValueChange = {
                        if (!it.contains("\n") && !it.contains(" ") && !it.contains("\t") && it.length < 21) {
                            viewModel.onEvent(AuthorizationEvent.ChangeSingUpNickname(it))
                        }
                    },
                    placeholder = {
                        Text(text = signUpState.hintNickname)
                    },
                    label = {
                        Text(text = signUpState.hintNickname)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
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

                OutlinedTextField(
                    value = signUpState.password,
                    onValueChange = {
                        if (!it.contains("\n") && !it.contains(" ") && !it.contains("\t") && it.length < 21) {
                            viewModel.onEvent(AuthorizationEvent.ChangeSingUpPassword(it))
                        }
                    },
                    placeholder = {
                        Text(text = signUpState.hintPassword)
                    },
                    label = {
                        Text(text = signUpState.hintPassword)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
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

                Button(
                    onClick = { viewModel.onEvent(AuthorizationEvent.SignUp) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NewBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = StringResourceUtils.getStringResource(
                            R.string.sign_up_button_text,
                            screenState.language,
                            LocalContext.current
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                LazyRow(
                    content = {
                        item {
                            Text(
                                text = StringResourceUtils.getStringResource(
                                    R.string.yes_account_hint,
                                    screenState.language,
                                    LocalContext.current
                                )
                            )
                        }
                        item {
                            Text(
                                text = StringResourceUtils.getStringResource(
                                    R.string.sign_in_account_hint,
                                    screenState.language,
                                    LocalContext.current
                                ),
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(
                                        AuthorizationEvent.ChangeSingIn
                                    )
                                },
                                color = NewBlue
                            )
                        }
                    },
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }


        }

    }
}