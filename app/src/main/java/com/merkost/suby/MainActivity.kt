package com.merkost.suby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.merkost.suby.presentation.viewModel.AppViewModel
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.utils.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Analytics.analyticsInit(BuildConfig.DEBUG)
        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            !viewModel.isAppReady.value
        }

        setContent {
            SubyTheme {
                SubyMainNavigation()
            }
        }
    }
}
