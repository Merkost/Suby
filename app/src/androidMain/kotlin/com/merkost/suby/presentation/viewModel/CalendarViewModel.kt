package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.entity.full.allPaydays
import com.merkost.suby.repository.room.SubscriptionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class CalendarViewModel(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val validStatuses = setOf(Status.ACTIVE, Status.TRIAL)

    val subscriptionsByPaymentDate: StateFlow<Map<LocalDate, List<Subscription>>> =
        subscriptionRepository.subscriptions
            .map { subscriptions ->
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val fromDate = today.minus(365, DateTimeUnit.DAY)
                val toDate = today.plus(365, DateTimeUnit.DAY)
                subscriptions.filter { it.status in validStatuses }
                    .flatMap { subscription ->
                        subscription.allPaydays(fromDate, toDate)
                            .map { payday -> payday to subscription }
                    }
                    .groupBy({ it.first }, { it.second })
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val paymentDates: StateFlow<List<LocalDate>> =
        subscriptionsByPaymentDate
            .map { grouped -> grouped.keys.sorted() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}