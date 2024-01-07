package com.merkost.suby.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

enum class Period(val periodName: String, val days: Long) {
    DAILY("Daily",1),
    WEEKLY("Weekly", 7),
    BI_WEEKLY("Bi-Weekly", 14),
    MONTHLY("Monthly", 30),
    QUARTERLY("Quarterly", 90),
    SEMI_ANNUAL("Semi-Annually", 180),
    ANNUAL("Annually", 365);
//    CUSTOM("Custom", 0);

    fun nextMain(): Period {
        return when (this) {
            DAILY -> WEEKLY
            WEEKLY -> MONTHLY
            else -> DAILY
        }
    }

    fun nextBillingDate(fromDate: LocalDate): LocalDate {
        val fromDate = fromDate.toJavaLocalDate()
        val res =  when (this) {
            MONTHLY -> fromDate.plusMonths(1)
            QUARTERLY -> fromDate.plusMonths(3)
            SEMI_ANNUAL -> fromDate.plusMonths(6)
            ANNUAL -> fromDate.plusYears(1)
            else -> fromDate.plusDays(this.days.toLong())
        }
        return res.toKotlinLocalDate()
    }
}