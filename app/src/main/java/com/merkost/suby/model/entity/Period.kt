package com.merkost.suby.model.entity

import java.time.temporal.ChronoUnit

enum class Period(
    val periodName: String,
    val days: Long,
    val chronoUnit: ChronoUnit,
    val chronoUnitDuration: Long,
    val description: String
) {
    DAILY("Daily", 1, ChronoUnit.DAYS, 1, "Billed every day."),
    WEEKLY("Weekly", 7, ChronoUnit.WEEKS, 1, "Billed every week."),
    BI_WEEKLY("Bi-Weekly", 14, ChronoUnit.WEEKS, 2, "Billed every two weeks."),
    MONTHLY("Monthly", 30, ChronoUnit.MONTHS, 1, "Billed once a month."),
    QUARTERLY("Quarterly", 90, ChronoUnit.MONTHS, 3, "Billed every three months."),
    SEMI_ANNUAL("Semi-Annually", 180, ChronoUnit.MONTHS, 6, "Billed every six months."),
    ANNUAL("Annually", 365, ChronoUnit.YEARS, 1, "Billed once a year."),
    CUSTOM("Custom", 0, ChronoUnit.DAYS, 0, "Billed for a custom period specified by you.");

    fun nextMain(): Period {
        return when (this) {
            DAILY -> WEEKLY
            WEEKLY -> MONTHLY
            else -> DAILY
        }
    }

}