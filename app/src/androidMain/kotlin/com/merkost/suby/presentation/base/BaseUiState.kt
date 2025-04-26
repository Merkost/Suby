package com.merkost.suby.presentation.base

import org.jetbrains.compose.resources.StringResource

sealed interface BaseUiState<out T> {
    data object Loading : BaseUiState<Nothing>
    data class Success<out T>(val data: T) : BaseUiState<T>
    data class Error(
        val message: String,
        val messageRes: StringResource
    ) : BaseUiState<Nothing>
}