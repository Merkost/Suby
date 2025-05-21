package com.merkost.suby.model.entity.full

import android.content.Context
import com.merkost.suby.R
import com.merkost.suby.domain.ui.EditableSubscription
import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Period
import com.merkost.suby.model.entity.Status
import com.merkost.suby.utils.Constants
import com.merkost.suby.utils.now
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime

data class Subscription(
    val id: Int,

    val serviceId: Int,
    val serviceName: String,
    val serviceLogoUrl: String?,
    val serviceCreatedAt: LocalDateTime,
    val serviceLastUpdated: LocalDateTime,
    val isCustomService: Boolean,

    val price: Double,
    val currency: Currency,

    val category: Category,

    val period: BasePeriod,

    val status: Status,
    val paymentDate: LocalDateTime,
    val paymentStartDate: LocalDateTime? = null,

    val createdDate: LocalDateTime = LocalDateTime.now(),
    val description: String,
) {

    val nextPaymentDate: LocalDate
        get() = period.nextBillingDateFromToday(paymentDate.date)

    val remainingDays: Long
        get() = LocalDate.now()
            .daysUntil(period.nextBillingDateFromToday(paymentDate.date),).toLong()

    fun getRemainingDurationString(context: Context): String {
        val formattedDate = paymentDate.toJavaLocalDateTime().format(Constants.dataFormat)
        // FIXME: This is not working for preview

        return when (status) {
            Status.ACTIVE -> {
                val days = remainingDays
                when {
                    days <= 0 -> context.getString(R.string.today)
                    days == 1L -> context.getString(R.string.tomorrow)
                    else -> context.getString(R.string.after_days, days)
                }
            }

            Status.TRIAL -> if (remainingDays > 0) {
                context.resources.getQuantityString(
                    /* id = */ R.plurals.trial_more_days,
                    /* quantity = */ remainingDays.toInt(),
                    /* ...formatArgs = */ remainingDays
                )
            } else {
                context.getString(R.string.trial_ended_on, formattedDate)
            }

            Status.CANCELED -> context.getString(R.string.canceled_on, formattedDate)

            Status.EXPIRED -> context.getString(R.string.expired_on, formattedDate)
        }
    }

    fun getPriceForPeriod(selectedPeriod: Period): Double {
        val periodPrice = if (this.period.approxDays != selectedPeriod.approxDays) {
            ((price / this.period.approxDays) * selectedPeriod.approxDays)
        } else price
        return periodPrice
    }

    fun toService(): Service =
        Service(
            id = serviceId,
            name = serviceName,
            logoUrl = serviceLogoUrl,
            createdAt = serviceCreatedAt,
            isCustomService = isCustomService,
            lastUpdated = serviceLastUpdated,
            category = category,
        )

    fun toEditableSubscription() = EditableSubscription(
        id = id,
        name = serviceName,
        description = description,
        price = price,
        currency = currency,
        period = period,
        status = status,
        service = toService(),
        billingDate = paymentDate,
    )
}

fun Subscription.upcomingPayments(count: Int = 3): List<LocalDate> {
    if (status == Status.CANCELED || status == Status.EXPIRED) {
        return emptyList()
    }

    // TODO: Add trial period and price after trial

    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val upcomingDates = mutableListOf<LocalDate>()

    var nextDate = period.nextBillingDateFromToday(paymentDate.date)

    while (upcomingDates.size < count) {
        if (nextDate > currentDate) {
            upcomingDates.add(nextDate)
        }
        nextDate = period.nextBillingDate(nextDate)
    }

    return upcomingDates.map { date ->
        java.time.LocalDate.of(
            date.year,
            date.month.value,
            date.dayOfMonth,
        ).toKotlinLocalDate()
    }
}

fun Subscription.allPaydays(from: LocalDate, to: LocalDate): List<LocalDate> {
    if (status == Status.CANCELED || status == Status.EXPIRED) return emptyList()
    val paydays = mutableListOf<LocalDate>()
    var nextDate = paymentDate.date
    while (nextDate < from) {
        nextDate = period.nextBillingDate(nextDate)
    }
    while (nextDate <= to) {
        paydays.add(nextDate)
        nextDate = period.nextBillingDate(nextDate)
    }
    return paydays
}
