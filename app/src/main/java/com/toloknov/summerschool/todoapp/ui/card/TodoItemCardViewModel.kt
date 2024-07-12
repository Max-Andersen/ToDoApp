package com.toloknov.summerschool.todoapp.ui.card

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toloknov.summerschool.todoapp.data.remote.model.ResponseStatus
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TodoItemCardViewModel @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            Log.e(TAG, "caught  " + exception.stackTraceToString())
            // По-хорошему нужно понять что за исключение
            _effect.emit(TodoItemCardEffect.ShowSnackbar("Ошибка загрузки данных"))
        }
    }

    private val safeBackgroundCoroutineDispatcher = Dispatchers.Default + exceptionHandler

    private val itemId = savedStateHandle.get<String>("itemId")

    private val _uiState: MutableStateFlow<TodoItemCardUiState> =
        MutableStateFlow(TodoItemCardUiState())
    val uiState: StateFlow<TodoItemCardUiState> =
        combine(todoItemsRepository.getActionStatusFlow(), _uiState) { status, state ->
            TodoItemCardUiState(
                isLoading = state.isLoading,
                networkRequestStatus = status,
                isNewItem = state.isNewItem,
                text = state.text,
                isDone = state.isDone,
                importance = state.importance,
                creationTime = state.creationTime,
                deadline = state.deadline,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _uiState.value)

    private val _effect: MutableSharedFlow<TodoItemCardEffect> = MutableSharedFlow()
    val effect: SharedFlow<TodoItemCardEffect> = _effect


    init {
        viewModelScope.launch(safeBackgroundCoroutineDispatcher) {
            launch {
                itemId?.let {
                    val item = todoItemsRepository.getById(itemId)

                    item?.let { data ->
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

            launch {
                todoItemsRepository.getActionStatusFlow().collect { status ->
                    when (status) {
                        ResponseStatus.Idle -> {}

                        ResponseStatus.Error -> {
                            _uiState.update { prevState -> prevState.copy(isLoading = false) }
                            _effect.emit(TodoItemCardEffect.ShowSnackbar("Произошла ошибка, повторите попытку"))
                        }

                        ResponseStatus.InProgress -> {
                            _uiState.update { prevState -> prevState.copy(isLoading = true) }
                        }

                        ResponseStatus.NetworkUnavailable -> {
                            // Ok, дальше по приборам (в офлайн режиме)
                            _uiState.update { prevState -> prevState.copy(isLoading = false) }
                            _effect.emit(TodoItemCardEffect.NavigateBack)
                        }

                        ResponseStatus.Success -> {
                            _effect.emit(TodoItemCardEffect.NavigateBack)
                        }
                    }
                }
            }
        }
    }

    fun reduce(intent: TodoItemCardIntent) =
        viewModelScope.launch(safeBackgroundCoroutineDispatcher) {
            when (intent) {
                is TodoItemCardIntent.SetText -> {
                    _uiState.update { lastState ->
                        lastState.copy(
                            text = intent.text
                        )
                    }
                }

                is TodoItemCardIntent.SetImportance -> {
                    _uiState.update { lastState ->
                        lastState.copy(
                            importance = intent.importance
                        )
                    }
                }

                is TodoItemCardIntent.SetDeadline -> {
                    _uiState.update { lastState ->
                        lastState.copy(
                            deadline = intent.deadline
                        )
                    }
                }

                is TodoItemCardIntent.DeleteTodoItem -> {
                    if (itemId != null) {
                        todoItemsRepository.removeItem(itemId)
                    }
                }

                is TodoItemCardIntent.SaveTodoItem -> {
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
                            val currentItemId =
                                requireNotNull(itemId) // Невозможное состояние, но кто его знает
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
                }
            }
        }

    companion object {
        private val TAG = TodoItemCardViewModel::class.simpleName
    }

}


data class TodoItemCardUiState(
    val isLoading: Boolean = false,
    val networkRequestStatus: ResponseStatus = ResponseStatus.Idle,

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

sealed class TodoItemCardIntent {

    data class SetText(val text: String) : TodoItemCardIntent()

    data class SetImportance(val importance: ItemImportance) : TodoItemCardIntent()

    data class SetDeadline(val deadline: ZonedDateTime?) : TodoItemCardIntent()

    data object SaveTodoItem : TodoItemCardIntent()

    data object DeleteTodoItem : TodoItemCardIntent()

}