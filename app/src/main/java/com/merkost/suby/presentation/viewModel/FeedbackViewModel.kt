package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.repository.ktor.api.SupabaseApi
import com.merkost.suby.utils.BaseViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FeedbackViewModel(
    private val supabaseApi: SupabaseApi
) : ViewModel() {

    private val _feedbackState = MutableStateFlow<BaseViewState<Unit>?>(null)
    val feedbackState = _feedbackState.asStateFlow()

    fun submitServiceRequest(serviceName: String) {
        viewModelScope.launch {
            // TODO: replace to the repo interface
            _feedbackState.update { BaseViewState.Loading }
            val result = supabaseApi.submitServiceRequest(serviceName).single()
            _feedbackState.update { result.toState() }
        }
    }

}

private fun <T> Result<T>.toState(): BaseViewState<T> {
    return if (isSuccess) this.getOrNull()?.let { BaseViewState.Success(it) } as BaseViewState<T>
    else BaseViewState.Error()
}
