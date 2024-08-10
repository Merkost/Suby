package com.merkost.suby.model.entity

import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.utils.Constants.DEFAULT_CUSTOM_PERIOD
import com.merkost.suby.utils.dateString
import com.merkost.suby.utils.now
import com.merkost.suby.utils.toLocalDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import timber.log.Timber

data class NewSubscription(
    val price: String = "",
    val service: Service? = null,
    val period: Period? = null,
    val customPeriodType: CustomPeriod? = null,
    val customPeriodDuration: Long? = null,
    val status: Status? = null,
    val billingDate: Long? = null,
    val description: String = "",
) {
    val basePeriod: BasePeriod?
        get() {
            return kotlin.runCatching {
                if (period == Period.CUSTOM) {
                    val type = customPeriodType ?: CustomPeriod.DAYS
                    val duration = customPeriodDuration ?: 1L
                    BasePeriod(type, duration)
                } else {
                    when (period) {
                        Period.DAILY -> BasePeriod(CustomPeriod.DAYS, 1L)
                        Period.WEEKLY -> BasePeriod(CustomPeriod.WEEKS, 1L)
                        Period.BI_WEEKLY -> BasePeriod(CustomPeriod.WEEKS, 2L)
                        Period.MONTHLY -> BasePeriod(CustomPeriod.MONTHS, 1L)
                        Period.QUARTERLY -> BasePeriod(CustomPeriod.MONTHS, 3L)
                        Period.SEMI_ANNUAL -> BasePeriod(CustomPeriod.MONTHS, 6L)
                        Period.ANNUAL -> BasePeriod(CustomPeriod.YEARS, 1L)
                        else -> BasePeriod(CustomPeriod.DAYS, 1L) // Default case
                    }
                }
            }.onFailure {
                Timber.tag("NewSubscription").e(it, "Error getting base period")
            }.getOrNull()
        }

    val billingDateInfo: String?
        get() {
            if (billingDate == null || period == null || status == null) {
                return null
            }

            val currentDate = LocalDate.now().toJavaLocalDate()
            val basePeriod = basePeriod ?: return null

            val endDate =
                basePeriod.nextBillingDate(billingDate.toLocalDate).toJavaLocalDate()

            val isPast = endDate.isBefore(currentDate) || endDate.isEqual(currentDate)

            val endDateFormatted = endDate.dateString()

            val subscriptionEndText = when {
                status == Status.ACTIVE && isPast -> {
                    val nextBillingDate =
                        basePeriod.nextBillingDateFromToday(billingDate.toLocalDate)
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