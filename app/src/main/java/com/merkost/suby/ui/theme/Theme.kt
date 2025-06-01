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
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
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
    tertiary = Pink40
)

data class AppState(
    val isFirstTimeLaunch: Boolean = true,
    val hasPremium: Boolean = false,
    val hasSubscriptions: Boolean = true,
    val mainCurrency: Currency = Currency.USD
)

val LocalAppState = compositionLocalOf<AppState> {
    error("No app state provided")
}

object SubyTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val spacing: SubySpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSubySpacing.current

    val appState: AppState
        @Composable
        @ReadOnlyComposable
        get() = LocalAppState.current
}

val MaterialTheme.subyColors: AppColors
    @Composable
    get() = SubyTheme.colors

val MaterialTheme.subySpacing: SubySpacing
    @Composable
    get() = SubyTheme.spacing

@Composable
fun SubyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    val spacing = SubySpacing()

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalSubySpacing provides spacing,
        LocalAppState provides appState,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
} 