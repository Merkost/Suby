package com.merkost.suby.utils.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.amplitude.android.Amplitude
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Status
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Analytics : KoinComponent {
    private val amplitude: Amplitude by inject()
    private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }

    /**
     * Initializes analytics settings.
     */
    fun analyticsInit(isDebug: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(!isDebug)
    }

    /**
     * Logs a general event with optional parameters to both Amplitude and Firebase Analytics.
     *
     * @param eventName The name of the event to log.
     * @param params Optional map of parameters associated with the event.
     */
    private fun logEvent(eventName: String, params: Map<String, Any>) {
        if (params.isNotEmpty()) {
            amplitude.track(eventName, params)
        } else {
            amplitude.track(eventName)
        }

        val bundle = Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putFloat(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                else -> bundle.putString(key, value.toString())
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    /**
     * Logs a button click event.
     *
     * @param buttonName The name of the button that was clicked.
     */
    fun logButtonClicked(
        buttonName: String,
        arguments: Map<String, Any> = emptyMap()
    ) {
        logEvent(
            Events.BUTTON_CLICKED,
            mapOf(Params.BUTTON_NAME to buttonName) + arguments
        )
    }

    /**
     * Logs an item selection event.
     *
     * @param itemId The identifier of the selected item.
     */
    fun logServiceSelected(id: String, name: String, isCustom: Boolean) {
        logEvent(
            Events.ITEM_SELECTED,
            mapOf(
                Params.ID to id,
                Params.NAME to name,
                Params.CATEGORY to if (isCustom) "custom" else "predefined",
            )
        )
    }

    /**
     * Logs a screen view event using predefined screen constants.
     *
     * @param screen The screen constant representing the screen being viewed.
     */
    fun logScreenView(screen: Screens) {
        amplitude.track(Events.SCREEN_VIEW, mapOf(Params.SCREEN_NAME to screen.screenName))
        firebaseAnalytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(Params.SCREEN_NAME to screen.screenName)
        )
    }

    /**
     * Logs the event of adding a subscription.
     *
     * @param serviceName Name of the service.
     * @param price Price of the subscription.
     * @param currency Currency type.
     * @param isCustom Whether the subscription is custom.
     * @param period Subscription period.
     * @param status Status of the subscription.
     */
    fun logAddedSubscription(
        serviceId: Int,
        serviceName: String,
        price: String,
        currency: Currency,
        isCustom: Boolean,
        period: BasePeriod,
        status: Status
    ) {
        logEvent(
            Events.ADDED_SUBSCRIPTION,
            mapOf(
                Params.ID to serviceId,
                Params.NAME to serviceName,
                Params.PRICE to price,
                Params.CURRENCY to currency.toString(),
                Params.CATEGORY to if (isCustom) "custom" else "predefined",
                Params.PERIOD to period.toString(),
                Params.STATUS to status.toString()
            )
        )
    }

    /**
     * Logs the event of updating a custom service.
     *
     * @param oldServiceName The previous name of the service.
     * @param serviceName The new name of the service.
     * @param categoryName The category name associated with the service.
     */
    fun logUpdatedCustomService(oldServiceName: String, serviceName: String, categoryName: String) {
        logEvent(
            Events.UPDATED_CUSTOM_SERVICE,
            mapOf(
                Params.OLD_SERVICE_NAME to oldServiceName,
                Params.NAME to serviceName,
                Params.GROUP_NAME to categoryName
            )
        )
    }

    /**
     * Logs the event of creating a custom service.
     *
     * @param serviceName The name of the new service.
     * @param categoryName The category name associated with the service.
     */
    fun logCreatedCustomService(serviceName: String, categoryName: String) {
        logEvent(
            Events.CREATED_CUSTOM_SERVICE,
            mapOf(
                Params.CUSTOM_NAME to serviceName,
                Params.GROUP_NAME to categoryName
            )
        )
    }

}