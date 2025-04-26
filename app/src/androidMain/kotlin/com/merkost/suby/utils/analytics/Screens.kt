package com.merkost.suby.utils.analytics

sealed class Screens(
    val screenName: String
) {
    data object Main : Screens("main")
    data object Greeting : Screens("greeting")
    data object Onboarding : Screens("onboarding")
    data object Premium : Screens("premium")
    data object NewSubscription : Screens("new_subscription")
    data object EditSubscription : Screens("edit_subscription")
    data object Currency : Screens("currency")
    data object SubscriptionDetails : Screens("subscription_details")
    data object Services : Screens("services")
    data object CustomServices : Screens("custom_services")
    data object CreateCustomService : Screens("create_custom_service")
    data object EditCustomService : Screens("edit_custom_service")
    data object Feedback : Screens("feedback")
    data object FiltersAndSort : Screens("filters_and_sort")
}