package com.merkost.suby.model.entity

import kotlinx.datetime.DateTimeUnit

enum class CustomPeriod(val unit: DateTimeUnit.DateBased) {
    DAYS(DateTimeUnit.DAY), WEEKS(DateTimeUnit.WEEK), MONTHS(DateTimeUnit.MONTH), YEARS(DateTimeUnit.YEAR);

    fun getTitle(count: Int): String {
        return when (this) {
            DAYS -> if (count == 1) "Day" else "Days"
            WEEKS -> if (count == 1) "Week" else "Weeks"
            MONTHS -> if (count == 1) "Month" else "Months"
            YEARS -> if (count == 1) "Year" else "Years"
        }
    }

    fun toDatePeriod(count: Int): kotlinx.datetime.DatePeriod {
        return when (this) {
            DAYS -> kotlinx.datetime.DatePeriod(days = count)
            WEEKS -> kotlinx.datetime.DatePeriod(days = count * 7)
            MONTHS -> kotlinx.datetime.DatePeriod(months = count)
            YEARS -> kotlinx.datetime.DatePeriod(years = count)
        }
    }
}
