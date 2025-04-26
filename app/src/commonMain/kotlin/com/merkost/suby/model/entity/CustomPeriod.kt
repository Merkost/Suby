package com.merkost.suby.model.entity

import kotlinx.datetime.DateTimeUnit

enum class CustomPeriod(val timeUnit: DateTimeUnit.DateBased) {
    DAYS(DateTimeUnit.DAY), WEEKS(DateTimeUnit.WEEK), MONTHS(DateTimeUnit.MONTH), YEARS(DateTimeUnit.YEAR);

    fun getTitle(count: Int): String {
        return when (this) {
            DAYS -> if (count == 1) "Day" else "Days"
            WEEKS -> if (count == 1) "Week" else "Weeks"
            MONTHS -> if (count == 1) "Month" else "Months"
            YEARS -> if (count == 1) "Year" else "Years"
        }
    }
}
