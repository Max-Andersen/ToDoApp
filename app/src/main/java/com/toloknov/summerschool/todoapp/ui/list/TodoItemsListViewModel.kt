package com.toloknov.summerschool.todoapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toloknov.summerschool.todoapp.data.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoItemsListViewModel(
    private val todoItemsRepository: TodoItemsRepository = TodoItemsRepositoryImpl()
) : ViewModel() {

    private val allItems = todoItemsRepository.getAllItems()

    private val _uiState: MutableStateFlow<TodoItemsListUiState> =
        MutableStateFlow(TodoItemsListUiState())


    val uiState: StateFlow<TodoItemsListUiState> =
        combine(allItems, _uiState) { items, uiState ->
            val itemsToShow = if (uiState.showDoneItems) items else items.filter { !it.isDone }

            TodoItemsListUiState(
                items = itemsToShow.map { it.toUiModel() },
                showDoneItems = uiState.showDoneItems
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TodoItemsListUiState())


    private val _effect = MutableSharedFlow<TodoItemsListEffect>()
    val effect: SharedFlow<TodoItemsListEffect> = _effect.asSharedFlow()

    fun reduce(intent: TodoItemsListIntent) = viewModelScope.launch {
        when (intent) {
            is TodoItemsListIntent.ClickOnShowDone -> {
                _uiState.update { lastState ->
                    lastState.copy(
                        showDoneItems = !lastState.showDoneItems
                    )
                }
            }

            is TodoItemsListIntent.DeleteItem -> {
                todoItemsRepository.removeItem(intent.itemId)
            }

            is TodoItemsListIntent.ChangeItemStatus -> {
                todoItemsRepository.setDoneStatusForItem(intent.itemId, intent.newStatus)
            }
        }
    }
}

data class TodoItemsListUiState(
    val items: List<TodoItemUi> = emptyList(),
    val showDoneItems: Boolean = true
)

sealed class TodoItemsListEffect {
}

sealed class TodoItemsListIntent {
    data object ClickOnShowDone : TodoItemsListIntent()
    data class ChangeItemStatus(val itemId: String, val newStatus: Boolean) : TodoItemsListIntent()
    data class DeleteItem(val itemId: String) : TodoItemsListIntent()

}