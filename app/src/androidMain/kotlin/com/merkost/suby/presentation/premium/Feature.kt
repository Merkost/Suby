package com.merkost.suby.presentation.premium

import androidx.compose.ui.graphics.vector.ImageVector

data class Feature(
    val icon: ImageVector,
    val title: String,
    val isComingSoon: Boolean = false
)