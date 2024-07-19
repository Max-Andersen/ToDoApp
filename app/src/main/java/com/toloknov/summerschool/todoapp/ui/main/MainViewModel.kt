package com.toloknov.summerschool.todoapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toloknov.summerschool.domain.api.NetworkRepository
import com.toloknov.summerschool.domain.api.ThemeRepository
import com.toloknov.summerschool.domain.model.ApplicationTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val _startDestination: MutableStateFlow<StartDestination?> = MutableStateFlow(null)
    val startDestination: StateFlow<StartDestination?> = _startDestination.asStateFlow()


    private val _applicationTheme: MutableStateFlow<ApplicationTheme> = MutableStateFlow(
        ApplicationTheme.SYSTEM
    )
    val applicationTheme = _applicationTheme.asStateFlow()

    private val _tokenMaySpoil: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        // Логика тут в чем, если у нас в какой-то момент случится 401, запрос закроется и в data store токен станет ""
        // тут смотрим на текущий токен и возможность его "протухания", чтобы покрыть состояние,
        // когда заходим в первый раз и когда нас "выкидывает" из приложения из-за токена
        viewModelScope.launch {
            launch {
                networkRepository.getTokenFlow().distinctUntilChanged().collect { oauthToken ->
                    if (oauthToken.isNotBlank()) {
                        _tokenMaySpoil.emit(true)
                        _startDestination.emit(StartDestination.LIST)
                    } else {
                        if (_tokenMaySpoil.value) {
                            _startDestination.emit(StartDestination.LOGIN(true))
                        } else {
                            _startDestination.emit(StartDestination.LOGIN(false))
                        }
                        _tokenMaySpoil.emit(false)
                    }
                }
            }
            launch {
                themeRepository.getTheme().collect { theme ->
                    _applicationTheme.emit(theme)
                }
            }
        }
    }


    companion object {
        private val TAG = MainViewModel::class.simpleName
    }

    sealed class StartDestination {
        data class LOGIN(val tokenSpoiled: Boolean) : StartDestination()
        data object LIST : StartDestination()
    }
}