package com.merkost.suby.model.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.merkost.suby.ui.theme.subyColors

enum class Status(
    val icon: ImageVector,
    val statusName: String,
    val description: String
) {
    ACTIVE(
        Icons.Outlined.CheckCircleOutline,
        "Active",
        "for subscriptions that are currently ongoing and for which you are regularly being charged. "
    ),
    TRIAL(
        Icons.Outlined.Timelapse,
        "Trial",
        "for subscriptions that are in their trial period."
    ),
    EXPIRED(
        Icons.Outlined.DoNotDisturbOn,
        "Expired",
        "for subscriptions that have reached their end date and are no longer active, but haven't been formally canceled."
    ),
    CANCELED(
        Icons.Outlined.Cancel,
        "Canceled",
        "for subscriptions that you have actively discontinued before their expiration date."
    );

    val color: Color
        @Composable
        get() = when (this) {
            ACTIVE -> MaterialTheme.subyColors.statusGreen
            TRIAL -> MaterialTheme.subyColors.statusYellow
            EXPIRED -> MaterialTheme.subyColors.statusOrange
            CANCELED -> MaterialTheme.subyColors.statusRed
        }
}
