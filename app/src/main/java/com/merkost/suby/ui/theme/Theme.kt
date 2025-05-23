package com.merkost.suby.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.repository.datastore.AppStateRepository
import org.koin.compose.koinInject

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val LocalAppColors = staticCompositionLocalOf { lightColors }

data class AppState(
    val isFirstTimeLaunch: Boolean = true,
    val hasPremium: Boolean = false,
    val hasSubscriptions: Boolean = true,
    val mainCurrency: Currency = Currency.USD
)

val LocalAppState = compositionLocalOf<AppState> {
    error("No app state provided")
}

val MaterialTheme.subyColors: AppColors
    @Composable
    get() = LocalAppColors.current

@Composable
fun SubyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val appState = if (isPreview) {
        AppState()
    } else {
        val appStateRepository = koinInject<AppStateRepository>()
        appStateRepository.appState.collectAsState().value
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val colors = when {
        darkTheme -> darkColors
        else -> lightColors
    }

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppState provides appState,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

val lightColors = AppColors(
    textPlaceholderColor = Color.LightGray,

    statusOrange = Color(0xFFEF6C00),
    statusGreen = Color(0xFF2E7D32),
    statusRed = Color(0xFFC62828),
    statusYellow = Color(0xFFF9A825),

    todayBorderColor = Color(0xFF6200EE),
    selectedBackgroundColor = Color(0xFF6200EE).copy(alpha = 0.2f)
)

val darkColors = AppColors(
    textPlaceholderColor = Color.Gray,

    statusOrange = Color.StatusOrange,
    statusGreen = Color.StatusGreen,
    statusRed = Color.StatusRed,
    statusYellow = Color.StatusYellow,

    todayBorderColor = Color(0xFFBB86FC),
    selectedBackgroundColor = Color(0xFFBB86FC).copy(alpha = 0.2f)
)

@Stable
data class AppColors(
    val textPlaceholderColor: Color,

    val statusOrange: Color,
    val statusGreen: Color,
    val statusRed: Color,
    val statusYellow: Color,

    val todayBorderColor: Color,
    val selectedBackgroundColor: Color
)