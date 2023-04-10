package com.zhulin.guideshop.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.zhulin.guideshop.core.presentation.util.Screen
import com.zhulin.guideshop.ui.theme.GuideShopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuideShopTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GuideNavigation(
                        navController = rememberNavController(),
                        startDestination = Screen.TopicsScreen.route
                    )
                }
            }
        }
    }
}

