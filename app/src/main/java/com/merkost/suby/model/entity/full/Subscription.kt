package com.merkost.suby.model.entity.full

import android.content.Context
import com.merkost.suby.R
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.CustomPeriod
import com.merkost.suby.model.entity.Period
import com.merkost.suby.model.entity.Status
import com.merkost.suby.utils.Constants
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

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

    val periodType: CustomPeriod,
    val periodDuration: Long,

    val status: Status,
    val paymentDate: LocalDateTime,

    val createdDate: LocalDateTime = java.time.LocalDateTime.now().toKotlinLocalDateTime(),
    val durationDays: Long,
    val description: String,
) {
    val periodDays: Long
        get() = when (this.periodType) {
            CustomPeriod.DAYS -> this.periodDuration
            CustomPeriod.WEEKS -> this.periodDuration * 7L
            CustomPeriod.MONTHS -> this.periodDuration * 30L
            CustomPeriod.YEARS -> this.periodDuration * 365L
        }

    val remainingDays: Long
        get() {
            val currentDateMillis = Clock.System.now().toEpochMilliseconds().milliseconds
            val paymentDateMillis = paymentDate.toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds().milliseconds
            val totalRemainingMillis =
                paymentDateMillis + periodDays.days.inWholeMilliseconds.milliseconds - currentDateMillis
            val totalRemainingDays = totalRemainingMillis.inWholeDays
            return if (totalRemainingDays < 0) 0 else totalRemainingDays
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
        return if (this.periodDays != selectedPeriod.days) {
            (price / this.periodDays * selectedPeriod.days)
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
}
