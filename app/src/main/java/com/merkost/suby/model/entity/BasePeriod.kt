package com.merkost.suby.model.entity

import com.merkost.suby.utils.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

data class BasePeriod(
    val type: CustomPeriod,
    val duration: Long,
) {
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