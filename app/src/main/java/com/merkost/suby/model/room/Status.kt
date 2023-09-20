package com.merkost.suby.model.room

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.merkost.suby.ui.theme.Orange

enum class Status(val icon: ImageVector, val color: Color) {
    ACTIVE(Icons.Outlined.CheckCircleOutline, Color.Green),
    CANCELED(Icons.Outlined.Cancel, Color.Red),
    EXPIRED(Icons.Outlined.DoNotDisturbOn, Color.Orange),
    TRIAL(Icons.Outlined.Timelapse, Color.Yellow);
}
