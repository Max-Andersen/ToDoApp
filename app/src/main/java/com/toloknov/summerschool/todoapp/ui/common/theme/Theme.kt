package com.toloknov.summerschool.todoapp.ui.common.theme

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    outline = LightGreyForText,
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
    background = DarkGreyForText,
    onBackground = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color.White,
    outline = DarkGreyForText,
    surfaceContainerLow = DarkSurfaceContainer,
    surfaceContainerLowest = DarkGreyForText
)


val ColorScheme.TodoGreen: Color
    @ReadOnlyComposable
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            LightAcceptGreen
        } else {
            DarkAcceptGreeen
        }
    }

val ColorScheme.TodoRed: Color
    @ReadOnlyComposable
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            LightRejectRed
        } else {
            DarkRejectRed
        }
    }

val ColorScheme.checkBoxUnCheckedNormalImportanceColor: Color
    @ReadOnlyComposable
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


@Preview(showBackground = true)
@Composable
private fun TypographyPreview() {
    ToDoAppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(PADDING_BIG)
        ) {
            Text(text = "Large title — 32/38", style = MaterialTheme.typography.titleLarge)
            Text(text = "Title — 20/32", style = MaterialTheme.typography.titleMedium)
            Text(text = "BUTTON — 14/24", style = MaterialTheme.typography.button)
            Text(text = "Body — 16/20", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=320")
@Composable
private fun ColorThemeLightPreview() {
    ToDoAppTheme {
        val scrollState = rememberScrollState()
        Surface {
            FlowRow(
                modifier = Modifier
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                maxItemsInEachRow = 3,
            ) {
                val itemSmallModifier = Modifier
                    .padding(horizontal = PADDING_MEDIUM, vertical = PADDING_SMALL)
                    .widthIn(0.dp, 128.dp)
                    .weight(1f)

                val itemBigModifier = Modifier
                    .padding(horizontal = PADDING_MEDIUM, vertical = PADDING_SMALL)
                    .fillMaxWidth()

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.primary,
                    "Primary"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onPrimary,
                    "onPrimary"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.primaryContainer,
                    "primaryContainer"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    "onPrimaryContainer"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surface,
                    "surface"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onSurface,
                    "onSurface"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.background,
                    "background"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onBackground,
                    "onBackground"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surfaceContainer,
                    "background"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surfaceVariant,
                    "surfaceVariant"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    "onSurfaceVariant"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.outline,
                    "outline"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surfaceContainerLow,
                    "surfaceContainerLow"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surfaceContainerLowest,
                    "surfaceContainerLowest"
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(
    showBackground = true, device = "spec:width=1080px,height=2340px,dpi=320",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ColorThemeDarkPreview() {
    ToDoAppTheme {
        val scrollState = rememberScrollState()
        Surface {
            FlowRow(
                modifier = Modifier
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                maxItemsInEachRow = 3,
            ) {
                val itemSmallModifier = Modifier
                    .padding(horizontal = PADDING_MEDIUM, vertical = PADDING_SMALL)
                    .widthIn(0.dp, 128.dp)
                    .weight(1f)

                val itemBigModifier = Modifier
                    .padding(horizontal = PADDING_MEDIUM, vertical = PADDING_SMALL)
                    .fillMaxWidth()

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.primary,
                    "Primary"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onPrimary,
                    "onPrimary"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.primaryContainer,
                    "primaryContainer"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    "onPrimaryContainer"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surface,
                    "surface"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onSurface,
                    "onSurface"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.background,
                    "background"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onBackground,
                    "onBackground"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surfaceContainer,
                    "surfaceContainer"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surfaceVariant,
                    "surfaceVariant"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    "onSurfaceVariant"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.outline,
                    "outline"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surfaceContainerLow,
                    "surfaceContainerLow"
                )

                ColoredBoxWithText(
                    itemSmallModifier,
                    MaterialTheme.colorScheme.surfaceContainerLowest,
                    "surfaceContainerLowest"
                )
            }
        }
    }
}

@Composable
private fun ColoredBoxWithText(
    modifier: Modifier = Modifier,
    color: Color,
    text: String
) {
    Box(
        modifier = modifier
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.onBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Green.copy(alpha = 0.7f)
        )
    }
}