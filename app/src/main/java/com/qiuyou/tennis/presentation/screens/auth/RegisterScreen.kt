package com.qiuyou.tennis.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qiuyou.tennis.R
import com.qiuyou.tennis.presentation.navigation.Screen
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var selectedSkillLevel by remember { mutableStateOf("3.0") }
    var signature by remember { mutableStateOf("") }
    
    val skillLevels = listOf("2.5以下", "2.5", "3.0", "3.0+", "3.5", "4.0", "4.0+")
    
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(authState) {
        if (authState is Result.Success) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            viewModel.clearAuthState()
        } else if (authState is Result.Error) {
            snackbarHostState.showSnackbar((authState as Result.Error).message)
            viewModel.clearAuthState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.phone_login)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "请完善资料完成注册",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Account/Phone
            OutlinedTextField(
                value = account,
                onValueChange = { account = it },
                label = { Text("账号") },
                placeholder = { Text("请输入手机号或用户名") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                placeholder = { Text("请输入密码") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Nickname
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text(stringResource(R.string.nickname)) },
                placeholder = { Text("请输入昵称") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Skill Level
            Column {
                Text(
                    text = stringResource(R.string.skill_level),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    skillLevels.forEach { level ->
                        FilterChip(
                            selected = selectedSkillLevel == level,
                            onClick = { selectedSkillLevel = level },
                            label = { Text(level) }
                        )
                    }
                }
            }
            
            // Signature
            OutlinedTextField(
                value = signature,
                onValueChange = { signature = it },
                label = { Text("个性签名") },
                placeholder = { Text("介绍一下自己吧（选填）") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Register button
            Button(
                onClick = {
                    viewModel.register(account, password, nickname, selectedSkillLevel, signature)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = account.isNotEmpty() && password.isNotEmpty() && nickname.isNotEmpty() && authState !is Result.Loading
            ) {
                if (authState is Result.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.register))
                }
            }
        }
    }
}
