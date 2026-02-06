package com.qiuyou.tennis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuyou.tennis.data.model.ActivityEntity
import com.qiuyou.tennis.data.repository.ActivityRepository
import com.qiuyou.tennis.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActivityListUiState {
    object Loading : ActivityListUiState()
    data class Success(val activities: List<ActivityEntity>) : ActivityListUiState()
    data class Error(val message: String) : ActivityListUiState()
}

@HiltViewModel
class ActivityListViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ActivityListUiState>(ActivityListUiState.Loading)
    val uiState: StateFlow<ActivityListUiState> = _uiState.asStateFlow()
    
    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()

    private val _activitiesCount = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val activitiesCount: StateFlow<Map<Long, Int>> = _activitiesCount.asStateFlow()
    
    private var loadJob: kotlinx.coroutines.Job? = null
    
    fun loadActivities(type: String, date: String? = null) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            activityRepository.getActivities(type, date).collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> ActivityListUiState.Loading
                    is Result.Success -> ActivityListUiState.Success(result.data)
                    is Result.Error -> ActivityListUiState.Error(result.message)
                }
            }
        }
    }

    fun loadActivitiesCount(type: String, dates: List<Long>) {
        viewModelScope.launch {
            val counts = activityRepository.getActivitiesCountByDate(type, dates)
            _activitiesCount.value = counts
        }
    }
    
    fun selectDate(type: String, date: String?) {
        _selectedDate.value = date
        loadActivities(type, date)
    }
    
    fun refreshActivities(type: String) {
        loadActivities(type, _selectedDate.value)
    }
}
