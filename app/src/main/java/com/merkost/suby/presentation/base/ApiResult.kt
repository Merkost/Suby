package com.merkost.suby.presentation.base

import com.merkost.suby.R

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    sealed class Error(private val exception: Throwable) : ApiResult<Nothing>() {

        fun <T> toUiState(): BaseUiState<T> {
            return BaseUiState.Error(
                message = exception.message.orEmpty(),
                messageRes = infoResource,
            )
        }

        companion object {
            fun fromThrowable(exception: Throwable): Error {
                return UnknownError(exception)
            }
        }

        data class NetworkError(val e: Throwable) : Error(e)
        data class UnknownError(val e: Throwable) : Error(e)

        private val infoResource: Int
            get() = when (this) {
                is NetworkError -> R.string.error_network
                is UnknownError -> R.string.error_unknown
            }
    }

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Error) -> R
    ): R {
        return when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(this)
        }
    }
}