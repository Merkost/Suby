package com.merkost.suby.model

enum class CustomPeriodType {
    DAYS, WEEKS, MONTHS, YEARS;

    fun getTitle(count: Int): String {
        return when(this) {
            DAYS -> if (count == 1) "Day" else "Days"
            WEEKS -> if (count == 1) "Week" else "Weeks"
            MONTHS -> if (count == 1) "Month" else "Months"
            YEARS -> if (count == 1) "Year" else "Years"
        }
    }
}
