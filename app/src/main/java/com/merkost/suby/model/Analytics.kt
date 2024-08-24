package com.merkost.suby.model

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Status

object Analytics {

    private val analytics = Firebase.analytics

    fun analyticsInit() {
        analytics.setAnalyticsCollectionEnabled(true)
    }

    fun logAddedSubscription(
        serviceName: String,
        price: String,
        currency: Currency,
        isCustom: Boolean,
        period: BasePeriod,
        status: Status
    ) {
        analytics.logEvent("added_subscription") {
            param("service_name", serviceName)
            param("price", price)
            param("currency", currency.toString())
            param("period", period.toString())
            param("status", status.toString())
            param("is_custom", isCustom.toString())
        }
    }

    fun logCreatedCustomService(serviceName: String, categoryName: String) {
        analytics.logEvent("created_custom_service") {
            param("service_name", serviceName)
            param("category_name", categoryName)
        }
    }

}