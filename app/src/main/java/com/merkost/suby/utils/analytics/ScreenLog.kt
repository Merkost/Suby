package com.merkost.suby.utils.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ScreenLog(screens: Screens) {
    LaunchedEffect(Unit) {
        Analytics.logScreenView(screens)
    }
}