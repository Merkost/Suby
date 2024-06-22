package com.merkost.suby.model

import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.utils.now
import com.merkost.suby.utils.toLocalDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

data class NewSubscription(
    val price: String = "",
    val service: Service? = null,
    val period: Period? = null,
    val customPeriodType: CustomPeriodType? = null,
    val customPeriodDuration: Long? = null,
    val status: Status? = null,
    val billingDate: Long? = null,
    val description: String = "",
) {
    val billingDateInfo: String?
        get() {
            if (billingDate == null || period == null || status == null) {
                return null
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val endDate = period.nextBillingDate(billingDate.toLocalDate)
                .toJavaLocalDate()

            val currentDate = LocalDate.now().toJavaLocalDate()
            val isPast = endDate.isBefore(currentDate) || endDate.isEqual(currentDate)

            val subscriptionEndText = when {
                status == Status.ACTIVE && isPast -> "Subscription renewed on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.ACTIVE && !isPast -> "Subscription will be renewed on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.CANCELED && isPast -> "Subscription was canceled on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.CANCELED && !isPast -> "Subscription will be canceled on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.EXPIRED && !isPast -> "Subscription expires on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.EXPIRED && isPast -> "Subscription expired on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.TRIAL && isPast -> "Trial period ended on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.TRIAL && !isPast -> "Trial period will end on ${
                    endDate.format(
                        formatter
                    )
                }"

                else -> "Subscription status: $status"
            }
            return subscriptionEndText
        }
}