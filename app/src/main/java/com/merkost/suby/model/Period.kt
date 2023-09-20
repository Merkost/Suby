package com.merkost.suby.model

enum class Period(val periodName: String, val days: Long) {
    DAILY("Daily",1),
    WEEKLY("Weekly", 7),
    BI_WEEKLY("Bi-Weekly", 14),
    MONTHLY("Monthly", 30),
    QUARTERLY("Quarterly", 90),
    SEMI_ANNUAL("Semi-Annually", 180),
    ANNUAL("Annually", 365),
    CUSTOM("Custom", 0);

    fun nextMain(): Period {
        return when (this) {
            DAILY -> WEEKLY
            WEEKLY -> MONTHLY
            else -> DAILY
        }
    }
}