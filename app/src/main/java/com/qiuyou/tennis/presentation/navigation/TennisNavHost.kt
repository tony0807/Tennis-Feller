package com.qiuyou.tennis.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.qiuyou.tennis.presentation.screens.main.MainScreen
import com.qiuyou.tennis.presentation.screens.activity.ActivityDetailScreen
import com.qiuyou.tennis.presentation.screens.activity.CreateActivityScreen
import com.qiuyou.tennis.presentation.screens.tournament.CreateTournamentScreen
import com.qiuyou.tennis.presentation.screens.course.CreateCourseScreen
import com.qiuyou.tennis.presentation.screens.profile.MyActivitiesScreen
import com.qiuyou.tennis.presentation.screens.profile.EditProfileScreen
import com.qiuyou.tennis.presentation.screens.auth.LoginScreen
import com.qiuyou.tennis.presentation.screens.auth.RegisterScreen

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.qiuyou.tennis.presentation.viewmodel.AuthViewModel

@Composable
fun TennisNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Main.route else Screen.Login.route
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        
        // Main screen with bottom navigation
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
        
        // Activity detail
        // Activity detail
        composable(
            route = Screen.ActivityDetail.route,
            arguments = listOf(navArgument("activityId") { type = NavType.StringType }),
            deepLinks = listOf(
                androidx.navigation.navDeepLink { uriPattern = "https://qiuyou.tennis.app/activity/{activityId}" },
                androidx.navigation.navDeepLink { uriPattern = "qiuyou://activity/{activityId}" }
            )
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
            ActivityDetailScreen(activityId, navController)
        }
        
        // Create Play
        composable(Screen.CreatePlay.route) {
            CreateActivityScreen("PLAY", navController)
        }
        
        // Create Tournament
        composable(Screen.CreateTournament.route) {
            CreateTournamentScreen(navController)
        }
        
        // Create Course
        composable(Screen.CreateCourse.route) {
            CreateCourseScreen(navController)
        }
        
        // Payment
        composable(
            route = Screen.Payment.route,
            arguments = listOf(navArgument("activityId") { type = NavType.StringType })
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
            // PaymentScreen(activityId, navController)
        }
        
        // My activities
        composable(Screen.MyActivities.route) {
            com.qiuyou.tennis.presentation.screens.profile.MyActivitiesScreen(navController)
        }
        
        // Edit Profile
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController)
        }
    }
}
