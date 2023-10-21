package com.merkost.suby.model.room.entity

import android.content.Context
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.merkost.suby.R
import com.merkost.suby.model.Currency
import com.merkost.suby.model.Period
import com.merkost.suby.model.Service
import com.merkost.suby.model.room.Status
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@Parcelize
@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded
    val service: Service,
    val price: Double,
    val currency: Currency,
    val period: Period,
    val status: Status,
    val paymentDate: Long,

    val createdDate: Long = System.currentTimeMillis(),
    val durationDays: Long,
) : Parcelable {
    val remainingDays: Long
        get() {
            val currentTimeMillis = System.currentTimeMillis().milliseconds
            val initialRemainingDays =
                (paymentDate.milliseconds + durationDays.days - currentTimeMillis).inWholeDays
            val elapsedPeriods = (initialRemainingDays / durationDays) + 1
            val remainingDays = initialRemainingDays - (elapsedPeriods - 1) * durationDays
            return kotlin.math.abs(remainingDays)
        }

    fun getRemainingDurationString(context: Context): String {
        return when (remainingDays) {
            0L -> context.getString(R.string.today)
            1L -> context.getString(R.string.tomorrow)
            else -> context.getString(R.string.after_days, remainingDays)
        }
    }

    fun getPriceForPeriod(selectedPeriod: Period): String {
        return if (period.days != selectedPeriod.days) {
            "~" + price / period.days * selectedPeriod.days
        } else price.toString()
    }
}
