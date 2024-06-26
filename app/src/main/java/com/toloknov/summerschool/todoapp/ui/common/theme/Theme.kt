package com.toloknov.summerschool.todoapp.ui.common.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Paddings
val PADDING_LARGE = 32.dp
val PADDING_BIG = 16.dp
val PADDING_MEDIUM = 8.dp
val PADDING_SMALL = 4.dp


//private val LightColorScheme = lightColorScheme(
//    primary = LightPrimary,
//    secondary = PurpleGrey40,
//    tertiary = Pink40,
//    surface = LightSurface,
//    surfaceContainer = LightSurfaceContainer,
//    primaryContainer = LightPrimary,
//    onPrimaryContainer = White,
//    outline = LightOutline,
//    surfaceVariant = LightSurfaceVariant
//)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = LightPrimary,
    onPrimaryContainer = White,
    surface = LightSurface,
    onSurface = Color.Black,
    background = Color(0xFFF7F6F2),
    surfaceContainer = LightSurfaceContainer,
    onBackground = Color.Black,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color.Black,
    outline = Color(0xFFB3B3B3),
    surfaceContainerLow = LightSurfaceContainer,
    surfaceContainerLowest = LightGreyForText
)

//private val DarkColorScheme = darkColorScheme(
//    primary = DarkPrimary,
//    secondary = PurpleGrey80,
//    tertiary = Pink80,
//
//    surface = DarkSurface,
//    surfaceContainer = DarkSurfaceContainer,
//    primaryContainer = DarkPrimary,
//    onPrimaryContainer = White,
//    outline = DarkOutline,
//    surfaceVariant = DarkSurfaceVariant
//)


private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.Black,
    primaryContainer = DarkPrimary,
    onPrimaryContainer = White,
    surface = DarkSurface,
    surfaceContainer = DarkSurfaceContainer,
    onSurface = Color.White,
    background = Color(0xFF161618),
    onBackground = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color.White,
    outline = Color(0xFF7C7C7E),
    surfaceContainerLow = DarkSurfaceContainer,
    surfaceContainerLowest = DarkGreyForText
)


val ColorScheme.TodoGreen: Color
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            LightAcceptGreen
        } else {
            DarkAcceptGreeen
        }
    }

val ColorScheme.TodoRed: Color
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            LightRejectRed
        } else {
            DarkRejectRed
        }
    }

val ColorScheme.checkBoxUnCheckedNormalImportanceColor: Color
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            LightUncheckedBox
        } else {
            DarkUncheckedBox
        }
    }




// С цветами в макете жесть конечно, не соотносится в палетку Material3, чтобы compose автоматически всё тянул
// подбирал на глаз и эмпирически
@Composable
fun ToDoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


@Preview
@Composable
private fun AppThemePreview() {
    ToDoAppTheme {

    }
}