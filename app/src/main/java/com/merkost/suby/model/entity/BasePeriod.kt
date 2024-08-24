package com.merkost.suby.model.entity

import com.merkost.suby.utils.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

data class BasePeriod(
    val type: CustomPeriod,
    val duration: Long,
    val period: Period
) {

    constructor(type: CustomPeriod, duration: Long) : this(
        type, duration, mapToPeriodEnum(type, duration)
    )

    fun nextBillingDate(fromDate: LocalDate): LocalDate {
        val javaFromDate = fromDate.toJavaLocalDate()
        val nextDate = javaFromDate.plus(duration, type.chronoUnit)
        return nextDate.toKotlinLocalDate()
    }

    fun nextBillingDateFromToday(fromDate: LocalDate): LocalDate {
        val today = LocalDate.now().toJavaLocalDate()
        var javaNextDate = fromDate.toJavaLocalDate()

        if (javaNextDate.isBefore(today)) {
            val multiplier = type.chronoUnit.between(javaNextDate, today) / duration + 1
            javaNextDate = javaNextDate.plus(multiplier * duration, type.chronoUnit)
        }

        return javaNextDate.toKotlinLocalDate()
    }

    fun toApproximateDays(): Long {
        return when (type) {
            CustomPeriod.DAYS -> duration
            CustomPeriod.WEEKS -> duration * 7
            CustomPeriod.MONTHS -> duration * 30
            CustomPeriod.YEARS -> duration * 365
        }
    }
}

/**
 * Maps the custom period to the main period enum.
 */
fun mapToPeriodEnum(periodType: CustomPeriod, periodDuration: Long): Period {
    return when (periodType) {
        CustomPeriod.DAYS -> when (periodDuration) {
            1L -> Period.DAILY
            else -> Period.CUSTOM
        }

        CustomPeriod.WEEKS -> when (periodDuration) {
            1L -> Period.WEEKLY
            2L -> Period.BI_WEEKLY
            else -> Period.CUSTOM
        }

        CustomPeriod.MONTHS -> when (periodDuration) {
            1L -> Period.MONTHLY
            3L -> Period.QUARTERLY
            6L -> Period.SEMI_ANNUAL
            else -> Period.CUSTOM
        }

        CustomPeriod.YEARS -> when (periodDuration) {
            1L -> Period.ANNUAL
            else -> Period.CUSTOM
        }
    }
}