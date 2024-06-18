package com.toloknov.summerschool.todoapp.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toloknov.summerschool.todoapp.R
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.ui.theme.PADDING_MEDIUM
import com.toloknov.summerschool.todoapp.ui.theme.ToDoAppTheme
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
    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(title = { Text("ToDo List") })
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp)
                .shadow(2.dp, RoundedCornerShape(PADDING_MEDIUM))
                .clip(RoundedCornerShape(PADDING_MEDIUM))
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            item {
                Spacer(modifier = Modifier.size(PADDING_MEDIUM))
            }
            items(items, key = { it.id }) { itemUi ->
                TodoListItem(
                    modifier = Modifier.requiredHeight(48.dp),
                    itemUi = itemUi,
                    clickOnItem = { reduce(TodoItemsListIntent.ClickOnShowDone) },
                    onChangeStatus = { newStatus ->
                        reduce(TodoItemsListIntent.ChangeItemStatus(itemUi.id, newStatus))
                    },
                    onDelete = { reduce(TodoItemsListIntent.DeleteItem(itemUi.id)) }
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .padding(start = 48.dp)
                        .requiredHeight(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Новое",
                    )
                }

            }
            item {
                Spacer(modifier = Modifier.size(PADDING_MEDIUM))
            }
        }


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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = itemUi.isDone,
            onCheckedChange = { newStatus -> onChangeStatus(newStatus) })
        Text(
            modifier = Modifier
                .weight(1f),
            text = itemUi.text
        )
        IconButton(onClick = clickOnItem) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info_24),
                contentDescription = null
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
                )
            ),
            reduce = {},
            showDoneItems = true
        )
    }
}