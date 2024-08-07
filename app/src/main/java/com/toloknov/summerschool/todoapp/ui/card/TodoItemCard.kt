package com.toloknov.summerschool.todoapp.ui.card

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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toloknov.summerschool.domain.model.ItemImportance
import com.toloknov.summerschool.theme.theme.PADDING_BIG
import com.toloknov.summerschool.theme.theme.ToDoAppTheme
import com.toloknov.summerschool.theme.theme.TodoRed
import com.toloknov.summerschool.theme.theme.textFieldTheme
import com.toloknov.summerschool.todoapp.R
import com.toloknov.summerschool.todoapp.ui.common.bottomsheet.ImportanceBottomSheetList
import com.toloknov.summerschool.todoapp.ui.common.snackbar.SnackbarError
import com.toloknov.summerschool.todoapp.ui.common.toolbar.CollapsingTopbar
import com.toloknov.summerschool.todoapp.ui.common.toolbar.rememberToolbarScrollBehavior
import com.toloknov.summerschool.todoapp.ui.common.utils.convertToReadable
import com.toloknov.summerschool.todoapp.ui.common.utils.convertToZonedDateTime
import java.time.ZonedDateTime

@Composable
fun TodoItemCard(
    onBackClick: () -> Unit,
    viewModel: TodoItemCardViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TodoItemCardEffect.NavigateBack -> onBackClick()
                is TodoItemCardEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    TodoItemCardStateless(
        isLoading = uiState.isLoading,
        uiState = uiState,
        onBackClick = onBackClick,
        reduce = viewModel::reduce,
        snackbarHostState = snackbarHostState
    )

}

/**
 * Stateless не в плане, что состояния нет, а в том, что это состояние снаружи приходит, а значит можно в Preview использовать
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItemCardStateless(
    isLoading: Boolean,
    uiState: TodoItemCardUiState,
    onBackClick: () -> Unit,
    reduce: (TodoItemCardIntent) -> Unit,
    snackbarHostState: SnackbarHostState = SnackbarHostState()
) {
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scrollBehavior = rememberToolbarScrollBehavior()

    var firstDateDialogState by remember { mutableStateOf(false) }

    if (firstDateDialogState) {
        DateDialog(currPickedDate = uiState.deadline,
            onConfirmButtonClick = { reduce(TodoItemCardIntent.SetDeadline(it)) },
            onDismissRequest = {
                firstDateDialogState = false
            })
    }

    Scaffold(modifier = Modifier
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
                    TextButton(onClick = { reduce(TodoItemCardIntent.SaveTodoItem) }) {
                        Text(
                            text = stringResource(id = R.string.save),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                collapsedElevation = 10.dp,
            )
        },
        snackbarHost = {
            Box(modifier = Modifier.safeDrawingPadding()) {
                SnackbarHost(hostState = snackbarHostState,
                    snackbar = { snackbarData: SnackbarData ->
                        SnackbarError(text = snackbarData.visuals.message,
                            onClick = { snackbarHostState.currentSnackbarData?.dismiss() })
                    })
            }
        }) { paddingValues ->


        if (showBottomSheet) {
            ImportanceBottomSheetList(
                paddingValues = paddingValues,
                sheetState = bottomSheetState,
                scope = scope,
                onItemClick = { reduce(TodoItemCardIntent.SetImportance(it)) },
                onDismissRequest = { showBottomSheet = false }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentAlignment = Alignment.Center
        ) {

            if (isLoading) {
                CircularProgressIndicator()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PADDING_BIG)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.size(com.toloknov.summerschool.theme.theme.PADDING_MEDIUM))

                InputTodoText(
                    uiState = uiState, reduce = reduce
                )
                Spacer(modifier = Modifier.size(PADDING_BIG))

                ImportanceBlock(
                    uiState = uiState,
                    openBottomSheet = { showBottomSheet = true }
                )
                Spacer(modifier = Modifier.size(PADDING_BIG))
                HorizontalDivider()

                Spacer(modifier = Modifier.size(PADDING_BIG))
                SelectDeadline(
                    uiState = uiState, openDialog = { firstDateDialogState = true }, reduce = reduce
                )

                Spacer(modifier = Modifier.size(PADDING_BIG))
                HorizontalDivider()
                Spacer(modifier = Modifier.size(PADDING_BIG))


                DeleteSection(uiState, reduce)

                Spacer(modifier = Modifier.size(PADDING_BIG * 2))
            }
        }
    }
}

@Composable
private fun DeleteSection(
    uiState: TodoItemCardUiState, reduce: (TodoItemCardIntent) -> Unit
) {
    val deleteSectionColor = if (uiState.isNewItem) {
        MaterialTheme.colorScheme.surfaceContainerLowest
    } else {
        MaterialTheme.colorScheme.TodoRed
    }

    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = com.toloknov.summerschool.theme.theme.PADDING_LARGE)
            .clip(RoundedCornerShape(com.toloknov.summerschool.theme.theme.PADDING_MEDIUM))
            .clickable(enabled = !uiState.isNewItem) { reduce(TodoItemCardIntent.DeleteTodoItem) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Delete, contentDescription = null, tint = deleteSectionColor
        )
        Text(
            text = stringResource(id = R.string.delete), color = deleteSectionColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SelectDeadline(
    uiState: TodoItemCardUiState, openDialog: () -> Unit, reduce: (TodoItemCardIntent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.do_before),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(checked = uiState.deadline != null, onCheckedChange = { newState ->
            if (newState) {
                openDialog()
            } else {
                reduce(TodoItemCardIntent.SetDeadline(null))
            }
        })
    }
    if (uiState.deadline != null) {
        Text(
            text = uiState.deadline.convertToReadable() ?: "",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ImportanceBlock(
    uiState: TodoItemCardUiState,
    openBottomSheet: () -> Unit,
) {
    Box(modifier = Modifier.clickable { openBottomSheet() }) {
        Column {
            Text(
                text = stringResource(id = R.string.importance),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = uiState.importance.nameRu,
                color = if (uiState.importance == ItemImportance.HIGH) MaterialTheme.colorScheme.TodoRed else Color.Unspecified,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun InputTodoText(
    uiState: TodoItemCardUiState, reduce: (TodoItemCardIntent) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(com.toloknov.summerschool.theme.theme.PADDING_MEDIUM))
            .defaultMinSize(minHeight = 100.dp),
        value = uiState.text,
        onValueChange = { reduce(TodoItemCardIntent.SetText(it)) },
        shape = RoundedCornerShape(com.toloknov.summerschool.theme.theme.PADDING_MEDIUM),
        colors = MaterialTheme.colorScheme.textFieldTheme,
        placeholder = {
            Text(
                text = stringResource(id = R.string.what_need_to_do),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        })
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
                Text(
                    text = stringResource(R.string.select),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.surfaceTint)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        properties = DialogProperties(),
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    modifier = Modifier.padding(
                        start = PADDING_BIG,
                        top = PADDING_BIG
                    ),
                    text = stringResource(R.string.date),
                    style = MaterialTheme.typography.bodyMedium
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
@PreviewLightDark
private fun TodoItemCardPreview() {
    ToDoAppTheme {
        TodoItemCardStateless(
            uiState = TodoItemCardUiState(),
            onBackClick = { },
            reduce = {},
            isLoading = false
        )
    }
}

// Превью выглядит страшно :(
@Composable
@PreviewLightDark
private fun DatePickerLight() {
    ToDoAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            DateDialog(currPickedDate = ZonedDateTime.now(),
                onConfirmButtonClick = { },
                onDismissRequest = { })
        }
    }
}

@Composable
@PreviewLightDark
private fun Toggle() {
    ToDoAppTheme {
        Surface {
            Column {
                Switch(checked = true, onCheckedChange = {})
                Switch(checked = false, onCheckedChange = {})
            }

        }
    }
}