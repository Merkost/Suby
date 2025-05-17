package com.merkost.suby.model.entity

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.until

data class BasePeriod(
    val type: CustomPeriod,
    val duration: Long,
    val period: Period
) {
    constructor(type: CustomPeriod, duration: Long) : this(
        type, duration, mapToPeriodEnum(type, duration)
    )

    fun nextBillingDate(fromDate: LocalDate): LocalDate =
        fromDate + type.toDatePeriod(duration.toInt())

    fun nextBillingDateFromToday(fromDate: LocalDate): LocalDate {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        if (fromDate >= today) return fromDate
        require(duration > 0)
        val diff = fromDate.until(today, type.unit)
        val periods = ((diff + duration - 1) / duration).coerceAtLeast(1)
        val total = (duration * periods).toInt()
        return fromDate + type.toDatePeriod(total)
    }

    val approxDays: Long
        get() = when (type) {
            CustomPeriod.DAYS -> duration
            CustomPeriod.WEEKS -> duration * 7
            CustomPeriod.MONTHS -> duration * 30
            CustomPeriod.YEARS -> duration * 365
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