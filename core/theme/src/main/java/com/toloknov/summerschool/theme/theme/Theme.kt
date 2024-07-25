package com.toloknov.summerschool.theme.theme

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
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
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

private const val DISABLED_ALPHA = 0.8f

// Paddings
val PADDING_LARGE = 32.dp
val PADDING_BIG = 16.dp
val PADDING_MEDIUM = 8.dp
val PADDING_SMALL = 4.dp

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = LightPrimary,
    onPrimaryContainer = White,
    surface = LightGreyForText,
    onSurface = Color.Black,
    background = LightBackground,
    surfaceContainer = LightSurfaceContainer,
    onBackground = Color.Black,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color.Black,
    outline = LightGreyForText,
    surfaceContainerLow = LightSurfaceContainer,
    surfaceContainerLowest = LightGreyForText
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.Black,
    primaryContainer = DarkPrimary,
    onPrimaryContainer = White,
    surface = DarkGreyForText,
    surfaceContainer = DarkSurfaceContainer,
    onSurface = Color.White,
    background = DarkBackground,
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
            com.toloknov.summerschool.theme.theme.DarkAcceptGreeen
        }
    }

val ColorScheme.TodoRed: Color
    @ReadOnlyComposable
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            com.toloknov.summerschool.theme.theme.LightRejectRed
        } else {
            com.toloknov.summerschool.theme.theme.DarkRejectRed
        }
    }

val ColorScheme.checkBoxUnCheckedNormalImportanceColor: Color
    @ReadOnlyComposable
    @Composable
    get() {
        return if (!isSystemInDarkTheme()) {
            com.toloknov.summerschool.theme.theme.LightUncheckedBox
        } else {
            com.toloknov.summerschool.theme.theme.DarkUncheckedBox
        }
    }

val ColorScheme.textButton: ButtonColors
    @Composable get() = ButtonDefaults.buttonColors(
        containerColor = surfaceVariant,
        contentColor = onBackground,
        disabledContainerColor = surfaceVariant.copy(DISABLED_ALPHA),
        disabledContentColor = onBackground.copy(DISABLED_ALPHA)
    )

val ColorScheme.textField: TextFieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = background,
        focusedTextColor = onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
        focusedLabelColor = MaterialTheme.colorScheme.surfaceTint,
        focusedLeadingIconColor = MaterialTheme.colorScheme.surfaceTint,
        focusedTrailingIconColor = MaterialTheme.colorScheme.surfaceTint,

        disabledTextColor = MaterialTheme.colorScheme.surfaceTint,
        disabledBorderColor = Color.Unspecified,
        disabledTrailingIconColor = MaterialTheme.colorScheme.outline,
        disabledLabelColor = MaterialTheme.colorScheme.surfaceTint,

        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceTint.copy(0.4f),
        unfocusedLabelColor = MaterialTheme.colorScheme.surfaceTint,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.surfaceTint,

        errorBorderColor = error,
        errorTrailingIconColor = error,
        errorLabelColor = error,

        cursorColor = onBackground,
    )

val ColorScheme.filledTextField: TextFieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = background,
        focusedTextColor = onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
        focusedLabelColor = MaterialTheme.colorScheme.surfaceTint,
        focusedLeadingIconColor = MaterialTheme.colorScheme.surfaceTint,
        focusedTrailingIconColor = MaterialTheme.colorScheme.surfaceTint,

        disabledTextColor = MaterialTheme.colorScheme.surfaceTint,
        disabledBorderColor = MaterialTheme.colorScheme.surfaceTint,
        disabledTrailingIconColor = MaterialTheme.colorScheme.outline,
        disabledLabelColor = MaterialTheme.colorScheme.surfaceTint,

        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
        unfocusedLabelColor = MaterialTheme.colorScheme.surfaceTint,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.surfaceTint,

        errorBorderColor = error,
        errorTrailingIconColor = error,
        errorLabelColor = error,

        cursorColor = onBackground,
    )

val ColorScheme.textFieldWithBlackDisabledColor: TextFieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = background,
        focusedTextColor = onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
        focusedLabelColor = MaterialTheme.colorScheme.surfaceTint,
        focusedLeadingIconColor = MaterialTheme.colorScheme.surfaceTint,
        focusedTrailingIconColor = MaterialTheme.colorScheme.surfaceTint,

        disabledTextColor = onBackground,
        disabledBorderColor = Color.Unspecified,
        disabledTrailingIconColor = MaterialTheme.colorScheme.outline,
        disabledLabelColor = MaterialTheme.colorScheme.surfaceTint,

        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceTint.copy(0.4f),
        unfocusedLabelColor = MaterialTheme.colorScheme.surfaceTint,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.surfaceTint,

        errorBorderColor = error,
        errorTrailingIconColor = error,
        errorLabelColor = error,

        cursorColor = onBackground,

        selectionColors = TextSelectionColors(
            handleColor = primary,
            backgroundColor = primary.copy(alpha = 0.4f)
        )
    )
val ColorScheme.requiredTextFieldWithBlackDisabledColor: TextFieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = background,
        focusedTextColor = onSurface,
        focusedBorderColor = secondary,
        focusedLabelColor = secondary,
        focusedLeadingIconColor = secondary,
        focusedTrailingIconColor = secondary,

        disabledTextColor = onBackground,
        disabledBorderColor = secondary,
        disabledTrailingIconColor = secondary,
        disabledLabelColor = secondary,

        unfocusedBorderColor = secondary,
        unfocusedLabelColor = secondary,

        errorBorderColor = error,
        errorTrailingIconColor = error,
        errorLabelColor = error,

        cursorColor = onBackground,

        selectionColors = TextSelectionColors(
            handleColor = primary,
            backgroundColor = primary.copy(alpha = 0.4f)
        )
    )

val ColorScheme.filledTextFieldWithBlackDisabledColor: TextFieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = background,
        focusedTextColor = onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
        focusedLabelColor = MaterialTheme.colorScheme.surfaceTint,
        focusedLeadingIconColor = MaterialTheme.colorScheme.surfaceTint,
        focusedTrailingIconColor = MaterialTheme.colorScheme.surfaceTint,

        disabledTextColor = onBackground,
        disabledBorderColor = MaterialTheme.colorScheme.surfaceTint,
        disabledTrailingIconColor = MaterialTheme.colorScheme.outline,
        disabledLabelColor = MaterialTheme.colorScheme.surfaceTint,

        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
        unfocusedLabelColor = MaterialTheme.colorScheme.surfaceTint,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.surfaceTint,

        errorBorderColor = error,
        errorTrailingIconColor = error,
        errorLabelColor = error,

        cursorColor = MaterialTheme.colorScheme.onBackground,

        selectionColors = TextSelectionColors(
            handleColor = primary,
            backgroundColor = primary.copy(alpha = 0.4f)
        )
    )


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
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
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
@Preview(
    name = "light",
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=320"
)
@Preview(
    name = "dark",
    showBackground = true, device = "spec:width=1080px,height=2340px,dpi=320",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ColorThemePreview() {
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
            color = Color.Green.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}