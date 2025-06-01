package com.merkost.suby.model.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.merkost.suby.ui.theme.SubyTheme

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
            ACTIVE -> SubyTheme.colors.statusGreen
            EXPIRED -> SubyTheme.colors.statusOrange
            CANCELED -> SubyTheme.colors.statusRed
        }
}
