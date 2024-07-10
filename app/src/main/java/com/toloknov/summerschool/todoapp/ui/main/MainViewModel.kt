package com.toloknov.summerschool.todoapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toloknov.summerschool.todoapp.domain.api.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _startDestination: MutableStateFlow<StartDestination?> = MutableStateFlow(null)
    val startDestination: StateFlow<StartDestination?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val oauthToken = networkRepository.getToken()
            if (oauthToken.isNotBlank()){
                _startDestination.emit(StartDestination.LIST)
            } else{
                _startDestination.emit(StartDestination.LOGIN)
            }
        }
    }


    companion object {
        private val TAG = MainViewModel::class.simpleName
    }

    enum class StartDestination {
        LOGIN,
        LIST
    }
}