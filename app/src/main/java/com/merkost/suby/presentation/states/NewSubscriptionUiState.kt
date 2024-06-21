package com.merkost.suby.presentation.states

sealed class NewSubscriptionUiState {
    data object Loading : NewSubscriptionUiState()
    data object ServiceRequired: NewSubscriptionUiState()
    data object PeriodRequired: NewSubscriptionUiState()
    data object PriceRequired: NewSubscriptionUiState()
    data object BillingDateRequired: NewSubscriptionUiState()
    data object Success : NewSubscriptionUiState()
    data object Error : NewSubscriptionUiState()
}
