package com.merkost.suby.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.merkost.suby.di.koinActivityViewModel
import com.merkost.suby.presentation.viewModel.AppViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun hasSubscriptions(): State<Boolean> {
    val appViewModel = koinActivityViewModel<AppViewModel>()
    val hasSubs = runBlocking { appViewModel.hasSubscriptions.first() }
    return produceState(hasSubs) {
        appViewModel.hasSubscriptions.collect { value = it }
    }
}

@Composable
fun isFirstTimeState(): State<Boolean> {
    val appViewModel = koinActivityViewModel<AppViewModel>()
    val isFirstTime = runBlocking { appViewModel.isFirstTimeLaunch.first() }
    return produceState(isFirstTime) {
        appViewModel.isFirstTimeLaunch.collect { value = it }
    }
}