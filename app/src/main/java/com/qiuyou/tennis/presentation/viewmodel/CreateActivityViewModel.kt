package com.qiuyou.tennis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuyou.tennis.data.model.ActivityEntity
import com.qiuyou.tennis.data.model.ActivityStatus
import com.qiuyou.tennis.data.model.ActivityType
import com.qiuyou.tennis.data.model.VenueEntity
import com.qiuyou.tennis.data.repository.ActivityRepository
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.data.repository.UserRepository
import com.qiuyou.tennis.data.repository.VenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val venueRepository: VenueRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _selectedCity = MutableStateFlow("")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()
    
    private val _venues = MutableStateFlow<List<VenueEntity>>(emptyList())
    val venues: StateFlow<List<VenueEntity>> = _venues.asStateFlow()
    
    private val _createResult = MutableStateFlow<Result<ActivityEntity>?>(null)
    val createResult: StateFlow<Result<ActivityEntity>?> = _createResult.asStateFlow()
    
    init {
        loadCurrentCity()
    }
    
    private fun loadCurrentCity() {
        viewModelScope.launch {
            val city = venueRepository.getCurrentCity()
            _selectedCity.value = city
            loadVenues(city)
        }
    }
    
    fun selectCity(city: String) {
        _selectedCity.value = city
        loadVenues(city)
    }
    
    private fun loadVenues(city: String) {
        viewModelScope.launch {
            venueRepository.getVenuesByCity(city).collect { result ->
                if (result is Result.Success) {
                    _venues.value = result.data
                }
            }
        }
    }
    
    fun createActivity(
        type: ActivityType,
        title: String,
        venueId: String?,
        customLocation: String?,
        startTime: Long,
        endTime: Long,
        maxParticipants: Int,
        fee: Double,
        skillLevel: String,
        activityType: String,
        description: String?,
        creatorParticipates: Boolean = true,
        imageUris: List<android.net.Uri> = emptyList()
    ) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserId()
            if (currentUserId == null) {
                _createResult.value = Result.Error("请先登录")
                return@launch
            }
            
            // Serialize image URIs to string (simple comma separation for now, or JSON array format)
            val imagesJson = if (imageUris.isNotEmpty()) {
                "[" + imageUris.joinToString(",") { "\"$it\"" } + "]"
            } else {
                null
            }
            
            val activity = ActivityEntity(
                id = "activity_${System.currentTimeMillis()}",
                type = type,
                title = title,
                creatorId = currentUserId,
                clubId = venueId,
                customLocation = customLocation,
                latitude = null,
                longitude = null,
                startTime = startTime,
                endTime = endTime,
                duration = ((endTime - startTime) / 60000).toInt(),
                maxParticipants = maxParticipants,
                currentParticipants = if (creatorParticipates) 1 else 0,
                fee = fee,
                skillLevel = skillLevel,
                activityType = activityType,
                description = description,
                images = imagesJson,
                creatorParticipates = creatorParticipates,
                status = ActivityStatus.OPEN,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            _createResult.value = Result.Loading
            _createResult.value = activityRepository.createActivity(activity)
        }
    }
    
    fun clearCreateResult() {
        _createResult.value = null
    }
}
