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

enum class Status(val icon: ImageVector, val color: Color, val statusName: String) {
    ACTIVE(Icons.Outlined.CheckCircleOutline, Color.StatusGreen, "Active"),
    CANCELED(Icons.Outlined.Cancel, Color.StatusRed, "Canceled"),
    EXPIRED(Icons.Outlined.DoNotDisturbOn, Color.StatusOrange, "Expired"),
    TRIAL(Icons.Outlined.Timelapse, Color.StatusYellow, "Trial");
}
