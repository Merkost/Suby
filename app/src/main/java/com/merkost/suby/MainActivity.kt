package com.merkost.suby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.merkost.suby.model.Analytics
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.presentation.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics.analyticsInit()

        enableEdgeToEdge(
            SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        val splashScreen = installSplashScreen()

        setContent {
            SubyTheme {
                SubyMainApp()
            }
        }
    }
}
