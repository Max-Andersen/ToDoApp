package com.toloknov.summerschool.todoapp.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toloknov.summerschool.domain.model.ItemImportance
import com.toloknov.summerschool.theme.theme.importanceCheckBoxTheme
import com.toloknov.summerschool.todoapp.R
import com.toloknov.summerschool.todoapp.ui.common.toolbar.CollapsingTitle
import com.toloknov.summerschool.todoapp.ui.common.toolbar.CollapsingTopbar
import com.toloknov.summerschool.todoapp.ui.common.utils.convertToReadable
import java.time.ZonedDateTime

@Composable
fun TodoItemsList(
    viewModel: TodoItemsListViewModel,
    clickOnItem: (itemId: String) -> Unit,
    clickOnCreate: () -> Unit,
    clickOnSettings: () -> Unit,
    clickOnAbout: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.reduce(TodoItemsListIntent.SyncData)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TodoItemsListEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    TodoItemsStateless(
        networkAvailable = uiState.networkAvailable,
        isLoading = uiState.isLoading,
        items = uiState.items,
        showDoneItems = uiState.showDoneItems,
        reduce = viewModel::reduce,
        clickOnItem = clickOnItem,
        clickOnCreate = clickOnCreate,
        clickOnSettings = clickOnSettings,
        clickOnAbout = clickOnAbout,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun TodoItemsStateless(
    networkAvailable: Boolean,
    isLoading: Boolean = false,
    items: List<TodoItemUi>,
    showDoneItems: Boolean,
    reduce: (TodoItemsListIntent) -> Unit,
    clickOnItem: (itemId: String) -> Unit,
    clickOnCreate: () -> Unit,
    clickOnSettings: () -> Unit,
    clickOnAbout: () -> Unit,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    val scrollBehavior =
        com.toloknov.summerschool.todoapp.ui.common.toolbar.rememberToolbarScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .systemBarsPadding(),
        topBar = {
            CollapsingTopbar(
                modifier = Modifier,
                scrollBehavior = scrollBehavior,
                collapsingTitle = CollapsingTitle(
                    titleText = stringResource(id = R.string.my_todo_items),
                    expandedTextStyle = MaterialTheme.typography.titleLarge
                ),
                actions = {
                    NetworkStatusIcon(networkAvailable)

                    ShowDoneItemsIcon(showDoneItems) {
                        reduce(TodoItemsListIntent.ClickOnShowDoneItems)
                    }

                    SettingsIcon(clickOnSettings)

                    AboutIcon(clickOnAbout)
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
                            modifier = Modifier.padding(start = com.toloknov.summerschool.theme.theme.PADDING_BIG),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else null,
                collapsedElevation = 10.dp,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = clickOnCreate,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        snackbarHost = {
            Box(modifier = Modifier.safeDrawingPadding()) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { snackbarData: SnackbarData ->
                        com.toloknov.summerschool.todoapp.ui.common.snackbar.SnackbarError(
                            text = snackbarData.visuals.message,
                            onClick = { snackbarHostState.currentSnackbarData?.dismiss() }
                        )
                    })
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator()
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + com.toloknov.summerschool.theme.theme.PADDING_MEDIUM,
                    bottom = com.toloknov.summerschool.theme.theme.PADDING_BIG * 2
                )
            ) {
                if (items.isEmpty() && !isLoading) {
                    ListEmpty()
                } else {
                    ConterItem(
                        isListEmpty = items.isEmpty(),
                        roundedCornerShape = RoundedCornerShape(
                            topStart = com.toloknov.summerschool.theme.theme.PADDING_MEDIUM,
                            topEnd = com.toloknov.summerschool.theme.theme.PADDING_MEDIUM
                        )
                    )

                    items(items, key = { it.id }) { itemUi ->
                        TodoListItem(
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            itemUi = itemUi,
                            clickOnItem = { clickOnItem(itemUi.id) },
                            onChangeStatus = { newStatus ->
                                reduce(
                                    TodoItemsListIntent.ChangeItemStatus(
                                        itemUi.id,
                                        newStatus
                                    )
                                )
                            },
                            onDelete = { reduce(TodoItemsListIntent.DeleteItem(itemUi.id)) }
                        )
                    }

                    ConterItem(
                        isListEmpty = items.isEmpty(),
                        roundedCornerShape = RoundedCornerShape(
                            bottomStart = com.toloknov.summerschool.theme.theme.PADDING_MEDIUM,
                            bottomEnd = com.toloknov.summerschool.theme.theme.PADDING_MEDIUM
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutIcon(clickOnAbout: () -> Unit) {
    IconButton(onClick = clickOnAbout) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun SettingsIcon(clickOnSettings: () -> Unit) {
    IconButton(onClick = clickOnSettings) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun NetworkStatusIcon(networkAvailable: Boolean) {
    Icon(
        modifier = Modifier.minimumInteractiveComponentSize(),
        painter = painterResource(id = if (networkAvailable) R.drawable.connetion_on else R.drawable.connetion_off),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primaryContainer
    )
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.ConterItem(
    isListEmpty: Boolean,
    roundedCornerShape: RoundedCornerShape
) {
    item {
        if (!isListEmpty) {
            Box(
                modifier = Modifier
                    .height(com.toloknov.summerschool.theme.theme.PADDING_SMALL)
                    .fillMaxWidth()
                    .clip(roundedCornerShape)
                    .animateItemPlacement()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    }
}

private fun LazyListScope.ListEmpty() {
    item {
        Box(modifier = Modifier.fillParentMaxSize()) {
            Text(
                text = stringResource(id = R.string.list_is_empty),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium
            )
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
        SwipeToDismissBoxValue.StartToEnd -> com.toloknov.summerschool.theme.theme.LightAcceptGreen
        SwipeToDismissBoxValue.EndToStart -> com.toloknov.summerschool.theme.theme.LightRejectRed
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
        modifier = Modifier
            .animateItemPlacement()
            .clickable { clickOnItem() }
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

            Icon(
                modifier = Modifier.minimumInteractiveComponentSize(),
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
    // Если элемент помечен как выполненный, то стиль будет содержать зачеркивание
    val (textStyle, textColor) = if (itemUi.isDone) {
        LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough) to MaterialTheme.colorScheme.surfaceContainerLowest
    } else {
        LocalTextStyle.current.copy(textDecoration = null) to Color.Unspecified
    }

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
                style = textStyle,
                color = textColor,
            )
        }
        itemUi.deadlineTs?.let { deadline ->
            Text(
                text = deadline,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun TodoListPreview() {
    com.toloknov.summerschool.theme.theme.ToDoAppTheme {
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
            clickOnCreate = {},
            isLoading = false,
            networkAvailable = true,
            clickOnSettings = {},
            clickOnAbout = {}
        )
    }
}


@PreviewLightDark
@Composable
private fun TodoItemListPreview() {
    com.toloknov.summerschool.theme.theme.ToDoAppTheme {
        Surface {
            val list = listOf(
                TodoItemUi(
                    id = "1",
                    text = "Купить хлеб",
                    importance = ItemImportance.COMMON,
                    isDone = false,
                    creationDate = ZonedDateTime.now().convertToReadable() ?: "",
                ),
                TodoItemUi(
                    id = "2",
                    text = "Купить много хлеба",
                    importance = ItemImportance.HIGH,
                    isDone = false,
                    creationDate = ZonedDateTime.now().convertToReadable() ?: "",
                    deadlineTs = ZonedDateTime.now().convertToReadable() ?: "",
                ),
                TodoItemUi(
                    id = "3",
                    text = "Купить много хлеба",
                    importance = ItemImportance.HIGH,
                    isDone = true,
                    creationDate = ZonedDateTime.now().convertToReadable() ?: "",
                    deadlineTs = ZonedDateTime.now().convertToReadable() ?: "",
                )
            )

            LazyColumn() {
                items(list, key = { it.id }) { item ->
                    TodoListItem(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        itemUi = item,
                        clickOnItem = { },
                        onChangeStatus = { },
                        onDelete = { }
                    )
                }
            }
        }
    }
}
