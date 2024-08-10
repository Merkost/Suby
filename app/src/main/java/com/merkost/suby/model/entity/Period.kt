package com.merkost.suby.model.entity

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
    CUSTOM("Custom", 0, "Billed for a custom period specified by you.");

    fun nextMain(): Period {
        return when (this) {
            DAILY -> WEEKLY
            WEEKLY -> MONTHLY
            else -> DAILY
        }
    }

}