package com.merkost.suby.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Stable
data class BannerColors(
    val infoBannerBackground: Color,
    val infoBannerContent: Color,
    val successBannerBackground: Color,
    val successBannerContent: Color,
    val warningBannerBackground: Color,
    val warningBannerContent: Color,
    val errorBannerBackground: Color,
    val errorBannerContent: Color
)

@Stable
data class AppColors(
    val textPlaceholderColor: Color,
    val statusOrange: Color,
    val statusGreen: Color,
    val statusRed: Color,
    val statusYellow: Color,
    val todayBorderColor: Color,
    val selectedBackgroundColor: Color,
    val banner: BannerColors
)

val lightColors = AppColors(
    textPlaceholderColor = Color.LightGray,
    statusOrange = Color(0xFFEF6C00),
    statusGreen = Color(0xFF2E7D32),
    statusRed = Color(0xFFC62828),
    statusYellow = Color(0xFFF9A825),
    todayBorderColor = Color(0xFF6200EE),
    selectedBackgroundColor = Color(0xFF6200EE).copy(alpha = 0.2f),
    banner = BannerColors(
        infoBannerBackground = Color(0xFF6A4C93),
        infoBannerContent = Color(0xFFFFFFFF),
        successBannerBackground = Color(0xFF2E8B57),
        successBannerContent = Color(0xFFFFFFFF),
        warningBannerBackground = Color(0xFFFF8C42),
        warningBannerContent = Color(0xFF1A1A1A),
        errorBannerBackground = Color(0xFFE74C3C),
        errorBannerContent = Color(0xFFFFFFFF)
    )
)

val darkColors = AppColors(
    textPlaceholderColor = Color.Gray,
    statusOrange = Color.StatusOrange,
    statusGreen = Color.StatusGreen,
    statusRed = Color.StatusRed,
    statusYellow = Color.StatusYellow,
    todayBorderColor = Color(0xFFBB86FC),
    selectedBackgroundColor = Color(0xFFBB86FC).copy(alpha = 0.2f),
    banner = BannerColors(
        infoBannerBackground = Color(0xFF8A6BB1),
        infoBannerContent = Color(0xFFFFFFFF),
        successBannerBackground = Color(0xFF4CAF50),
        successBannerContent = Color(0xFFFFFFFF),
        warningBannerBackground = Color(0xFFFFB74D),
        warningBannerContent = Color(0xFF1A1A1A),
        errorBannerBackground = Color(0xFFF44336),
        errorBannerContent = Color(0xFFFFFFFF)
    )
)

val LocalAppColors = staticCompositionLocalOf { lightColors } 