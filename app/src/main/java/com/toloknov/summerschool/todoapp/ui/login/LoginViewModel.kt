package com.toloknov.summerschool.todoapp.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toloknov.summerschool.todoapp.domain.api.NetworkRepository
import com.yandex.authsdk.YandexAuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    // Решил не гороздить тут полноценный MVI (как во всём проекте), т.к. этот экран простой и расти не будет

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _authSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess.asStateFlow()

    fun handleResult(result: YandexAuthResult) {
        viewModelScope.launch {
            when (result) {
                is YandexAuthResult.Success -> {
                    Log.d(TAG, "Success -> token: ${result.token.value}")
                    networkRepository.saveToken(result.token.value)
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
        private val TAG = LoginViewModel::class.simpleName
    }
}