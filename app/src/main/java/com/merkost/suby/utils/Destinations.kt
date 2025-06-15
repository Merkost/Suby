package com.merkost.suby.utils

import kotlinx.serialization.Serializable

/**
 * Destinations used in the [SubyApp].
 */
sealed interface Destinations {

    @Serializable
    data class Feedback(val action: String, val text: String): Destinations

    @Serializable
    data object Greeting: Destinations

    @Serializable
    data object Onboarding: Destinations

    @Serializable
    data object OnboardingNotifications: Destinations

    @Serializable
    data object OnboardingComplete: Destinations

    @Serializable
    data object MainScreen: Destinations

    @Serializable
    data object NewSubscription: Destinations

    @Serializable
    data class CurrencyPick(
        val isMainCurrency: Boolean = false
    ): Destinations

    @Serializable
    data class SubscriptionInfo(
        val subscriptionId: Int,
    ): Destinations

    @Serializable
    data class EditSubscription(
        val subscriptionId: Int,
    ): Destinations

    @Serializable
    data object PremiumFeatures: Destinations

    @Serializable
    data object CalendarView: Destinations
    
    @Serializable
    data object About: Destinations
}

object Arguments {
    const val CURRENCY = "currency_arg"
}