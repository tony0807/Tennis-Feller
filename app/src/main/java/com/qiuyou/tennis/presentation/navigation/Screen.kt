package com.qiuyou.tennis.presentation.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Main Tabs
    object Main : Screen("main")
    object Play : Screen("play")
    object Tournament : Screen("tournament")
    object Course : Screen("course")
    object Profile : Screen("profile")
    
    // Activity Details
    object ActivityDetail : Screen("activity/{activityId}") {
        fun createRoute(activityId: String) = "activity/$activityId"
    }
    
    // Create Activity
    object CreatePlay : Screen("create_play")
    object CreateTournament : Screen("create_tournament")
    object CreateCourse : Screen("create_course")
    
    // Payment
    object Payment : Screen("payment/{activityId}") {
        fun createRoute(activityId: String) = "payment/$activityId"
    }
    
    // My Activities
    object MyActivities : Screen("my_activities")
    
    // Edit Profile
    object EditProfile : Screen("edit_profile")
}
