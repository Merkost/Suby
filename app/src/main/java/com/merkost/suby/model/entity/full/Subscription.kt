package com.merkost.suby.model.entity.full

import android.content.Context
import com.merkost.suby.R
import com.merkost.suby.model.Currency
import com.merkost.suby.model.CustomPeriodType
import com.merkost.suby.model.Period
import com.merkost.suby.model.Status
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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

    val period: Period,
    val customPeriodType: CustomPeriodType,
    val customPeriodDuration: Long,

    val status: Status,
    val paymentDate: LocalDateTime,

    val createdDate: LocalDateTime = java.time.LocalDateTime.now().toKotlinLocalDateTime(),
    val durationDays: Long,
    val description: String,
) {
    val periodDays: Long
        get() = when (this.period) {
            Period.CUSTOM -> when (this.customPeriodType) {
                CustomPeriodType.DAYS -> this.customPeriodDuration
                CustomPeriodType.WEEKS -> this.customPeriodDuration * 7L
                CustomPeriodType.MONTHS -> this.customPeriodDuration * 30L
                CustomPeriodType.YEARS -> this.customPeriodDuration * 365L
            }

            else -> this.period.days
        }

    private val remainingDays: Long
        get() {
            val currentTimeMillis = System.currentTimeMillis().milliseconds
            val initialRemainingDays =
                (paymentDate.toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds().milliseconds + durationDays.days - currentTimeMillis).inWholeDays
            val elapsedPeriods = (initialRemainingDays / durationDays) + 1
            val remainingDays = initialRemainingDays - (elapsedPeriods - 1) * durationDays
            return kotlin.math.abs(remainingDays)
        }

    fun getRemainingDurationString(context: Context): String {
        return when (remainingDays) {
            0L -> context.getString(R.string.today)
            1L -> context.getString(R.string.tomorrow)
            else -> context.getString(R.string.after_days, remainingDays)
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
