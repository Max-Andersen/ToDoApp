package com.toloknov.summerschool.todoapp.ui.card

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toloknov.summerschool.todoapp.data.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import com.toloknov.summerschool.todoapp.ui.list.TodoItemsListIntent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoItemCardViewModel(
    // savedStateHandle: SavedStateHandle, не нашёл вариант, как получить его без di
    private val todoItemsRepository: TodoItemsRepository = TodoItemsRepositoryImpl()
) : ViewModel() {

    val _uiState: MutableStateFlow<TodoItemCardUiState> = MutableStateFlow(TodoItemCardUiState())
    val uiState: StateFlow<TodoItemCardUiState> = _uiState

    val _effect: MutableSharedFlow<TodoItemCardEffect> = MutableSharedFlow()
    val effect: SharedFlow<TodoItemCardEffect> = _effect


    fun reduce(intent: TodoItemCardItent) = viewModelScope.launch {
        when (intent) {
            is TodoItemCardItent.SetItemId -> {
                val itemData = todoItemsRepository.getById(intent.itemId)
                itemData?.let { data ->
                    _uiState.update { lastState ->
                        lastState.copy(
                            itemId = intent.itemId,
                            text = data.text,
                            importance = data.importance,
                            deadline = data.deadlineTs?.toEpochSecond()
                        )
                    }
                }
            }

            is TodoItemCardItent.SetText -> {
                _uiState.update { lastState ->
                    lastState.copy(
                        text = intent.text
                    )
                }
            }

            is TodoItemCardItent.SetImportance -> {
                _uiState.update { lastState ->
                    lastState.copy(
                        importance = intent.importance
                    )
                }
            }

            is TodoItemCardItent.SetDeadline -> {
                _uiState.update { lastState ->
                    lastState.copy(
                        deadline = intent.deadline
                    )
                }
            }

            is TodoItemCardItent.DeleteTodoItem -> {
                _uiState.value.itemId?.let { todoItemsRepository.removeItem(it) }
                _effect.emit(TodoItemCardEffect.NavigateBack)
            }

            is TodoItemCardItent.SaveTodoItem -> {

                with(_uiState.value) {

                    if (itemId == null) {

                    } else {

                    }

                }
                _effect.emit(TodoItemCardEffect.NavigateBack)
            }
        }
    }

}


data class TodoItemCardUiState(
    val itemId: String? = null,

    val text: String = "",
    val importance: ItemImportance = ItemImportance.COMMON,
    val deadline: Long? = null,
)

sealed class TodoItemCardEffect {
    data object NavigateBack : TodoItemCardEffect()
}

sealed class TodoItemCardItent {

    // region временные интенты, уберу после добавления di
    data class SetItemId(val itemId: String) : TodoItemCardItent()
    // endregion


    data class SetText(val text: String) : TodoItemCardItent()

    data class SetImportance(val importance: ItemImportance) : TodoItemCardItent()

    data class SetDeadline(val deadline: Long?) : TodoItemCardItent()

    data object SaveTodoItem : TodoItemCardItent()

    data object DeleteTodoItem : TodoItemCardItent()

}