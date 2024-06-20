package com.toloknov.summerschool.todoapp.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toloknov.summerschool.todoapp.R
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.ui.common.theme.LightAcceptGreen
import com.toloknov.summerschool.todoapp.ui.common.theme.LightRejectRed
import com.toloknov.summerschool.todoapp.ui.common.toolbar.CollapsingTitle
import com.toloknov.summerschool.todoapp.ui.common.toolbar.CollapsingTopbar
import com.toloknov.summerschool.todoapp.ui.common.toolbar.rememberToolbarScrollBehavior
import com.toloknov.summerschool.todoapp.ui.common.theme.PADDING_BIG
import com.toloknov.summerschool.todoapp.ui.common.theme.PADDING_MEDIUM
import com.toloknov.summerschool.todoapp.ui.common.theme.PADDING_SMALL
import com.toloknov.summerschool.todoapp.ui.common.theme.ToDoAppTheme
import com.toloknov.summerschool.todoapp.ui.common.theme.importanceCheckBoxTheme
import com.toloknov.summerschool.todoapp.ui.common.utils.convertToReadable
import java.time.ZonedDateTime

@Composable
fun TodoItemsList(
    clickOnItem: (itemId: String) -> Unit,
    clickOnCreate: () -> Unit
) {
    val viewModel: TodoItemsListViewModel = viewModel()

    val uiState by viewModel.uiState.collectAsState()

    TodoItemsStateless(
        items = uiState.items,
        showDoneItems = uiState.showDoneItems,
        reduce = viewModel::reduce,
        clickOnItem = clickOnItem,
        clickOnCreate = clickOnCreate,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TodoItemsStateless(
    items: List<TodoItemUi>,
    showDoneItems: Boolean,
    reduce: (TodoItemsListIntent) -> Unit,
    clickOnItem: (itemId: String) -> Unit,
    clickOnCreate: () -> Unit
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
                onClick = clickOnCreate,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus_24),
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() - PADDING_BIG,
                bottom = PADDING_BIG * 2
            )
        ) {

            if (items.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize()) {
                        Text(
                            text = stringResource(id = R.string.list_is_empty),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            } else {
                item {
                    CornerItem(
                        clipShape = RoundedCornerShape(
                            topStart = PADDING_MEDIUM, topEnd = PADDING_MEDIUM
                        )
                    )
                }

                items(items, key = { it.id }) { itemUi ->
                    TodoListItem(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        itemUi = itemUi,
                        clickOnItem = { clickOnItem(itemUi.id) },
                        onChangeStatus = { newStatus ->
                            reduce(TodoItemsListIntent.ChangeItemStatus(itemUi.id, newStatus))
                        },
                        onDelete = { reduce(TodoItemsListIntent.DeleteItem(itemUi.id)) }
                    )
                }

                item {
                    CornerItem(
                        clipShape = RoundedCornerShape(
                            bottomStart = PADDING_MEDIUM, bottomEnd = PADDING_MEDIUM
                        )
                    )
                }
            }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.CornerItem(
    clipShape: RoundedCornerShape
) {
    Box(
        modifier = Modifier
            .height(PADDING_SMALL)
            .fillMaxWidth()
            .clip(clipShape)
            .animateItemPlacement()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.TodoListItem(
    modifier: Modifier = Modifier,
    itemUi: TodoItemUi,
    clickOnItem: () -> Unit,
    onChangeStatus: (Boolean) -> Unit,
    onDelete: () -> Unit
) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onChangeStatus(true)
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                }

                SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState false
        },
        // positional threshold of 25%
        positionalThreshold = { it * .25f }
    )

    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> LightAcceptGreen
        SwipeToDismissBoxValue.EndToStart -> LightRejectRed
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_accept_24),
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier)
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete_24),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        modifier = Modifier.animateItemPlacement()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
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

}

@Composable
fun TodoListItemText(
    modifier: Modifier = Modifier,
    itemUi: TodoItemUi,
) {
    // Если элемент помечен как выполненный, то стиль будет содержать зачеркивание
    val textStyle =
        LocalTextStyle.current.copy(textDecoration = if (itemUi.isDone) TextDecoration.LineThrough else null)

    Column(
        modifier = modifier.padding(vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Почему это картинками?
            // Такие символы не будут отображаться одинаково на всех устройствах
            if (!itemUi.isDone) {
                if (itemUi.importance == ItemImportance.LOW) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_down_arrow),
                        contentDescription = null
                    )
                }
                if (itemUi.importance == ItemImportance.HIGH) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.size(3.dp))
            }
            Text(
                text = itemUi.text,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = textStyle
            )
        }
        itemUi.deadlineTs?.let { deadline ->
            Text(
                text = deadline
            )
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
            showDoneItems = true,
            reduce = {},
            clickOnItem = {},
            clickOnCreate = {}
        )
    }
}