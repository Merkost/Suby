package com.merkost.suby.presentation.screens.calendar


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import com.merkost.suby.SubySmallShape
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.components.subscription.HorizontalSubscriptionItem
import com.merkost.suby.presentation.viewModel.CalendarViewModel
import com.merkost.suby.utils.Constants
import com.merkost.suby.utils.anim.AnimatedVisibilityCrossfade
import com.merkost.suby.utils.now
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun CalendarViewScreen(
    onSubscriptionClick: (Subscription) -> Unit,
    upPress: () -> Unit,
) {
    val viewModel = koinViewModel<CalendarViewModel>()
    val subscriptionsByDay by viewModel.subscriptionsByPaymentDate.collectAsState()
    CalendarViewContent(
        subscriptionsByDay = subscriptionsByDay,
        onSubscriptionClick = onSubscriptionClick,
        upPress = upPress
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarViewContent(
    subscriptionsByDay: Map<LocalDate, List<Subscription>>,
    onSubscriptionClick: (Subscription) -> Unit,
    upPress: () -> Unit,
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = currentMonth.minusMonths(12)
    val endMonth = currentMonth.plusMonths(12)
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth
    )
    val selectedDate = remember { mutableStateOf<LocalDate?>(null) }
    val filteredSubscriptions =
        remember(subscriptionsByDay, selectedDate.value, calendarState.firstVisibleMonth) {
            if (selectedDate.value != null) {
                val day = selectedDate.value!!
                mapOf(day to (subscriptionsByDay[day] ?: emptyList()))
            } else {
                subscriptionsByDay.filterKeys { key ->
                    key.year == calendarState.firstVisibleMonth.yearMonth.year &&
                            key.monthNumber == calendarState.firstVisibleMonth.yearMonth.monthNumber
                }
            }
        }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = { CalendarTitleRow(calendarState = calendarState, upPress = upPress) },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = SubySmallShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                HorizontalCalendar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .padding(16.dp),
                    state = calendarState,
                    dayContent = { day ->
                        DayCell(
                            day = day,
                            isSelected = selectedDate.value == day.date,
                            isToday = day.date == LocalDate.now(),
                            hasSubscriptions = subscriptionsByDay.containsKey(day.date),
                            onDayClick = { clickedDate ->
                                selectedDate.value =
                                    if (selectedDate.value == clickedDate) null else clickedDate
                            }
                        )
                    }
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                if (filteredSubscriptions.isEmpty()) {
                    item {
                        PlaceholderView(message = "No subscriptions for the selected month.")
                    }
                } else {
                    val sortedDates = filteredSubscriptions.keys.sorted()
                    sortedDates.forEach { date ->
                        val subs = filteredSubscriptions[date] ?: emptyList()
                        if (subs.isNotEmpty()) {
                            stickyHeader(key = date.toString()) { DateHeader(date = date) }
                            items(subs, key = { it.id }) { subscription ->
                                HorizontalSubscriptionItem(
                                    subscription = subscription,
                                    onClick = { onSubscriptionClick(subscription) },
                                    shouldShowDurationLeft = false,
                                    modifier = Modifier
                                        .animateItem()
                                        .padding(bottom = 8.dp)
                                )
                            }
                        } else {
                            item {
                                PlaceholderView(message = "No subscriptions for $date.")
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(Constants.LAZY_PADDING)) }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    isToday: Boolean,
    hasSubscriptions: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val borderModifier = if (isToday) Modifier.border(
        1.dp,
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        MaterialTheme.shapes.small
    ) else Modifier
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(40.dp)
            .clip(MaterialTheme.shapes.small)
            .then(borderModifier)
            .background(backgroundColor)
            .clickable { onDayClick(day.date) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            if (hasSubscriptions) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun PlaceholderView(message: String) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Canvas(modifier = Modifier.size(150.dp)) {
            val startX = 20.dp.toPx()
            val endX = size.width - 20.dp.toPx()
            val centerY = size.height / 2
            val stroke = 4.dp.toPx()
            drawLine(
                color = primaryColor.copy(alpha = 0.2f),
                start = Offset(startX, centerY),
                end = Offset(endX, centerY),
                strokeWidth = stroke
            )
            drawCircle(
                color = primaryColor,
                radius = 6.dp.toPx(),
                center = Offset(startX, centerY)
            )
            drawCircle(
                color = primaryColor,
                radius = 6.dp.toPx(),
                center = Offset(endX, centerY)
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTitleRow(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    upPress: () -> Unit,
) {
    val isCurrentMonth by remember { derivedStateOf { calendarState.firstVisibleMonth.yearMonth == YearMonth.now() } }
    val coroutineScope = rememberCoroutineScope()
    SubyTopAppBar(
        modifier = modifier,
        upPress = upPress,
        title = {
            Text(
                text = "${
                    calendarState.firstVisibleMonth.yearMonth.month.name.lowercase(Locale.getDefault())
                        .replaceFirstChar { it.uppercase() }
                } ${calendarState.firstVisibleMonth.yearMonth.year}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        },
        actions = {
            AnimatedVisibilityCrossfade(isCurrentMonth.not()) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        calendarState.animateScrollToMonth(YearMonth.now())
                    }
                }) {
                    Icon(Icons.AutoMirrored.Rounded.Undo)
                }
            }
        }
    )
}