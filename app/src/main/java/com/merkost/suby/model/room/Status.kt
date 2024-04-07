package com.merkost.suby.model.room

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.merkost.suby.ui.theme.StatusGreen
import com.merkost.suby.ui.theme.StatusOrange
import com.merkost.suby.ui.theme.StatusRed
import com.merkost.suby.ui.theme.StatusYellow

enum class Status(
    val icon: ImageVector,
    val color: Color,
    val statusName: String,
    val description: String
) {
    ACTIVE(
        Icons.Outlined.CheckCircleOutline,
        Color.StatusGreen,
        "Active",
        "for subscriptions that are currently ongoing and for which you are regularly being charged. "
    ),
    CANCELED(
        Icons.Outlined.Cancel,
        Color.StatusRed,
        "Canceled",
        "for subscriptions that you have actively discontinued before their expiration date."
    ),
    EXPIRED(
        Icons.Outlined.DoNotDisturbOn,
        Color.StatusOrange,
        "Expired",
        "for subscriptions that have reached their end date and are no longer active, but haven't been formally canceled."
    ),
    TRIAL(
        Icons.Outlined.Timelapse,
        Color.StatusYellow,
        "Trial",
        "for subscriptions that are in their trial period."
    );
}
