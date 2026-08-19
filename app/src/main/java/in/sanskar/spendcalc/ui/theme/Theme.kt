package `in`.sanskar.spendcalc.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import `in`.sanskar.spendcalc.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF5B3FD3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE7DEFF),
    onPrimaryContainer = Color(0xFF1A0063),
    secondary = Color(0xFF5E5C71),
    secondaryContainer = Color(0xFFE4E0F9),
    tertiary = Color(0xFF7A5367),
    background = Color(0xFFFFFBFF),
    surface = Color(0xFFFFFBFF),
    surfaceVariant = Color(0xFFE7E0EC),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFC9B8FF),
    onPrimary = Color(0xFF2C148E),
    primaryContainer = Color(0xFF4328B7),
    onPrimaryContainer = Color(0xFFE7DEFF),
    secondary = Color(0xFFC8C4DC),
    secondaryContainer = Color(0xFF464559),
    tertiary = Color(0xFFEAB8D0),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF49454F),
    error = Color(0xFFFFB4AB),
)

@Composable
fun SpendCalcTheme(
    themeMode: ThemeMode,
    largeText: Boolean,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            WindowCompat.getInsetsController(activity.window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }

    val base = Typography()
    val typography = if (largeText) {
        base.copy(
            bodySmall = base.bodySmall.copy(fontSize = 15.sp, lineHeight = 21.sp),
            bodyMedium = base.bodyMedium.copy(fontSize = 18.sp, lineHeight = 26.sp),
            bodyLarge = base.bodyLarge.copy(fontSize = 20.sp, lineHeight = 29.sp),
            labelLarge = base.labelLarge.copy(fontSize = 16.sp, lineHeight = 22.sp),
            titleMedium = base.titleMedium.copy(fontSize = 21.sp, lineHeight = 28.sp),
            titleLarge = base.titleLarge.copy(fontSize = 26.sp, lineHeight = 34.sp),
        )
    } else {
        base
    }

    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = typography,
        content = content,
    )
}
