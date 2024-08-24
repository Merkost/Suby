package com.merkost.suby.model.entity

import com.merkost.suby.utils.Constants.DEFAULT_CUSTOM_PERIOD_DAYS

enum class Period(
    val periodName: String,
    val days: Long,
    val description: String
) {
    DAILY("Daily", 1, "Billed every day."),
    WEEKLY("Weekly", 7, "Billed every week."),
    BI_WEEKLY("Bi-Weekly", 14, "Billed every two weeks."),
    MONTHLY("Monthly", 30, "Billed once a month."),
    QUARTERLY("Quarterly", 90, "Billed every three months."),
    SEMI_ANNUAL("Semi-Annually", 180, "Billed every six months."),
    ANNUAL("Annually", 365, "Billed once a year."),
    CUSTOM("Custom", DEFAULT_CUSTOM_PERIOD_DAYS, "Billed for a custom period specified by you.");

    fun nextMain(): Period {
        return when (this) {
            DAILY -> WEEKLY
            WEEKLY -> MONTHLY
            else -> DAILY
        }
    }

    fun toBasePeriod(): BasePeriod {
        return when (this) {
            DAILY -> BasePeriod(CustomPeriod.DAYS, 1)
            WEEKLY -> BasePeriod(CustomPeriod.WEEKS, 1)
            BI_WEEKLY -> BasePeriod(CustomPeriod.WEEKS, 2)
            MONTHLY -> BasePeriod(CustomPeriod.MONTHS, 1)
            QUARTERLY -> BasePeriod(CustomPeriod.MONTHS, 3)
            SEMI_ANNUAL -> BasePeriod(CustomPeriod.MONTHS, 6)
            ANNUAL -> BasePeriod(CustomPeriod.YEARS, 1)
            CUSTOM -> BasePeriod(CustomPeriod.DAYS, DEFAULT_CUSTOM_PERIOD_DAYS)
        }
    }

}