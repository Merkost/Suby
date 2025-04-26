package com.merkost.suby.model.entity.full

import androidx.compose.runtime.Composable
import com.merkost.suby.domain.EditableSubscription
import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Period
import com.merkost.suby.model.entity.Status
import com.merkost.suby.utils.now
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import suby.app.generated.resources.Res
import suby.app.generated.resources.after_days
import suby.app.generated.resources.canceled_on
import suby.app.generated.resources.expired_on
import suby.app.generated.resources.today
import suby.app.generated.resources.tomorrow
import suby.app.generated.resources.trial_ended_on
import suby.app.generated.resources.trial_more_days

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

    val createdDate: LocalDateTime = LocalDateTime.now(),
    val description: String,
) {

    val nextPaymentDate: LocalDate
        get() = period.nextBillingDateFromToday(paymentDate.date)

    val remainingDays: Long
        get() {
            val currentDate =
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val nextBilling = period.nextBillingDateFromToday(paymentDate.date)
            return currentDate.daysUntil(nextBilling).toLong()
        }

    @Composable
    fun getRemainingDurationString(): String {
        val formattedDate = paymentDate.toString()
        // FIXME: This is not working for preview
//        .toJavaLocalDateTime().format(Constants.dataFormat)

        return when (status) {
            Status.ACTIVE -> when (remainingDays) {
                0L -> stringResource(Res.string.today)
                1L -> stringResource(Res.string.tomorrow)
                else -> stringResource(Res.string.after_days, remainingDays)
            }

            Status.TRIAL -> if (remainingDays > 0) {
                pluralStringResource(
                    /* id = */ Res.plurals.trial_more_days,
                    /* quantity = */ remainingDays.toInt(),
                    /* ...formatArgs = */ remainingDays
                )
            } else {
                stringResource(Res.string.trial_ended_on, formattedDate)
            }

            Status.CANCELED -> stringResource(Res.string.canceled_on, formattedDate)

            Status.EXPIRED -> stringResource(Res.string.expired_on, formattedDate)
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
        LocalDate(
            year = date.year,
            month = date.month,
            dayOfMonth = date.dayOfMonth
        )
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
