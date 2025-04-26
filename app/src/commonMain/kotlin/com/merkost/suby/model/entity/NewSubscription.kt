package com.merkost.suby.model.entity

import androidx.compose.runtime.Composable
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.utils.DateFormats
import com.merkost.suby.utils.format
import com.merkost.suby.utils.now
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import suby.app.generated.resources.Res
import suby.app.generated.resources.subscription_expired_on
import suby.app.generated.resources.subscription_expires_on
import suby.app.generated.resources.subscription_renews_on
import suby.app.generated.resources.subscription_was_canceled_on
import suby.app.generated.resources.subscription_will_be_canceled_on
import suby.app.generated.resources.trial_period_ended_on
import suby.app.generated.resources.trial_period_ends_on

data class NewSubscription(
    val price: String = "",
    val service: Service? = null,
    val period: BasePeriod? = null,
    val status: Status? = null,
    val billingDate: Long? = null,
    val description: String = "",
) {
    val billingDateInfo: String?
        @Composable get() {
            if (billingDate == null || period == null || status == null) return null

            val today = LocalDate.now()

            val billingLocal = Instant
                .fromEpochMilliseconds(billingDate)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date

            val nextDue = period.nextBillingDate(billingLocal)
            val isPastOrToday = nextDue <= today

            val (resId, dateToShow) = when (status) {
                Status.ACTIVE -> if (isPastOrToday)
                    Res.string.subscription_renews_on to period.nextBillingDateFromToday(
                        billingLocal
                    )
                else
                    Res.string.subscription_renews_on to nextDue

                Status.CANCELED -> if (isPastOrToday)
                    Res.string.subscription_was_canceled_on to nextDue
                else
                    Res.string.subscription_will_be_canceled_on to nextDue

                Status.EXPIRED -> if (isPastOrToday)
                    Res.string.subscription_expired_on to nextDue
                else
                    Res.string.subscription_expires_on to nextDue

                Status.TRIAL -> if (isPastOrToday)
                    Res.string.trial_period_ended_on to nextDue
                else
                    Res.string.trial_period_ends_on to nextDue
            }

            val formatted = dateToShow.format(DateFormats.JUST_DATE)
            return stringResource(resId, formatted)
        }

}