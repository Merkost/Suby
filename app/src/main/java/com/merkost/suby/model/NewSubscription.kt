package com.merkost.suby.model

import com.merkost.suby.model.room.Status
import com.merkost.suby.now
import com.merkost.suby.toLocalDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

data class NewSubscription(
    val price: String = "",
    val service: Service? = null,
    val customService: CustomService? = null,
    val period: Period? = null,
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
                status == Status.ACTIVE && isPast -> "Your subscription ended on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.ACTIVE && !isPast -> "Your subscription will end on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.CANCELED && isPast -> "Your subscription was canceled on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.CANCELED && !isPast -> "Your subscription will be canceled on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.EXPIRED && !isPast -> "Your subscription expires on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.EXPIRED && isPast -> "Your subscription expired on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.TRIAL && isPast -> "Your trial period ended on ${
                    endDate.format(
                        formatter
                    )
                }"

                status == Status.TRIAL && !isPast -> "Your trial period will end on ${
                    endDate.format(
                        formatter
                    )
                }"

                else -> "Subscription status: $status"
            }
            return subscriptionEndText
        }
}