package com.toloknov.summerschool.todoapp.ui.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance

val ColorScheme.importanceCheckBoxTheme: @Composable (ItemImportance) -> CheckboxColors
    @Composable get() = { importance ->
        when (importance) {
            ItemImportance.HIGH -> {
                CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.TodoGreen,
                    uncheckedColor = MaterialTheme.colorScheme.TodoRed,
                    checkmarkColor = MaterialTheme.colorScheme.surfaceContainer
                )
            }

            else -> {
                CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.TodoGreen,
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


val ColorScheme.textFieldTheme: TextFieldColors
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
        cursorColor = MaterialTheme.colorScheme.surfaceVariant
    )