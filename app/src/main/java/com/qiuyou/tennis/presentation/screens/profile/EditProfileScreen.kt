package com.qiuyou.tennis.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.presentation.viewmodel.ProfileUiState
import com.qiuyou.tennis.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val updateResult by viewModel.updateResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var nickname by remember { mutableStateOf("") }
    var skillLevel by remember { mutableStateOf("") }
    var signature by remember { mutableStateOf("") }
    
    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            val user = (uiState as ProfileUiState.Success).user
            nickname = user.nickname
            skillLevel = user.skillLevel ?: ""
            signature = user.signature ?: ""
        }
    }
    
    LaunchedEffect(updateResult) {
        when (val result = updateResult) {
            is Result.Success -> {
                snackbarHostState.showSnackbar("更新成功")
                viewModel.clearUpdateResult()
                navController.navigateUp()
            }
            is Result.Error -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.clearUpdateResult()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑资料") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("昵称") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = skillLevel,
                onValueChange = { skillLevel = it },
                label = { Text("网球水平 (例如: 3.0)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = signature,
                onValueChange = { signature = it },
                label = { Text("个性签名") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { 
                    viewModel.updateProfile(
                        nickname = nickname,
                        avatar = null,
                        skillLevel = skillLevel,
                        signature = signature
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nickname.isNotEmpty() && updateResult !is Result.Loading
            ) {
                if (updateResult is Result.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("保存")
                }
            }
        }
    }
}
