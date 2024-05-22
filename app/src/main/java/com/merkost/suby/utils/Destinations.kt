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

    const val CURRENCY_PICK = "currency_pick"
    const val MAIN_CURRENCY_PICK = "main_currency_pick"

    @Serializable
    data class SubscriptionInfo(
        val subscriptionId: Int,
    )
}

object Arguments {
    const val CURRENCY = "currency_arg"
}