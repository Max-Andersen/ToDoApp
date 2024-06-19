package com.toloknov.summerschool.todoapp.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toloknov.summerschool.todoapp.R
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.ui.toolbar.CollapsingTitle
import com.toloknov.summerschool.todoapp.ui.toolbar.CollapsingTopbar
import com.toloknov.summerschool.todoapp.ui.toolbar.rememberToolbarScrollBehavior
import com.toloknov.summerschool.todoapp.ui.theme.PADDING_BIG
import com.toloknov.summerschool.todoapp.ui.theme.PADDING_MEDIUM
import com.toloknov.summerschool.todoapp.ui.theme.ToDoAppTheme
import com.toloknov.summerschool.todoapp.ui.theme.importanceCheckBoxTheme
import com.toloknov.summerschool.todoapp.ui.utils.convertToReadable
import java.time.ZonedDateTime

@Composable
fun TodoItemsList() {
    val viewModel: TodoItemsListViewModel = viewModel()

    val uiState by viewModel.uiState.collectAsState()

    TodoItemsStateless(
        items = uiState.items,
        showDoneItems = uiState.showDoneItems,
        reduce = viewModel::reduce
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoItemsStateless(
    items: List<TodoItemUi>,
    showDoneItems: Boolean,
    reduce: (TodoItemsListIntent) -> Unit
) {
    val scrollBehavior = rememberToolbarScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingTopbar(
                modifier = Modifier.systemBarsPadding(),
                scrollBehavior = scrollBehavior,
                collapsingTitle = CollapsingTitle(
                    titleText = stringResource(id = R.string.my_todo_items),
                    expandedTextStyle = MaterialTheme.typography.headlineLarge
                ),
                actions = {
                    ShowDoneItemsIcon(showDoneItems) {
                        reduce(TodoItemsListIntent.ClickOnShowDoneItems)
                    }
                },
                navigationIcon = null,
                // todo коммент и/или рефакторинг
                statisticContent = if (showDoneItems) {
                    {
                        Text(
                            text = stringResource(
                                R.string.count_done_items,
                                "${items.filter { it.isDone }.size}"
                            ),
                            modifier = Modifier.padding(start = PADDING_BIG)
                        )
                    }
                } else null,
                collapsedElevation = 10.dp,
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus_24),
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.size(paddingValues.calculateTopPadding()))
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .shadow(2.dp, RoundedCornerShape(PADDING_MEDIUM))
                    .clip(RoundedCornerShape(PADDING_MEDIUM))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Spacer(modifier = Modifier.size(PADDING_MEDIUM))
                items.forEach { itemUi ->
                    key(itemUi.id) {
                        TodoListItem(
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                            itemUi = itemUi,
                            clickOnItem = { },
                            onChangeStatus = { newStatus ->
                                reduce(TodoItemsListIntent.ChangeItemStatus(itemUi.id, newStatus))
                            },
                            onDelete = { reduce(TodoItemsListIntent.DeleteItem(itemUi.id)) }
                        )
                    }

                }
                Box(
                    modifier = Modifier
                        .padding(start = 48.dp)
                        .requiredHeight(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.new_item),
                    )
                }

                Spacer(modifier = Modifier.size(PADDING_MEDIUM))


            }
            Spacer(modifier = Modifier.size(PADDING_BIG * 2))
        }

    }
}

@Composable
private fun ShowDoneItemsIcon(
    showDoneItems: Boolean,
    onClick: () -> Unit,
) {
    val iconResId = remember(showDoneItems) {
        if (showDoneItems) R.drawable.ic_eye_opened_24 else R.drawable.ic_eye_closed_24
    }
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun TodoListItem(
    modifier: Modifier = Modifier,
    itemUi: TodoItemUi,
    clickOnItem: () -> Unit,
    onChangeStatus: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = itemUi.isDone,
            onCheckedChange = { newStatus -> onChangeStatus(newStatus) },
            colors = MaterialTheme.colorScheme.importanceCheckBoxTheme(itemUi.importance),
        )

        TodoListItemText(
            modifier = Modifier.weight(1f),
            itemUi = itemUi,
        )

        IconButton(onClick = clickOnItem) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info_24),
                contentDescription = null
            )
        }
    }
}

@Composable
fun TodoListItemText(
    modifier: Modifier = Modifier,
    itemUi: TodoItemUi,
) {
    Column(
        modifier = modifier.padding(vertical = 12.dp)
    ) {
        Row {
            // Почему это картинками?
            // Такие символы не будут отображаться одинаково на всех устройствах
            if (!itemUi.isDone) {
                if (itemUi.importance == ItemImportance.LOW) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_down_arrow),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Top)
                    )
                }
                if (itemUi.importance == ItemImportance.HIGH) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Top)
                    )
                }
                Spacer(modifier = Modifier.size(3.dp))
            }
            Text(
                text = itemUi.text,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        itemUi.deadlineTs?.let { deadline ->
            Text(text = deadline)
        }
    }
}


@Preview
@Composable
private fun TodoItemListPreview() {
    ToDoAppTheme {
        TodoItemsStateless(
            items = listOf(
                TodoItemUi(
                    id = "1",
                    text = "Купить хлеб",
                    importance = ItemImportance.LOW,
                    creationDate = ZonedDateTime.now().convertToReadable() ?: "",
                    isDone = false,
                    updateTs = null
                ),
                TodoItemUi(
                    id = "2",
                    text = "Купить хлеб",
                    importance = ItemImportance.LOW,
                    creationDate = ZonedDateTime.now().convertToReadable() ?: "",
                    isDone = false,
                    updateTs = null
                ),
                TodoItemUi(
                    id = "3",
                    text = "Купить хлеб важно!",
                    importance = ItemImportance.HIGH,
                    creationDate = ZonedDateTime.now().convertToReadable() ?: "",
                    deadlineTs = "2022-01-01",
                    isDone = false,
                    updateTs = null
                ),
            ),
            reduce = {},
            showDoneItems = true
        )
    }
}