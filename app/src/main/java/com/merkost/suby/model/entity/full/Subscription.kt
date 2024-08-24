package com.merkost.suby.model.entity.full

import android.content.Context
import com.merkost.suby.R
import com.merkost.suby.domain.ui.EditableSubscription
import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Period
import com.merkost.suby.model.entity.Status
import com.merkost.suby.utils.Constants
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
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

    val createdDate: LocalDateTime = java.time.LocalDateTime.now().toKotlinLocalDateTime(),
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

    fun getRemainingDurationString(context: Context): String {
        val formattedDate = paymentDate.toJavaLocalDateTime().format(Constants.dataFormat)

        return when (status) {
            Status.ACTIVE -> when (remainingDays) {
                0L -> context.getString(R.string.today)
                1L -> context.getString(R.string.tomorrow)
                else -> context.getString(R.string.after_days, remainingDays)
            }

            Status.TRIAL -> if (remainingDays > 0) {
                context.getString(R.string.trial_more_days, remainingDays)
            } else {
                context.getString(R.string.trial_ended_on, formattedDate)
            }

            Status.CANCELED -> context.getString(R.string.canceled_on, formattedDate)

            Status.EXPIRED -> context.getString(R.string.expired_on, formattedDate)
        }
    }

    fun getPriceForPeriod(selectedPeriod: Period): Double {
        return if (this.period.duration != selectedPeriod.days) {
            (price / this.period.duration * selectedPeriod.days)
        } else price
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
