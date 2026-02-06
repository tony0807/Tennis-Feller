package com.qiuyou.tennis.presentation.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.qiuyou.tennis.R
import com.qiuyou.tennis.presentation.navigation.Screen
import com.qiuyou.tennis.presentation.screens.course.CourseListScreen
import com.qiuyou.tennis.presentation.screens.play.PlayListScreen
import com.qiuyou.tennis.presentation.screens.profile.ProfileScreen
import com.qiuyou.tennis.presentation.screens.tournament.TournamentListScreen
import kotlinx.coroutines.launch

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: @Composable (NavHostController) -> Unit
)

data class CreateOption(
    val label: String,
    val icon: ImageVector,
    val type: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showCreateSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var sharedSelectedDate by rememberSaveable { mutableStateOf<String?>(null) }
    
    val navItems = listOf(
        BottomNavItem(
            label = stringResource(R.string.nav_play),
            icon = Icons.Default.SportsTennis,
            screen = { PlayListScreen(it, sharedSelectedDate, onDateSelected = { sharedSelectedDate = it }) }
        ),
        BottomNavItem(
            label = stringResource(R.string.nav_tournament),
            icon = Icons.Default.EmojiEvents,
            screen = { TournamentListScreen(it, sharedSelectedDate, onDateSelected = { sharedSelectedDate = it }) }
        ),
        // Placeholder for center FAB
        BottomNavItem(
            label = "",
            icon = Icons.Default.Add,
            screen = { }
        ),
        BottomNavItem(
            label = stringResource(R.string.nav_course),
            icon = Icons.Default.Book,
            screen = { CourseListScreen(it, sharedSelectedDate, onDateSelected = { sharedSelectedDate = it }) }
        ),
        BottomNavItem(
            label = stringResource(R.string.nav_profile),
            icon = Icons.Default.AccountCircle,
            screen = { ProfileScreen(it) }
        )
    )
    
    val createOptions = listOf(
        CreateOption(
            label = stringResource(R.string.nav_play),
            icon = Icons.Default.SportsTennis,
            type = "PLAY"
        ),
        CreateOption(
            label = stringResource(R.string.nav_tournament),
            icon = Icons.Default.EmojiEvents,
            type = "TOURNAMENT"
        ),
        CreateOption(
            label = stringResource(R.string.nav_course),
            icon = Icons.Default.Book,
            type = "COURSE"
        )
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    if (index == 2) {
                        // Center FAB integrated with NavigationBar
                        NavigationBarItem(
                            selected = false,
                            onClick = { 
                                when (selectedTab) {
                                    0 -> navController.navigate(Screen.CreatePlay.route)
                                    1 -> navController.navigate(Screen.CreateTournament.route)
                                    3 -> navController.navigate(Screen.CreateCourse.route)
                                    else -> showCreateSheet = true
                                }
                            },
                            icon = {
                                FloatingActionButton(
                                    onClick = { 
                                        when (selectedTab) {
                                            0 -> navController.navigate(Screen.CreatePlay.route)
                                            1 -> navController.navigate(Screen.CreateTournament.route)
                                            3 -> navController.navigate(Screen.CreateCourse.route)
                                            else -> showCreateSheet = true
                                        }
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Create", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            },
                            label = { }
                        )
                    } else {
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { 
                                selectedTab = index
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            navItems[selectedTab].screen(navController)
        }
        
        // Bottom sheet for create options
        if (showCreateSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCreateSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.create_activity),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    CreateOptionItem(
                    icon = Icons.Default.Add,
                    label = "发布约球",
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showCreateSheet = false
                            navController.navigate(Screen.CreatePlay.route)
                        }
                    }
                )
                CreateOptionItem(
                    icon = Icons.Default.Add,
                    label = "发布赛事",
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showCreateSheet = false
                            navController.navigate(Screen.CreateTournament.route)
                        }
                    }
                )
                CreateOptionItem(
                    icon = Icons.Default.Add,
                    label = "发布课程",
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showCreateSheet = false
                            navController.navigate(Screen.CreateCourse.route)
                        }
                    }
                )
                }
            }
        }
    }
}

@Composable
private fun CreateOptionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(label) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}
