package com.zhulin.guideshop.core.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zhulin.guideshop.core.presentation.util.Screen
import com.zhulin.guideshop.feature_topic.presentation.author_profile.AuthorProfileScreen
import com.zhulin.guideshop.feature_topic.presentation.authorization.AuthorizationScreen
import com.zhulin.guideshop.feature_topic.presentation.edit_topic.EditTopicScreen
import com.zhulin.guideshop.feature_topic.presentation.settings.SettingsScreen
import com.zhulin.guideshop.feature_topic.presentation.topics.TopicsScreen
import com.zhulin.guideshop.feature_topic.presentation.user_profile.UserProfileScreen
import com.zhulin.guideshop.feature_topic.presentation.watch_topic.WatchTopicScreen

@ExperimentalMaterial3Api
@Composable
fun GuideNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Screen.TopicsScreen.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.AuthorizationScreen.route) {
            AuthorizationScreen(navController = navController)
        }
        composable(Screen.TopicsScreen.route) {
            TopicsScreen(navController = navController)
        }
        composable(route = Screen.EditTopicScreen.route + "?topicId={topicId}",
            arguments = listOf(
                navArgument(
                    name = "topicId"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )) {
            EditTopicScreen(navController = navController)
        }
        composable(Screen.UserProfileScreen.route) {
            UserProfileScreen(navController = navController)
        }
        composable(route = Screen.AuthorProfileScreen.route + "?authorLogin={authorLogin}",
            arguments = listOf(
                navArgument(
                    name = "authorLogin"
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )) {
            AuthorProfileScreen(navController = navController)
        }

        composable(Screen.SettingsScreen.route) {
            SettingsScreen(navController = navController)
        }
        composable(
            route = Screen.WatchTopicScreen.route + "?topicId={topicId}",
            arguments = listOf(
                navArgument(
                    name = "topicId"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            WatchTopicScreen(navController = navController)
        }
    }
}