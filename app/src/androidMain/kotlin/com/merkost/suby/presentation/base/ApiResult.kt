package com.merkost.suby.presentation.base

import org.jetbrains.compose.resources.StringResource
import suby.app.generated.resources.Res
import suby.app.generated.resources.error_network
import suby.app.generated.resources.error_unknown

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

        private val infoResource: StringResource
            get() = when (this) {
                is NetworkError -> Res.string.error_network
                is UnknownError -> Res.string.error_unknown
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