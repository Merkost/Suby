package com.merkost.suby.model.entity

import org.jetbrains.compose.resources.StringResource
import suby.app.generated.resources.Res
import suby.app.generated.resources.add_new_currency
import suby.app.generated.resources.add_new_currency_question
import suby.app.generated.resources.add_new_service
import suby.app.generated.resources.add_new_service_question
import suby.app.generated.resources.feedback_general_failure
import suby.app.generated.resources.feedback_general_success

enum class FeedbackSection {
    GENERAL,
    FEATURE_REQUEST,
    BUG_REPORT,
    UI_IMPROVEMENT,
    OTHER;
}

enum class FeedbackAction(
    val messageRes: StringResource,
    val questionRes: StringResource,
    val section: FeedbackSection
) {
    ADD_CURRENCY(
        Res.string.add_new_currency, Res.string.add_new_currency_question,
        FeedbackSection.GENERAL
    ),
    ADD_SERVICE(
        Res.string.add_new_service, Res.string.add_new_service_question,
        FeedbackSection.GENERAL
    ), ;

    val successMessageRes: StringResource
        get() = Res.string.feedback_general_success

    val failureMessageRes: StringResource
        get() = Res.string.feedback_general_failure
}