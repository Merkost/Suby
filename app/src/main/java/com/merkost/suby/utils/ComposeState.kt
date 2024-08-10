package com.merkost.suby.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.merkost.suby.di.hiltActivityViewModel
import com.merkost.suby.viewModel.AppViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun hasSubscriptions(): State<Boolean> {
    val appViewModel = hiltActivityViewModel<AppViewModel>()
    val hasSubs = runBlocking { appViewModel.hasSubscriptions.first() }
    return produceState(hasSubs) {
        appViewModel.hasSubscriptions
    }
}

@Composable
fun isFirstTimeState(): State<Boolean> {
    val appViewModel = hiltActivityViewModel<AppViewModel>()
    val isFirstTime = runBlocking { appViewModel.isFirstTimeLaunch.first() }
    return produceState(isFirstTime) {
        appViewModel.isFirstTimeLaunch
    }
}