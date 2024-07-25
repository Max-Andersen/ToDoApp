package com.toloknov.summerschool.todoapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toloknov.summerschool.domain.api.NetworkRepository
import com.toloknov.summerschool.domain.api.ThemeRepository
import com.toloknov.summerschool.domain.model.ApplicationTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val networkRepository: NetworkRepository
): ViewModel() {

    private val _appTheme = MutableStateFlow(ApplicationTheme.SYSTEM)
    val appTheme = _appTheme.asStateFlow()

    private val _avatarId: MutableStateFlow<String?> = MutableStateFlow(null)
    val avatarId = _avatarId.asStateFlow()


    fun logout(){
        viewModelScope.launch {
            networkRepository.saveToken("")
        }
    }

    fun changeTheme(theme: String){
        viewModelScope.launch {
            val themeEnum = ApplicationTheme.fromNameRu(theme)
            themeRepository.setTheme(themeEnum)
        }
    }

    init {
        viewModelScope.launch {
            launch {
                themeRepository.getTheme().collect{
                    _appTheme.emit(it)
                }
            }
            launch {
                _avatarId.emit(networkRepository.getAvatarId())
            }
        }
    }

}