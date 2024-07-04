package com.toloknov.summerschool.todoapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.toloknov.summerschool.todoapp.TodoApp
import com.toloknov.summerschool.todoapp.domain.api.AuthRepository
import com.toloknov.summerschool.todoapp.ui.login.LoginViewModel
import com.toloknov.summerschool.todoapp.ui.navigation.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _startDestination: MutableStateFlow<StartDestination?> = MutableStateFlow(null)
    val startDestination: StateFlow<StartDestination?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val oauthToken = authRepository.getToken()
            if (oauthToken.isNotBlank()){
                _startDestination.emit(StartDestination.LIST)
            } else{
                _startDestination.emit(StartDestination.LOGIN)
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

                return MainViewModel(
                    (application as TodoApp).getAuthRepository(),
                ) as T
            }
        }

        private val TAG = MainViewModel::class.simpleName
    }

    enum class StartDestination {
        LOGIN,
        LIST
    }
}