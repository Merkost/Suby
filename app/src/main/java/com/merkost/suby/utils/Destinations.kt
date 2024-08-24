package com.merkost.suby.utils

import kotlinx.serialization.Serializable

/**
 * Destinations used in the [SubyApp].
 */
object Destinations {


    @Serializable
    data class Feedback(
        val action: String,
        val text: String
    )

    const val GREETING = "greeting"
    const val ONBOARDING = "onboarding"
    const val ONBOARDING_CURRENCY = "onboarding_currency"

    const val MAIN_SCREEN = "main"
    const val NEW_SUBSCRIPTION = "new"

    @Serializable
    data class CurrencyPick(
        val isMainCurrency: Boolean = false
    )

    @Serializable
    data class SubscriptionInfo(
        val subscriptionId: Int,
    )

    @Serializable
    data class EditSubscription(
        val subscriptionId: Int,
    )
}

object Arguments {
    const val CURRENCY = "currency_arg"
}