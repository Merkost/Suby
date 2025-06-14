package com.merkost.suby.utils.transition

sealed class SharedTransitionKeys(val id: String) {
    sealed interface Subscription {
        data class ServiceLogoHomeToDescription(val subscriptionId: Int) : SharedTransitionKeys(
            "service_logo_home_to_description_$subscriptionId"
        )
    }
}