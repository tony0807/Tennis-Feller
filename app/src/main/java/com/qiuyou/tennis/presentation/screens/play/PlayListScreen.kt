package com.qiuyou.tennis.presentation.screens.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qiuyou.tennis.R
import com.qiuyou.tennis.presentation.components.ActivityCard
import com.qiuyou.tennis.presentation.components.ActivityCardData
import com.qiuyou.tennis.presentation.components.DateSelector
import com.qiuyou.tennis.presentation.navigation.Screen
import com.qiuyou.tennis.presentation.viewmodel.ActivityListUiState
import com.qiuyou.tennis.presentation.viewmodel.ActivityListViewModel
import kotlinx.coroutines.launch

@Composable
fun PlayListScreen(
    navController: NavHostController,
    initialDate: String? = null,
    onDateSelected: (String?) -> Unit = {},
    viewModel: ActivityListViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val activityCounts by viewModel.activitiesCount.collectAsState()
    
    LaunchedEffect(initialDate) {
        viewModel.loadActivities("PLAY", initialDate)
    }

    LaunchedEffect(Unit) {
        // Load counts for the next 7 days
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val dates = List(7) {
            val time = calendar.timeInMillis
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            time
        }
        viewModel.loadActivitiesCount("PLAY", dates)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Date selector
            DateSelector(
                modifier = Modifier.padding(vertical = 8.dp),
                activityCounts = activityCounts,
                onDateSelected = { dateItem ->
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA)
                    val dateString = sdf.format(java.util.Date(dateItem.date))
                    viewModel.selectDate("PLAY", dateString)
                    onDateSelected(dateString)
                }
            )
            
            // Content based on state
            when (val state = uiState) {
                is ActivityListUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ActivityListUiState.Success -> {
                    if (state.activities.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_activities),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.activities) { activity ->
                                ActivityCard(
                                    activity = ActivityCardData(
                                        id = activity.id,
                                        title = activity.title,
                                        time = formatTime(activity.startTime, activity.endTime),
                                        location = activity.customLocation ?: "球场",
                                        distance = "0km",
                                        skillLevel = activity.skillLevel,
                                        activityType = activity.activityType,
                                        currentParticipants = activity.currentParticipants,
                                        maxParticipants = activity.maxParticipants,
                                        pricePerPerson = activity.fee,
                                        imageUrl = if (!activity.images.isNullOrEmpty()) {
                                            try {
                                                activity.images.removePrefix("[").removeSuffix("]")
                                                    .split(",")
                                                    .firstOrNull { it.isNotEmpty() }
                                                    ?.trim()
                                                    ?.removeSurrounding("\"")
                                            } catch (e: Exception) {
                                                null
                                            }
                                        } else null
                                    ),
                                    onCardClick = {
                                        navController.navigate(Screen.ActivityDetail.createRoute(activity.id))
                                    },
                                    onLocationClick = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("打开地图: ${activity.customLocation}")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is ActivityListUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private fun formatTime(startTime: Long, endTime: Long): String {
    val sdf = java.text.SimpleDateFormat("MM月dd日 HH:mm", java.util.Locale.CHINA)
    val start = sdf.format(java.util.Date(startTime))
    val endSdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.CHINA)
    val end = endSdf.format(java.util.Date(endTime))
    return "$start-$end"
}
