package com.merkost.suby.model

enum class Period(val periodName: String, val days: Long) {
    DAILY("Daily",1),
    WEEKLY("Weekly", 7),
    BI_WEEKLY("Bi-Weekly", 14),
    MONTHLY("Monthly", 30),
    QUARTERLY("Quarterly", 900),
    SEMI_ANNUAL("Semi-Annual", 180),
    ANNUAL("Annual", 365),
    CUSTOM("Custom", 0)
}