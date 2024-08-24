package com.merkost.suby.presentation.viewModel

sealed interface UiState {
    data object Success: UiState
    data object Error: UiState
    data object Loading: UiState
}