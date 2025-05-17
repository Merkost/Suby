package com.merkost.suby.model.entity

import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.utils.dateString
import com.merkost.suby.utils.now
import com.merkost.suby.utils.toLocalDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate

data class NewSubscription(
    val price: String = "",
    val service: Service? = null,
    val period: BasePeriod? = null,
    val status: Status? = null,
    val billingDate: Long? = null,
    val paymentStartDate: Long? = null,
    val description: String = "",
) {
    val billingDateInfo: String?
        get() {
            if (billingDate == null || period == null || status == null) {
                return null
            }

            val currentDate = LocalDate.now().toJavaLocalDate()

            val endDate =
                period.nextBillingDate(billingDate.toLocalDate).toJavaLocalDate()

            val isPast = endDate.isBefore(currentDate) || endDate.isEqual(currentDate)

            val endDateFormatted = endDate.dateString()

            val subscriptionEndText = when {
                status == Status.ACTIVE && isPast -> {
                    val nextBillingDate =
                        period.nextBillingDateFromToday(billingDate.toLocalDate)
                            .toJavaLocalDate()
                    "Subscription renews on ${nextBillingDate.dateString()}"
                }

                status == Status.ACTIVE && !isPast -> "Subscription renews on $endDateFormatted"
                status == Status.CANCELED && isPast -> "Subscription was canceled on $endDateFormatted"
                status == Status.CANCELED && !isPast -> "Subscription will be canceled on $endDateFormatted"
                status == Status.EXPIRED && !isPast -> "Subscription expires on $endDateFormatted"
                status == Status.EXPIRED && isPast -> "Subscription expired on $endDateFormatted"
                status == Status.TRIAL && isPast -> "Trial period ended on $endDateFormatted"
                status == Status.TRIAL && !isPast -> "Trial period ends on $endDateFormatted"

                else -> "Subscription status: $status"
            }
            return subscriptionEndText
        }
}