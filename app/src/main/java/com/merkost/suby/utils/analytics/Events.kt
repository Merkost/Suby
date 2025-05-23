package com.merkost.suby.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics.Event

internal object Events {
    const val SCREEN_VIEW = "screen_view"
    const val BUTTON_CLICKED = "button_clicked"
    const val PREMIUM_BOUGHT = Event.PURCHASE
    const val ITEM_SELECTED = "item_selected"
    const val ADDED_SUBSCRIPTION = "added_subscription"
    const val UPDATED_CUSTOM_SERVICE = "updated_custom_service"
    const val CREATED_CUSTOM_SERVICE = "created_custom_service"
}