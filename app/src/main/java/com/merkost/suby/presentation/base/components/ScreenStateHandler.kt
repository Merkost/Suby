package com.merkost.suby.presentation.base.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.presentation.base.UiState

@Composable
fun <T> ScreenStateHandler(
    screenState: UiState<T>,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = {
        LottieLoading(
            modifier = Modifier.size(226.dp),
            resId = R.raw.loading_wave
        )
    },
    errorContent: @Composable (String) -> Unit = { message ->
        ErrorView(
            message = message,
            onRetry = onRetry
        )
    },
    successContent: @Composable (T) -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = screenState,
        label = "screenStateHandlerAnim",
        transitionSpec = { screenStateTransitionSpec() }
    ) { state ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is UiState.Loading -> {
                    loadingContent()
                }

                is UiState.Error -> {
                    errorContent(stringResource(state.messageRes))
                }

                is UiState.Success -> {
                    successContent(state.data)
                }
            }
        }
    }
}