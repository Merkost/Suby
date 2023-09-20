package com.merkost.suby.utils

import androidx.annotation.StringRes

sealed class BaseViewState<out T> {
    data class Success<out T>(val data: T, val isLocal: Boolean = false) : BaseViewState<T>()
    data class Error(
        val text: String = "",
        val error: Throwable? = null,
        @StringRes val errorRes: Int? = null,
    ) : BaseViewState<Nothing>()

    data object Loading : BaseViewState<Nothing>()
}