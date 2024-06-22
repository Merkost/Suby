package com.merkost.suby.model.entity

import com.merkost.suby.utils.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import timber.log.Timber
import java.time.temporal.ChronoUnit

data class BasePeriod(
    val chronounit: ChronoUnit,
    val duration: Long,
    ) {
        fun nextBillingDate(fromDate: LocalDate): LocalDate {
            val javaFromDate = fromDate.toJavaLocalDate()
            val nextDate = javaFromDate.plus(duration, chronounit)
            return nextDate.toKotlinLocalDate()
        }

        fun nextBillingDateFromToday(fromDate: LocalDate): LocalDate {
            val today = LocalDate.now().toJavaLocalDate()
            var javaNextDate = fromDate.toJavaLocalDate()

            if (javaNextDate.isBefore(today)) {
                val multiplier = chronounit.between(javaNextDate, today) / duration + 1
                javaNextDate = javaNextDate.plus(multiplier * duration, chronounit)
            }

            return javaNextDate.toKotlinLocalDate()
        }
    }