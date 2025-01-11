package com.merkost.suby.presentation.base

import androidx.annotation.StringRes

sealed interface BaseUiState<out T> {
    data object Loading : BaseUiState<Nothing>
    data class Success<out T>(val data: T) : BaseUiState<T>
    data class Error(
        val message: String,
        @StringRes val messageRes: Int
    ) : BaseUiState<Nothing>
}