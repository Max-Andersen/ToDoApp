package com.toloknov.summerschool.todoapp.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toloknov.summerschool.domain.api.TodoItemsRepository
import com.toloknov.summerschool.domain.model.ResponseStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoItemsListViewModel @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            Log.e(TAG, exception.stackTraceToString())
            // По-хорошему нужно понять что за исключение
            _effect.emit(TodoItemsListEffect.ShowSnackbar("Ошибка загрузки данных"))
        }
    }

    private val safeBackgroundCoroutineDispatcher = Dispatchers.Default + exceptionHandler

    private val _uiState: MutableStateFlow<TodoItemsListUiState> =
        MutableStateFlow(TodoItemsListUiState())

    val uiState: StateFlow<TodoItemsListUiState> =
        combine(todoItemsRepository.getItems(), _uiState) { items, uiState ->
            val itemsToShow = if (uiState.showDoneItems) items else items.filter { !it.isDone }

            TodoItemsListUiState(
                isLoading = uiState.isLoading,
                items = itemsToShow.map { it.toUiModel() },
                showDoneItems = uiState.showDoneItems,
                networkAvailable = uiState.networkAvailable
            )
        }
            .flowOn(safeBackgroundCoroutineDispatcher)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TodoItemsListUiState())


    private val _effect = MutableSharedFlow<TodoItemsListEffect>()
    val effect: SharedFlow<TodoItemsListEffect> = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            todoItemsRepository.getActionStatusFlow().collect { status ->
                when (status) {
                    ResponseStatus.Error -> {
                        _uiState.update { prevState -> prevState.copy(isLoading = false) }
                        _effect.emit(TodoItemsListEffect.ShowSnackbar("Произошла ошибка, повторите попытку"))
                    }

                    ResponseStatus.InProgress -> {
                        _uiState.update { prevState -> prevState.copy(isLoading = true) }
                    }

                    ResponseStatus.NetworkUnavailable -> {
                        // Ok, дальше по приборам (в офлайн режиме)
                        Log.d("TodoItemsListViewModel", "Network unavailable")
                        _uiState.update { prevState ->
                            prevState.copy(
                                isLoading = false,
                                networkAvailable = false
                            )
                        }
                    }

                    ResponseStatus.Success -> {
                        _uiState.update { prevState ->
                            prevState.copy(
                                isLoading = false,
                                networkAvailable = true
                            )
                        }
                    }

                    ResponseStatus.Idle -> {}
                }
            }
        }
    }

    fun reduce(intent: TodoItemsListIntent) =
        viewModelScope.launch(safeBackgroundCoroutineDispatcher) {
            when (intent) {
                is TodoItemsListIntent.ClickOnShowDoneItems -> {
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

                TodoItemsListIntent.SyncData -> {}
            }
        }

    companion object {
        private val TAG = TodoItemsListViewModel::class.simpleName
    }
}

data class TodoItemsListUiState(
    val isLoading: Boolean = true,
    val items: List<TodoItemUi> = emptyList(),
    val showDoneItems: Boolean = true,
    val networkAvailable: Boolean = true
)

sealed class TodoItemsListEffect {
    data class ShowSnackbar(val message: String) : TodoItemsListEffect()
}

sealed class TodoItemsListIntent {
    data object SyncData : TodoItemsListIntent()
    data object ClickOnShowDoneItems : TodoItemsListIntent()
    data class ChangeItemStatus(val itemId: String, val newStatus: Boolean) : TodoItemsListIntent()
    data class DeleteItem(val itemId: String) : TodoItemsListIntent()
}