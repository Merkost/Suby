package com.merkost.suby.model.room.entity

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.merkost.suby.R
import com.merkost.suby.model.Category
import com.merkost.suby.model.Currency
import com.merkost.suby.model.Service
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@Parcelize
@Entity(tableName = "subscriptions")
data class Subscription(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val service: Service,
    val category: Category,
    val price: Double,
    val currency: Currency,
    val durationDays: Long,
    // TODO: add period and custom period
    val paymentDate: Long,

    val createdDate: Long = System.currentTimeMillis(),

    ) : Parcelable {
    val remainingDays: Long
        get() {
            val currentTimeMillis = System.currentTimeMillis().milliseconds
            val initialRemainingDays = (paymentDate.milliseconds + durationDays.days - currentTimeMillis).inWholeDays
            val elapsedPeriods = (initialRemainingDays / durationDays) + 1
            val remainingDays = initialRemainingDays - (elapsedPeriods - 1) * durationDays
            return kotlin.math.abs(remainingDays)
        }

    fun getRemainingDurationString(context: Context): String {
            return when(remainingDays) {
                0L -> context.getString(R.string.today)
                1L -> context.getString(R.string.tomorrow)
                else -> context.getString(R.string.after_days, remainingDays)
            }
        }
}
