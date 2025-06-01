package com.merkost.suby.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
data class SubySpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val huge: Dp = 32.dp,
    val massive: Dp = 48.dp,
    
    val chipTiny: Dp = 36.dp,
    val chipRegular: Dp = 40.dp,
    val iconSmall: Dp = 18.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    
    val bottomSheetPadding: Dp = 16.dp,
    val screenPadding: Dp = 16.dp,
    val cardPadding: Dp = 16.dp,
    
    val dividerThickness: Dp = 1.dp,
    val elevationSmall: Dp = 2.dp,
    val elevationMedium: Dp = 4.dp,
    val elevationLarge: Dp = 8.dp
)

val LocalSubySpacing = staticCompositionLocalOf { SubySpacing() } 