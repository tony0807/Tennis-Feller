package com.qiuyou.tennis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuyou.tennis.data.model.ActivityEntity
import com.qiuyou.tennis.data.model.UserEntity
import com.qiuyou.tennis.data.repository.ActivityRepository
import com.qiuyou.tennis.data.repository.RegistrationRepository
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActivityDetailUiState {
    object Loading : ActivityDetailUiState()
    data class Success(
        val activity: ActivityEntity,
        val participants: List<UserEntity>,
        val isRegistered: Boolean,
        val isCreator: Boolean
    ) : ActivityDetailUiState()
    data class Error(val message: String) : ActivityDetailUiState()
}

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val registrationRepository: RegistrationRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ActivityDetailUiState>(ActivityDetailUiState.Loading)
    val uiState: StateFlow<ActivityDetailUiState> = _uiState.asStateFlow()
    
    private val _registrationResult = MutableStateFlow<Result<Unit>?>(null)
    val registrationResult: StateFlow<Result<Unit>?> = _registrationResult.asStateFlow()
    
    fun loadActivity(activityId: String) {
        viewModelScope.launch {
            val activityResult = activityRepository.getActivityById(activityId)
            
            when (activityResult) {
                is Result.Success -> {
                    val activity = activityResult.data
                    val currentUserId = userRepository.getCurrentUserId() ?: ""
                    
                    // Load participants
                    registrationRepository.getParticipants(activityId).collect { participantsResult ->
                        val participants = if (participantsResult is Result.Success) {
                            participantsResult.data
                        } else {
                            emptyList()
                        }
                        
                        // Check if user is registered
                        val isRegistered = registrationRepository.isRegistered(activityId, currentUserId)
                        
                        _uiState.value = ActivityDetailUiState.Success(
                            activity = activity,
                            participants = participants,
                            isRegistered = isRegistered,
                            isCreator = activity.creatorId == currentUserId
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.value = ActivityDetailUiState.Error(activityResult.message)
                }
                else -> {
                    _uiState.value = ActivityDetailUiState.Loading
                }
            }
        }
    }
    
    fun registerActivity(activityId: String) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserId()
            if (currentUserId == null) {
                _registrationResult.value = Result.Error("请先登录")
                return@launch
            }
            
            _registrationResult.value = Result.Loading
            val result = registrationRepository.registerActivity(activityId, currentUserId)
            _registrationResult.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                else -> Result.Error("报名失败")
            }
            
            // Reload activity to update participant count
            if (result is Result.Success) {
                loadActivity(activityId)
            }
        }
    }
    
    fun cancelRegistration(activityId: String) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserId()
            if (currentUserId == null) {
                _registrationResult.value = Result.Error("请先登录")
                return@launch
            }
            
            _registrationResult.value = Result.Loading
            val result = registrationRepository.cancelRegistration(activityId, currentUserId)
            _registrationResult.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                else -> Result.Error("取消失败")
            }
            
            // Reload activity
            if (result is Result.Success) {
                loadActivity(activityId)
            }
        }
    }
    
    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            _registrationResult.value = Result.Loading
            val result = activityRepository.deleteActivity(activityId)
            _registrationResult.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                else -> Result.Error("取消活动失败")
            }
        }
    }
    
    fun clearRegistrationResult() {
        _registrationResult.value = null
    }
}
