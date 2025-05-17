package com.merkost.suby.presentation.base.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merkost.suby.presentation.base.BaseUiState
import com.merkost.suby.utils.LottieFiles
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> ScreenStateHandler(
    screenState: BaseUiState<T>,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = {
        LottieLoading(
            modifier = Modifier.size(226.dp),
            LottieFiles.Loading
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
        when (state) {
            is BaseUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { loadingContent() }
            }

            is BaseUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { errorContent(stringResource(state.messageRes)) }
            }

            is BaseUiState.Success -> {
                successContent(state.data)
            }
        }
    }
}