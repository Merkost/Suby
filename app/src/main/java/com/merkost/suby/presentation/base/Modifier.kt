package com.merkost.suby.presentation.base

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.merkost.suby.R
import com.merkost.suby.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DoubleBackPressHandler(enabled: Boolean = true) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isBackPressed = remember { mutableStateOf(false) }
    BackHandler(enabled && !isBackPressed.value) {
        isBackPressed.value = true
        context.showToast(R.string.press_back_to_exit)
        scope.launch {
            delay(2000L)
            isBackPressed.value = false
        }
    }
}