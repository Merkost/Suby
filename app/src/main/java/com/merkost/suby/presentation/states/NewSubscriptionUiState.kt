package com.merkost.suby.presentation.states

import androidx.annotation.StringRes
import com.merkost.suby.R

sealed class NewSubscriptionUiState {
    data object Loading : NewSubscriptionUiState()

    sealed class Requirement(
        @StringRes val stringResId: Int
    ) : NewSubscriptionUiState() {
        data object ServiceRequired : Requirement(R.string.service_required)
        data object PeriodRequired : Requirement(R.string.period_required)
        data object PriceRequired : Requirement(R.string.price_required)
        data object BillingDateRequired : Requirement(R.string.billing_date_required)
    }

    data object Success : NewSubscriptionUiState()
    data object Error : NewSubscriptionUiState()
}
