package dkmak.atlunch.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF80DB89),
        onPrimary = Color(0xFF00390C),
        primaryContainer = Color(0xFF005315),
        onPrimaryContainer = Color(0xFFA0F8A4),
        inversePrimary = AppSecondary,
        secondary = Color(0xFFB3D1B0),
        onSecondary = Color(0xFF203620),
        secondaryContainer = Color(0xFF374D35),
        onSecondaryContainer = Color(0xFFCFEECB),
        tertiary = AppTertiary,
        onTertiary = Color(0xFF003910),
        tertiaryContainer = Color(0xFF00531A),
        onTertiaryContainer = Color(0xFF9CF7A6),
        background = Color(0xFF111411),
        onBackground = Color(0xFFE1E4DD),
        surface = Color(0xFF111411),
        onSurface = Color(0xFFE1E4DD),
        surfaceVariant = Color(0xFF41493F),
        onSurfaceVariant = Color(0xFFC1C9BC),
        surfaceTint = Color(0xFF80DB89),
        inverseSurface = Color(0xFFE1E4DD),
        inverseOnSurface = Color(0xFF2E322C),
        outline = Color(0xFF8B9387),
        outlineVariant = Color(0xFF41493F),
        scrim = Color.Black,
        surfaceBright = Color(0xFF363A34),
        surfaceDim = Color(0xFF111411),
        surfaceContainerLowest = Color(0xFF0B0F0B),
        surfaceContainerLow = Color(0xFF191D18),
        surfaceContainer = Color(0xFF1D211C),
        surfaceContainerHigh = Color(0xFF272B26),
        surfaceContainerHighest = Color(0xFF323630),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
    )

private val LightColorScheme =
    lightColorScheme(
        primary = AppSecondary,
        secondary = AppSecondary,
        tertiary = AppTertiary,
        background = AppBackground,
        surface = AppSurface,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFB2F2B8),
        onPrimaryContainer = Color(0xFF002106),
        inversePrimary = Color(0xFF80DB89),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD2EDCD),
        onSecondaryContainer = Color(0xFF10200F),
        onTertiary = Color(0xFF002109),
        tertiaryContainer = Color(0xFFAEF4B7),
        onTertiaryContainer = Color(0xFF002109),
        onBackground = Color(0xFF191D18),
        onSurface = Color(0xFF191D18),
        surfaceVariant = Color(0xFFDEE5D8),
        onSurfaceVariant = Color(0xFF41493F),
        surfaceTint = AppSecondary,
        inverseSurface = Color(0xFF2E322C),
        inverseOnSurface = Color(0xFFF0F2EB),
        outline = Color(0xFF71796D),
        outlineVariant = Color(0xFFC1C9BC),
        scrim = Color.Black,
        surfaceBright = AppSurface,
        surfaceDim = Color(0xFFD9DDD5),
        surfaceContainerLowest = AppSurface,
        surfaceContainerLow = Color(0xFFF5F7F1),
        surfaceContainer = AppBackground,
        surfaceContainerHigh = Color(0xFFE8EBE3),
        surfaceContainerHighest = Color(0xFFE2E5DE),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
    )

@Composable
fun AtLunchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
