package com.merkost.suby.presentation.states

import org.jetbrains.compose.resources.StringResource
import suby.app.generated.resources.Res
import suby.app.generated.resources.billing_date_required
import suby.app.generated.resources.period_required
import suby.app.generated.resources.price_required
import suby.app.generated.resources.service_required
import suby.app.generated.resources.status_required

sealed class NewSubscriptionUiState {
    data object Loading : NewSubscriptionUiState()

    sealed class Requirement(
        val stringResId: StringResource
    ) : NewSubscriptionUiState() {
        data object ServiceRequired : Requirement(Res.string.service_required)
        data object PeriodRequired : Requirement(Res.string.period_required)
        data object PriceRequired : Requirement(Res.string.price_required)
        data object BillingDateRequired : Requirement(Res.string.billing_date_required)
        data object StatusRequired : Requirement(Res.string.status_required)
    }

    data object Success : NewSubscriptionUiState()
    data object Error : NewSubscriptionUiState()
}
