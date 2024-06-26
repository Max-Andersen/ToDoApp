package com.toloknov.summerschool.todoapp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toloknov.summerschool.todoapp.R
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.ui.common.theme.PADDING_BIG
import com.toloknov.summerschool.todoapp.ui.common.theme.PADDING_LARGE
import com.toloknov.summerschool.todoapp.ui.common.theme.PADDING_MEDIUM
import com.toloknov.summerschool.todoapp.ui.common.theme.ToDoAppTheme
import com.toloknov.summerschool.todoapp.ui.common.theme.TodoRed
import com.toloknov.summerschool.todoapp.ui.common.theme.textFieldTheme
import com.toloknov.summerschool.todoapp.ui.common.toolbar.CollapsingTopbar
import com.toloknov.summerschool.todoapp.ui.common.toolbar.rememberToolbarScrollBehavior
import com.toloknov.summerschool.todoapp.ui.common.utils.convertToReadable
import com.toloknov.summerschool.todoapp.ui.common.utils.convertToZonedDateTime
import java.time.ZonedDateTime

@Composable
fun TodoItemCard(
    onBackClick: () -> Unit
) {
    val viewModel: TodoItemCardViewModel = viewModel(factory = TodoItemCardViewModel.Factory)

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TodoItemCardEffect.NavigateBack -> onBackClick()
            }
        }
    }

    TodoItemCardStateless(
        uiState = uiState,
        onBackClick = onBackClick,
        reduce = viewModel::reduce
    )

}

/**
 * Stateless не в плане, что состояния нет, а в том, что это состояние снаружи приходит, а значит можно в Preview использовать
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItemCardStateless(
    uiState: TodoItemCardUiState,
    onBackClick: () -> Unit,
    reduce: (TodoItemCardItent) -> Unit
) {

    val scrollBehavior = rememberToolbarScrollBehavior()

    var firstDateDialogState by remember { mutableStateOf(false) }


    if (firstDateDialogState) {
        DateDialog(
            currPickedDate = uiState.deadline,
            onConfirmButtonClick = { reduce(TodoItemCardItent.SetDeadline(it)) },
            onDismissRequest = {
                firstDateDialogState = false
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingTopbar(
                modifier = Modifier,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    CloseButton(onClick = onBackClick)
                },
                actions = {
                    TextButton(onClick = { reduce(TodoItemCardItent.SaveTodoItem) }) {
                        Text(text = stringResource(id = R.string.save))
                    }
                },
                collapsedElevation = 10.dp,
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = PADDING_BIG)
                .verticalScroll(rememberScrollState())

        ) {
            Spacer(modifier = Modifier.size(PADDING_MEDIUM))
            // Чтобы при каждой рекомпозиции мы этот список снова не собирали
            val importanceItems = remember {
                ItemImportance.values()
            }

            InputTodoText(
                uiState = uiState,
                reduce = reduce
            )
            Spacer(modifier = Modifier.size(PADDING_BIG))

            ImportanceBlock(
                uiState = uiState,
                importanceItems = importanceItems,
                reduce = reduce
            )
            Spacer(modifier = Modifier.size(PADDING_BIG))
            HorizontalDivider()

            Spacer(modifier = Modifier.size(PADDING_BIG))
            SelectDeadline(
                uiState = uiState,
                openDialog = { firstDateDialogState = true },
                reduce = reduce
            )

            Spacer(modifier = Modifier.size(PADDING_BIG))
            HorizontalDivider()
            Spacer(modifier = Modifier.size(PADDING_BIG))


            DeleteSection(uiState, reduce)

            Spacer(modifier = Modifier.size(PADDING_BIG * 2))
        }
    }
}

@Composable
private fun DeleteSection(
    uiState: TodoItemCardUiState,
    reduce: (TodoItemCardItent) -> Unit
) {
    val deleteSectionColor = if (uiState.isNewItem) {
        MaterialTheme.colorScheme.surfaceContainerLowest
    } else {
        MaterialTheme.colorScheme.TodoRed
    }

    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = PADDING_LARGE)
            .clip(RoundedCornerShape(PADDING_MEDIUM))
            .clickable(enabled = !uiState.isNewItem) {
                reduce(TodoItemCardItent.DeleteTodoItem)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = deleteSectionColor
        )
        Text(text = stringResource(id = R.string.delete), color = deleteSectionColor)
    }
}

@Composable
private fun SelectDeadline(
    uiState: TodoItemCardUiState,
    openDialog: () -> Unit,
    reduce: (TodoItemCardItent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.do_before)
        )
        Switch(checked = uiState.deadline != null, onCheckedChange = { newState ->
            if (newState) {
                openDialog()
            } else {
                reduce(TodoItemCardItent.SetDeadline(null))
            }
        })
    }
    if (uiState.deadline != null) {
        Text(
            text = uiState.deadline.convertToReadable() ?: "",
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ImportanceBlock(
    uiState: TodoItemCardUiState,
    importanceItems: Array<ItemImportance>,
    reduce: (TodoItemCardItent) -> Unit
) {
    var dropDownExpanded by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.clickable { dropDownExpanded = true }) {
        Column {
            Text(text = stringResource(id = R.string.importance))
            Text(
                text = uiState.importance.nameRu,
                color = if (uiState.importance == ItemImportance.HIGH) MaterialTheme.colorScheme.TodoRed else Color.Unspecified
            )
        }
        DropdownMenu(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer),
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false }) {
            importanceItems.forEach { item ->
                if (item == ItemImportance.HIGH) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "!! ${item.nameRu}",
                                color = Color.Red
                            )
                        },
                        onClick = {
                            reduce(TodoItemCardItent.SetImportance(item))
                            dropDownExpanded = false
                        })
                } else {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item.nameRu,
                            )
                        },
                        onClick = {
                            reduce(TodoItemCardItent.SetImportance(item))
                            dropDownExpanded = false
                        })
                }

            }
        }
    }
}

@Composable
private fun InputTodoText(
    uiState: TodoItemCardUiState,
    reduce: (TodoItemCardItent) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(PADDING_MEDIUM))
            .defaultMinSize(minHeight = 100.dp),
        value = uiState.text,
        onValueChange = { reduce(TodoItemCardItent.SetText(it)) },
        shape = RoundedCornerShape(PADDING_MEDIUM),
        colors = MaterialTheme.colorScheme.textFieldTheme,
        placeholder = {
            Text(
                text = stringResource(id = R.string.what_need_to_do),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                fontSize = 16.sp
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateDialog(
    currPickedDate: ZonedDateTime?,
    onConfirmButtonClick: (newStartDate: ZonedDateTime) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currPickedDate?.toInstant()?.toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.convertToZonedDateTime()
                    ?.let { onConfirmButtonClick(it) }
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.select))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.surfaceTint)
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        properties = DialogProperties(),
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    modifier = Modifier.padding(start = PADDING_BIG, top = PADDING_BIG),
                    text = stringResource(R.string.date)
                )
            },
        )
    }
}


@Composable
private fun CloseButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Default.Close, contentDescription = null)
    }
}


@Composable
@Preview
private fun TodoItemCardPreview() {
    ToDoAppTheme {

    }
}