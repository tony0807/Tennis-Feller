package com.qiuyou.tennis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuyou.tennis.data.model.UserEntity
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserEntity) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    private val _updateResult = MutableStateFlow<Result<UserEntity>?>(null)
    val updateResult: StateFlow<Result<UserEntity>?> = _updateResult.asStateFlow()
    
    init {
        loadProfile()
    }
    
    fun loadProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile().collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> ProfileUiState.Loading
                    is Result.Success -> ProfileUiState.Success(result.data)
                    is Result.Error -> ProfileUiState.Error(result.message)
                }
            }
        }
    }
    
    fun updateProfile(
        nickname: String?,
        avatar: String?,
        skillLevel: String?,
        signature: String?
    ) {
        viewModelScope.launch {
            _updateResult.value = Result.Loading
            _updateResult.value = userRepository.updateProfile(
                nickname = nickname,
                avatar = avatar,
                skillLevel = skillLevel,
                signature = signature
            )
            
            // Reload profile if update successful
            if (_updateResult.value is Result.Success) {
                loadProfile()
            }
        }
    }
    
    fun logout() {
        userRepository.logout()
    }
    
    fun clearUpdateResult() {
        _updateResult.value = null
    }
}
