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
    val isTrial: Boolean = false,
    val billingDate: Long? = null,
    val paymentStartDate: Long? = null,
    val description: String = "",
) {
    val billingDateInfo: String?
        get() {
            val bdLong = billingDate ?: return null
            val per = period ?: return null
            val st = status ?: return null

            val today: LocalDate = LocalDate.now()
            val billingLocal: LocalDate = bdLong.toLocalDate
            val endDate: LocalDate = per.nextBillingDate(billingLocal)

            val isPassed: Boolean = endDate <= today
            val endDateStr: String = endDate.toJavaLocalDate().dateString()

            return when {
                isTrial ->
                    if (isPassed) {
                        "Trial ended on $endDateStr"
                    } else {
                        "Trial ends on $endDateStr"
                    }
                
                st == Status.ACTIVE ->
                    if (isPassed) {
                        val nextFromToday: String = per
                            .nextBillingDateFromToday(billingLocal)
                            .toJavaLocalDate()
                            .dateString()
                        "Subscription renews on $nextFromToday"
                    } else {
                        "Subscription renews on $endDateStr"
                    }

                st == Status.CANCELED ->
                    if (isPassed) {
                        "Subscription was canceled on $endDateStr"
                    } else {
                        "Subscription will be canceled on $endDateStr"
                    }

                st == Status.EXPIRED ->
                    if (isPassed) {
                        "Subscription expired on $endDateStr"
                    } else {
                        "Subscription expires on $endDateStr"
                    }
                else -> null
            }
        }
}