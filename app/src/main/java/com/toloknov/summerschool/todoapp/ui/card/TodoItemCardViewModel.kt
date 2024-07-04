package com.toloknov.summerschool.todoapp.ui.card

import android.util.Log
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
import com.toloknov.summerschool.todoapp.ui.list.TodoItemsListViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.ZonedDateTime
import java.util.UUID

class TodoItemCardViewModel(
    private val todoItemsRepository: TodoItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId = savedStateHandle.get<String>("itemId")

    private val _uiState: MutableStateFlow<TodoItemCardUiState> =
        MutableStateFlow(TodoItemCardUiState())
    val uiState: StateFlow<TodoItemCardUiState> =
        _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _uiState.value)

    private val _effect: MutableSharedFlow<TodoItemCardEffect> = MutableSharedFlow()
    val effect: SharedFlow<TodoItemCardEffect> = _effect

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            Log.e(TAG, "caught  " + exception.stackTraceToString())
            // По-хорошему нужно понять что за исключение
            _effect.emit(TodoItemCardEffect.ShowSnackbar("Ошибка загрузки данных"))
        }
    }

    // Если не сделать тут delay, то из-за очень быстрой работы с мок-данными, effect на отображение снекбара улетит до подписки экраном на shared flow
    // есть решение replay = 1, чтобы дублировать пропущенные события, но выглядит больше костылём
    // так что в будущем добавлю progres bar
    init {
        viewModelScope.launch(Dispatchers.Default + exceptionHandler) {
            itemId?.let {
                // Имитируем загрузку с БД/сети
                delay(1000L)
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


    fun reduce(intent: TodoItemCardItent) =
        viewModelScope.launch(Dispatchers.Default + exceptionHandler) {
            when (intent) {
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

        private val TAG = TodoItemCardViewModel::class.simpleName
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

    data class ShowSnackbar(val message: String) : TodoItemCardEffect()
}

sealed class TodoItemCardItent {

    data class SetText(val text: String) : TodoItemCardItent()

    data class SetImportance(val importance: ItemImportance) : TodoItemCardItent()

    data class SetDeadline(val deadline: ZonedDateTime?) : TodoItemCardItent()

    data object SaveTodoItem : TodoItemCardItent()

    data object DeleteTodoItem : TodoItemCardItent()

}