package com.merkost.suby.model

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Status
import com.merkost.suby.utils.Environment

object Analytics {

    private val analytics = Firebase.analytics

    fun analyticsInit() {
        analytics.setAnalyticsCollectionEnabled(!Environment.DEBUG)
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

    fun logUpdatedCustomService(oldServiceName: String, serviceName: String, categoryName: String) {
        analytics.logEvent("updated_custom_service") {
            param("old_service_name", oldServiceName)
            param("service_name", serviceName)
            param("category_name", categoryName)
        }
    }

    fun logCreatedCustomService(serviceName: String, categoryName: String) {
        analytics.logEvent("created_custom_service") {
            param("service_name", serviceName)
            param("category_name", categoryName)
        }
    }

}