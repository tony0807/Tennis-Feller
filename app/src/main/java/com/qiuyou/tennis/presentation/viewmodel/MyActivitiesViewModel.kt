package com.qiuyou.tennis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuyou.tennis.data.model.ActivityEntity
import com.qiuyou.tennis.data.repository.ActivityRepository
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyActivitiesViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _activities = MutableStateFlow<Result<List<ActivityEntity>>>(Result.Loading)
    val activities: StateFlow<Result<List<ActivityEntity>>> = _activities.asStateFlow()

    private val _selectedType = MutableStateFlow("PLAY")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    init {
        loadActivities()
    }

    fun selectType(type: String) {
        _selectedType.value = type
        loadActivities()
    }

    fun loadActivities() {
        val userId = userRepository.getCurrentUserId()
        if (userId == null) {
            _activities.value = Result.Error("请先登录")
            return
        }

        viewModelScope.launch {
            activityRepository.getMyActivitiesByType(userId, _selectedType.value)
                .collect { result ->
                    _activities.value = result
                }
        }
    }
}
