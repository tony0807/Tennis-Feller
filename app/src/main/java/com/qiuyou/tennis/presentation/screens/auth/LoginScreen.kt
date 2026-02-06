package com.qiuyou.tennis.presentation.screens.auth
import androidx.compose.ui.res.painterResource

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qiuyou.tennis.R
import com.qiuyou.tennis.presentation.navigation.Screen
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App icon - Using Material icon since adaptive icons crash painterResource
        Icon(
            imageVector = Icons.Default.SportsTennis,
            contentDescription = "App Icon",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // App name
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Slogan
        Text(
            text = "勇网直前",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        var account by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(
            value = account,
            onValueChange = { account = it },
            label = { Text("账号") },
            placeholder = { Text("邮箱/手机号/用户名") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            placeholder = { Text("输入密码") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        // Login button
        Button(
            onClick = { 
                viewModel.loginWithPassword(account, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is Result.Loading && account.isNotEmpty() && password.isNotEmpty()
        ) {
            if (authState is Result.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("登录")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // WeChat login button
        OutlinedButton(
            onClick = { 
                viewModel.loginWithWeChat()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("微信一键登录")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Register option
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("没有账号？", style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { 
                navController.navigate(Screen.Register.route) 
            }) {
                Text("立即注册")
            }
        }
        
        SnackbarHost(hostState = snackbarHostState)
    }
}
