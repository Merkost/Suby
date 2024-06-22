package com.merkost.suby.model.entity

import androidx.annotation.StringRes
import com.merkost.suby.R

enum class FeedbackSection {
    GENERAL,
    FEATURE_REQUEST,
    BUG_REPORT,
    UI_IMPROVEMENT,
    OTHER;
}

enum class FeedbackAction(
    @StringRes val messageRes: Int,
    @StringRes val questionRes: Int,
    val section: FeedbackSection
) {
    ADD_CURRENCY(R.string.add_new_currency, R.string.add_new_currency_question,
        FeedbackSection.GENERAL
    ),
    ADD_SERVICE(R.string.add_new_service, R.string.add_new_service_question,
        FeedbackSection.GENERAL
    ),;

    val successMessageRes: Int
        get() = R.string.feedback_general_success

    val failureMessageRes: Int
        get() = R.string.feedback_general_failure
}