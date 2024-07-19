package com.toloknov.summerschool.todoapp.ui.common.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.toloknov.summerschool.theme.theme.filledTextField
import com.toloknov.summerschool.theme.theme.filledTextFieldWithBlackDisabledColor
import com.toloknov.summerschool.theme.theme.requiredTextFieldWithBlackDisabledColor
import com.toloknov.summerschool.theme.theme.textField
import com.toloknov.summerschool.theme.theme.textFieldWithBlackDisabledColor
import com.toloknov.summerschool.todoapp.R
import com.toloknov.summerschool.todoapp.ui.common.utils.noRippleClickable

@Composable
fun <T> ButtonWithExposedDropDownMenu(
    modifier: Modifier = Modifier,
    listOfParameters: List<T>,
    label: String,
    isHighlighted: Boolean = false,
    selectedParameter: T? = null,
    selectedParameterName: String? = null,
    enabled: Boolean = true,
    onItemClick: (T) -> Unit
) {
    var dropDownExpanded by remember { mutableStateOf(false) }

    val icon =
        if (dropDownExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val currSelected = if (selectedParameter == null) "" else "$selectedParameter"

    val textColors = when {
        enabled && isHighlighted -> MaterialTheme.colorScheme.requiredTextFieldWithBlackDisabledColor

        enabled && currSelected.isNotBlank() -> MaterialTheme.colorScheme.filledTextFieldWithBlackDisabledColor

        enabled && currSelected.isEmpty() -> MaterialTheme.colorScheme.textFieldWithBlackDisabledColor

        !enabled && currSelected.isNotBlank() -> MaterialTheme.colorScheme.filledTextField

        else -> MaterialTheme.colorScheme.textField
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedParameterName ?: currSelected,
            onValueChange = {},
            label = {
                Text(
                    text = label,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
                .noRippleClickable {
                    if (enabled) {
                        dropDownExpanded = !dropDownExpanded
                    }
                },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            },
            colors = textColors,
            shape = Shapes().large,
            enabled = false,
            singleLine = true,
            maxLines = 1
        )

        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                .heightIn(0.dp, 256.dp)
                .background(MaterialTheme.colorScheme.surfaceBright)
        ) {
            listOfParameters.forEach { item ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                dropDownExpanded = false
                                onItemClick(item)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(top = com.toloknov.summerschool.theme.theme.PADDING_MEDIUM, bottom = 7.dp)
                                .weight(1f),
                            text = "$item",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (item == selectedParameter) {
                            Icon(
                                painter = painterResource(
                                    R.drawable.ic_selected_element_in_drop_down_menu
                                ),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (item != listOfParameters.last()) {
                        HorizontalDivider(
                            modifier = Modifier
                                .height(1.dp)
                                .padding(horizontal = com.toloknov.summerschool.theme.theme.PADDING_SMALL),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                    }
                }
            }
        }
    }
}