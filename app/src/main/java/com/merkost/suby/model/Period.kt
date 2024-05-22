package com.merkost.suby.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

enum class Period(val periodName: String, val days: Long, val description: String) {
    DAILY("Daily", 1, "Billed every day."),
    WEEKLY("Weekly", 7, "Billed every week."),
    BI_WEEKLY("Bi-Weekly", 14, "Billed every two weeks."),
    MONTHLY("Monthly", 30, "Billed once a month."),
    QUARTERLY("Quarterly", 90, "Billed every three months."),
    SEMI_ANNUAL("Semi-Annually", 180, "Billed every six months."),
    ANNUAL("Annually", 365, "Billed once a year."),
    CUSTOM("Custom", 0, "Billed for a custom period specified by you.");

    fun nextMain(): Period {
        return when (this) {
            DAILY -> WEEKLY
            WEEKLY -> MONTHLY
            else -> DAILY
        }
    }

    fun nextBillingDate(fromDate: LocalDate): LocalDate {
        val fromDate = fromDate.toJavaLocalDate()
        val res = when (this) {
            MONTHLY -> fromDate.plusMonths(1)
            QUARTERLY -> fromDate.plusMonths(3)
            SEMI_ANNUAL -> fromDate.plusMonths(6)
            ANNUAL -> fromDate.plusYears(1)
            else -> fromDate.plusDays(this.days.toLong())
        }
        return res.toKotlinLocalDate()
    }
}