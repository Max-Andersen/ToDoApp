package com.toloknov.summerschool.todoapp.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.toloknov.summerschool.todoapp.TodoApp
import com.toloknov.summerschool.todoapp.data.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import com.toloknov.summerschool.todoapp.ui.card.TodoItemCardEffect
import com.toloknov.summerschool.todoapp.ui.card.TodoItemCardViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
import kotlin.coroutines.coroutineContext

class TodoItemsListViewModel(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<TodoItemsListUiState> =
        MutableStateFlow(TodoItemsListUiState())

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            Log.e(TAG, exception.stackTrace.toString())
            // По-хорошему нужно понять что за исключение
            _effect.emit(TodoItemsListEffect.ShowSnackbar("Ошибка загрузки данных"))
        }
    }

    val uiState: StateFlow<TodoItemsListUiState> =
        combine(todoItemsRepository.getLocalItems(), _uiState) { items, uiState ->
            val itemsToShow = if (uiState.showDoneItems) items else items.filter { !it.isDone }

            Log.d(TAG, uiState.isLoading.toString())
            Log.d(TAG, items.size.toString())

            TodoItemsListUiState(
                isLoading = uiState.isLoading,
                items = itemsToShow.map { it.toUiModel() },
                showDoneItems = uiState.showDoneItems
            )
        }
            .flowOn(Dispatchers.Default + exceptionHandler)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TodoItemsListUiState())


    private val _effect = MutableSharedFlow<TodoItemsListEffect>()
    val effect: SharedFlow<TodoItemsListEffect> = _effect.asSharedFlow()

    fun reduce(intent: TodoItemsListIntent) =
        viewModelScope.launch(Dispatchers.Default + exceptionHandler) {
            when (intent) {
                is TodoItemsListIntent.ClickOnShowDoneItems -> {
                    _uiState.update { lastState ->
                        lastState.copy(
                            showDoneItems = !lastState.showDoneItems
                        )
                    }
                }

                is TodoItemsListIntent.DeleteItem -> {
                    _uiState.update { prevState -> prevState.copy(isLoading = true) }
                    todoItemsRepository.removeItem(intent.itemId).onFailure {
                        _effect.emit(TodoItemsListEffect.ShowSnackbar("Ошибка удаления"))
                    }
                    _uiState.update { prevState -> prevState.copy(isLoading = false) }
                }

                is TodoItemsListIntent.ChangeItemStatus -> {
                    _uiState.update { prevState -> prevState.copy(isLoading = true) }
                    todoItemsRepository.setDoneStatusForItem(intent.itemId, intent.newStatus)
                        .onFailure {
                            _effect.emit(TodoItemsListEffect.ShowSnackbar("Ошибка изменения статуса"))
                        }
                    _uiState.update { prevState -> prevState.copy(isLoading = false) }

                }

                TodoItemsListIntent.SyncData -> {
                    _uiState.update { prevState -> prevState.copy(isLoading = true) }
                    todoItemsRepository.syncItemsWithResult().onFailure {
                        _effect.emit(TodoItemsListEffect.ShowSnackbar("Ошибка получения данных"))
                    }
                    _uiState.update { prevState -> prevState.copy(isLoading = false) }
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

                return TodoItemsListViewModel(
                    (application as TodoApp).getTodoItemsRepository(),
                ) as T
            }
        }

        private val TAG = TodoItemsListViewModel::class.simpleName
    }
}

data class TodoItemsListUiState(
    val isLoading: Boolean = false,
    val items: List<TodoItemUi> = emptyList(),
    val showDoneItems: Boolean = true
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