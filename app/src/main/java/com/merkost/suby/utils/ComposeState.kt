package com.merkost.suby.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.merkost.suby.repository.datastore.AppStateRepository
import com.merkost.suby.ui.theme.AppState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

@Composable
fun appState(): State<AppState> {
    val appRepository = koinInject<AppStateRepository>()
    val appState = runBlocking { appRepository.appState.first() }
    return produceState(appState) {
        appRepository.appState.collect { value = it }
    }
}