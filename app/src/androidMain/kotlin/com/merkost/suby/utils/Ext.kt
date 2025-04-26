package com.merkost.suby.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.Dp

fun WindowInsets.Companion.all(dp: Dp): WindowInsets =
    WindowInsets(
        top = dp,
        bottom = dp,
        left = dp,
        right = dp
    )