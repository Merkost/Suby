package com.merkost.suby.presentation.screens.create

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notes
import androidx.compose.ui.graphics.vector.ImageVector

enum class OptionalField(val title: String, val icon: ImageVector) {
    Description("Description", Icons.Default.Notes),
    PaymentStartDate("Payment Start Date", Icons.Default.CalendarToday)
}