package com.toloknov.summerschool.todoapp.ui.card

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.toloknov.summerschool.todoapp.TodoApp
import com.toloknov.summerschool.todoapp.data.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import com.toloknov.summerschool.todoapp.ui.common.utils.convertToReadable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.UUID

class TodoItemCardViewModel(
    private val todoItemsRepository: TodoItemsRepository = TodoItemsRepositoryImpl(),
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId = savedStateHandle.get<String>("isNewItem")


    private val _uiState: MutableStateFlow<TodoItemCardUiState> =
        MutableStateFlow(TodoItemCardUiState())
    val uiState: StateFlow<TodoItemCardUiState> = _uiState

    private val _effect: MutableSharedFlow<TodoItemCardEffect> = MutableSharedFlow()
    val effect: SharedFlow<TodoItemCardEffect> = _effect

    init {
        viewModelScope.launch {
            itemId?.let {
                val itemData = todoItemsRepository.getById(itemId)
                itemData?.let { data ->
                    _uiState.update { lastState ->
                        lastState.copy(
                            isNewItem = false,
                            text = data.text,
                            isDone = data.isDone,
                            importance = data.importance,
                            deadline = data.deadlineTs,
                            creationTime = data.creationDate
                        )
                    }
                }
            }
        }
    }


    fun reduce(intent: TodoItemCardItent) = viewModelScope.launch {
        when (intent) {
            is TodoItemCardItent.SetItemId -> {

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
                if (itemId != null) {
                    todoItemsRepository.removeItem(itemId)
                }
                _effect.emit(TodoItemCardEffect.NavigateBack)
            }

            is TodoItemCardItent.SaveTodoItem -> {

                with(_uiState.value) {

                    if (isNewItem) {
                        todoItemsRepository.addItem(
                            TodoItem(
                                id = UUID.randomUUID().toString(),
                                text = text,
                                importance = importance,
                                isDone = false,
                                creationDate = ZonedDateTime.now(),
                                deadlineTs = deadline,
                                updateTs = ZonedDateTime.now(),
                            )
                        )
                    } else {
                        // Невозможное состояние, но кто его знает
                        val currentItemId = requireNotNull(itemId)
                        val currentItemCreationDate = requireNotNull(creationTime)
                        todoItemsRepository.updateItem(
                            TodoItem(
                                id = currentItemId,
                                text = text,
                                importance = importance,
                                isDone = isDone,
                                creationDate = currentItemCreationDate,
                                deadlineTs = deadline,
                                updateTs = ZonedDateTime.now(),
                            )
                        )
                    }

                }
                _effect.emit(TodoItemCardEffect.NavigateBack)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Получаем инстанс приложения (а он один, поэтому и di контейнер будет один)
                val application = checkNotNull(extras[APPLICATION_KEY])
                // Создаём SavedStateHandle для чтения навигационных аргументов
                val savedStateHandle = extras.createSavedStateHandle()

                return TodoItemCardViewModel(
                    (application as TodoApp).getTodoItemsRepository(),
                    savedStateHandle
                ) as T
            }
        }
    }

}


data class TodoItemCardUiState(
    val isNewItem: Boolean = true,

    val text: String = "",
    val isDone: Boolean = false,
    val importance: ItemImportance = ItemImportance.COMMON,
    val creationTime: ZonedDateTime? = null,
    val deadline: ZonedDateTime? = null,
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

    data class SetDeadline(val deadline: ZonedDateTime?) : TodoItemCardItent()

    data object SaveTodoItem : TodoItemCardItent()

    data object DeleteTodoItem : TodoItemCardItent()

}