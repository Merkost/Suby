package com.merkost.suby.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.merkost.suby.SubySmallShape
import com.merkost.suby.ui.theme.LocalAppColors
import com.merkost.suby.utils.now
import kotlinx.datetime.LocalDate

@Composable
internal fun DayCell(day: CalendarDay, isSelected: Boolean, hasSubscriptions: Boolean) {
    val today = LocalDate.now()
    val isFromCurrentMonth = day.position == DayPosition.MonthDate
    val appColors = LocalAppColors.current

    val selectedBackgroundColor = appColors.selectedBackgroundColor
    val todayBorderColor = appColors.todayBorderColor
    val defaultBackgroundColor = Color.Transparent

    val backgroundModifier = if (isSelected) {
        Modifier.background(selectedBackgroundColor, shape = MaterialTheme.shapes.small)
    } else {
        Modifier.background(defaultBackgroundColor)
    }

    val borderModifier = if (day.date == today && !isSelected) {
        Modifier.border(width = 2.dp, color = todayBorderColor, shape = MaterialTheme.shapes.small)
    } else Modifier

    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(4.dp)
            .then(backgroundModifier)
            .then(borderModifier)
            .clip(SubySmallShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = if (day.date == today && !isSelected)
                    MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                else MaterialTheme.typography.bodySmall,
                color = if (!isFromCurrentMonth)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                else if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            if (hasSubscriptions) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(if (day.date == today) 6.dp else 4.dp)
                        .clip(CircleShape)
                        .background(
                            if (!isFromCurrentMonth)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    }
}