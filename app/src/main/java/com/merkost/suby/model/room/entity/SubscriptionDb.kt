package com.merkost.suby.model.room.entity

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.merkost.suby.R
import com.merkost.suby.model.Currency
import com.merkost.suby.model.CustomPeriodType
import com.merkost.suby.model.Period
import com.merkost.suby.model.room.Status
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@Parcelize
@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = ServiceDb::class,
            parentColumns = ["serviceId"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class SubscriptionDb(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serviceId: Int,
    val price: Double,
    val currency: Currency,
    val period: Period,

    val customPeriodType: CustomPeriodType,
    val customPeriodDuration: Long,

    val status: Status,
    val paymentDate: Long,

    val createdDate: Long = System.currentTimeMillis(),
    val durationDays: Long,

    val description: String,
) : Parcelable {

    @IgnoredOnParcel
    val periodDays: Long
        get() = when (this.period) {
            Period.CUSTOM -> when (this.customPeriodType) {
                CustomPeriodType.DAYS -> this.customPeriodDuration
                CustomPeriodType.WEEKS -> this.customPeriodDuration * 7L
                CustomPeriodType.MONTHS -> this.customPeriodDuration * 30L
                CustomPeriodType.YEARS -> this.customPeriodDuration * 365L
            }

            else -> this.period.days
        }

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

    fun getPriceForPeriod(selectedPeriod: Period): Double {
        return if (this.periodDays != selectedPeriod.days) {
            (price / this.periodDays * selectedPeriod.days)
        } else price
    }
}
