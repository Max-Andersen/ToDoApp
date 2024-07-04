package com.toloknov.summerschool.todoapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.toloknov.summerschool.todoapp.TodoApp
import com.toloknov.summerschool.todoapp.domain.api.AuthRepository
import com.yandex.authsdk.YandexAuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Решил не гороздить тут полноценный MVI (как во всё проекте), т.к. этот экран простой и расти не будет

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _authSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess.asStateFlow()

    fun handleResult(result: YandexAuthResult) {
        viewModelScope.launch {
            when (result) {
                is YandexAuthResult.Success -> {
                    authRepository.saveToken(result.token.value)
                    _authSuccess.emit(true)
                }
                is YandexAuthResult.Failure -> {
                    _errorMessage.emit(result.exception.message.toString())
                }
                YandexAuthResult.Cancelled -> {
                    _errorMessage.emit(result.toString())
                }
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

                return LoginViewModel(
                    (application as TodoApp).getAuthRepository(),
                ) as T
            }
        }

        private val TAG = LoginViewModel::class.simpleName
    }


}