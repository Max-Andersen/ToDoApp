package com.toloknov.summerschool.todoapp.ui.card

import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.ui.common.theme.PADDING_BIG
import com.toloknov.summerschool.todoapp.ui.common.theme.PADDING_MEDIUM
import com.toloknov.summerschool.todoapp.ui.common.theme.ToDoAppTheme
import com.toloknov.summerschool.todoapp.ui.common.toolbar.CollapsingTopbar
import com.toloknov.summerschool.todoapp.ui.common.toolbar.rememberToolbarScrollBehavior
import com.toloknov.summerschool.todoapp.ui.common.utils.convertToReadable
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
                        Text(text = "Сохранить")
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

            var dropDownExpanded by remember {
                mutableStateOf(false)
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(PADDING_MEDIUM))
                    .defaultMinSize(minHeight = 100.dp),
                value = uiState.text,
                onValueChange = { reduce(TodoItemCardItent.SetText(it)) },
                shape = RoundedCornerShape(PADDING_MEDIUM),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Gray,
                    errorTextColor = Color.Red,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    cursorColor = Color.Black,
                    errorCursorColor = Color.Red,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Red,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                ),
                placeholder = {
                    Text(
                        text = "Что надо сделать...",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            )

            Spacer(modifier = Modifier.size(PADDING_BIG))

            Box(modifier = Modifier.clickable { dropDownExpanded = true }) {
                Column {
                    Text(text = "Важность")
                    Text(text = uiState.importance.name)
                }
                DropdownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = { dropDownExpanded = false }) {
                    importanceItems.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(text = item.name)
                            },
                            onClick = {
                                reduce(TodoItemCardItent.SetImportance(item))
                                dropDownExpanded = false
                            })
                    }
                }
            }



            Spacer(modifier = Modifier.size(PADDING_BIG))
            HorizontalDivider()
            Spacer(modifier = Modifier.size(PADDING_BIG))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Сделать до")
                Switch(checked = uiState.deadline != null, onCheckedChange = { newState ->
                    if (newState) {
                        reduce(TodoItemCardItent.SetDeadline(ZonedDateTime.now()))
                    } else {
                        reduce(TodoItemCardItent.SetDeadline(null))
                    }
                })
            }
            if (uiState.deadline != null) {
                Text(text = uiState.deadline.convertToReadable() ?: "")
            }

            Spacer(modifier = Modifier.size(PADDING_BIG))
            HorizontalDivider()
            Spacer(modifier = Modifier.size(PADDING_BIG))


            val deleteSectionColor = if (uiState.isNewItem) {
                Color.LightGray
            } else {
                Color.Red
            }


            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(PADDING_MEDIUM))
                    .clickable(enabled = !uiState.isNewItem) {
                        reduce(TodoItemCardItent.DeleteTodoItem)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = deleteSectionColor
                )
                Text(text = "Удалить", color = deleteSectionColor)

            }

            Spacer(modifier = Modifier.size(PADDING_BIG * 2))
        }
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