package com.qiuyou.tennis.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qiuyou.tennis.R
import com.qiuyou.tennis.data.model.toCardData
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.presentation.components.ActivityCard
import com.qiuyou.tennis.presentation.navigation.Screen
import com.qiuyou.tennis.presentation.viewmodel.MyActivitiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyActivitiesScreen(
    navController: NavHostController,
    viewModel: MyActivitiesViewModel = hiltViewModel()
) {
    val activitiesResult by viewModel.activities.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    
    val tabs = listOf(
        "约球" to "PLAY",
        "赛事" to "TOURNAMENT",
        "课程" to "COURSE"
    )
    
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.my_activities)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
                TabRow(selectedTabIndex = tabs.indexOfFirst { it.second == selectedType }) {
                    tabs.forEach { (label, type) ->
                        Tab(
                            selected = selectedType == type,
                            onClick = { viewModel.selectType(type) },
                            text = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (activitiesResult) {
                is Result.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Result.Error -> {
                    Text(
                        text = (activitiesResult as Result.Error).message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is Result.Success -> {
                    val activities = (activitiesResult as Result.Success).data
                    if (activities.isEmpty()) {
                        Text(
                            text = "暂无相关活动",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(activities) { activity ->
                                ActivityCard(
                                    activity = activity.toCardData(),
                                    onCardClick = {
                                        navController.navigate(Screen.ActivityDetail.createRoute(activity.id))
                                    },
                                    onLocationClick = {
                                        // TODO: Open map or filter by location
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
