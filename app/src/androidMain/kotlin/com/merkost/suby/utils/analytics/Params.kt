package com.merkost.suby.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

internal object Params {
    const val BUTTON_NAME = "button_name"
    const val SCREEN_NAME = FirebaseAnalytics.Param.SCREEN_NAME
    const val IS_VALID = "is_valid"
    const val ID = FirebaseAnalytics.Param.ITEM_ID
    const val NAME = FirebaseAnalytics.Param.ITEM_NAME
    const val PRICE = FirebaseAnalytics.Param.PRICE
    const val CURRENCY = FirebaseAnalytics.Param.CURRENCY
    const val CATEGORY = FirebaseAnalytics.Param.ITEM_CATEGORY
    const val PERIOD = FirebaseAnalytics.Param.TERM
    const val STATUS = FirebaseAnalytics.Param.CONTENT
    const val OLD_SERVICE_NAME = FirebaseAnalytics.Param.ITEM_NAME
    const val CUSTOM_NAME = FirebaseAnalytics.Param.CREATIVE_NAME
    const val GROUP_NAME  = FirebaseAnalytics.Param.GROUP_ID
}