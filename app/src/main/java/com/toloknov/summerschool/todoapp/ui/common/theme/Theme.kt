package com.toloknov.summerschool.todoapp.ui.common.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance

// Paddings
val PADDING_LARGE = 32.dp
val PADDING_BIG = 16.dp
val PADDING_MEDIUM = 8.dp
val PADDING_SMALL = 4.dp


private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    surface = LightSurface,
    surfaceContainer = LightSurfaceContainer,
    primaryContainer = LightFab,
    onPrimaryContainer = White,
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

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    surface = DarkSurface,
    surfaceContainer = DarkSurfaceContainer,
    primaryContainer = DarkFab,
    onPrimaryContainer = White,
)

private val ColorScheme.checkBoxCheckedColor: Color
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            LightAcceptGreen
        } else {
            DarkAcceptGreeen
        }
    }

private val ColorScheme.checkBoxUnCheckedHightImportanceColor: Color
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            LightRejectRed
        } else {
            DarkRejectRed
        }
    }

private val ColorScheme.checkBoxUnCheckedNormalImportanceColor: Color
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            LightUncheckedBox
        } else {
            DarkUncheckedBox
        }
    }


val ColorScheme.importanceCheckBoxTheme: @Composable (ItemImportance) -> CheckboxColors
    @Composable get() = { importance ->
        when (importance) {
            ItemImportance.HIGH -> {
                CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.checkBoxCheckedColor,
                    uncheckedColor = MaterialTheme.colorScheme.checkBoxUnCheckedHightImportanceColor,
                    checkmarkColor = MaterialTheme.colorScheme.surfaceContainer
                )
            }

            else -> {
                CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.checkBoxCheckedColor,
                    uncheckedColor = MaterialTheme.colorScheme.checkBoxUnCheckedNormalImportanceColor,
                    checkmarkColor = MaterialTheme.colorScheme.surfaceContainer
                )
            }
        }
    }

val ColorScheme.plainImportancecheckBoxTheme: CheckboxColors
    @Composable get() =
        if (!isSystemInDarkTheme()) {
            CheckboxDefaults.colors(
                checkedColor = LightAcceptGreen,
                uncheckedColor = Color(0xFFcccccc),
                checkmarkColor = White,
                disabledCheckedColor = MaterialTheme.colorScheme.primary,
                disabledUncheckedColor = MaterialTheme.colorScheme.primary,
                disabledIndeterminateColor = MaterialTheme.colorScheme.primary,
            )
        } else {
            CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.primary,
                disabledCheckedColor = MaterialTheme.colorScheme.primary,
                disabledUncheckedColor = MaterialTheme.colorScheme.primary,
                disabledIndeterminateColor = MaterialTheme.colorScheme.primary,
            )
        }

val ColorScheme.hightImportancecheckBoxTheme: CheckboxColors
    @Composable get() =
        if (!isSystemInDarkTheme()) {
            CheckboxDefaults.colors(
                checkedColor = LightAcceptGreen,
                uncheckedColor = LightRejectRed,
                checkmarkColor = White,
                disabledCheckedColor = MaterialTheme.colorScheme.primary,
                disabledUncheckedColor = MaterialTheme.colorScheme.primary,
                disabledIndeterminateColor = MaterialTheme.colorScheme.primary,
            )
        } else {
            CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.primary,
                disabledCheckedColor = MaterialTheme.colorScheme.primary,
                disabledUncheckedColor = MaterialTheme.colorScheme.primary,
                disabledIndeterminateColor = MaterialTheme.colorScheme.primary,
            )
        }


@Composable
fun ToDoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}