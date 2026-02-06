package com.qiuyou.tennis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuyou.tennis.data.model.UserEntity
import com.qiuyou.tennis.data.remote.dto.LoginData
import com.qiuyou.tennis.data.remote.dto.WeChatUserInfo
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val isLoggedIn = userRepository.isLoggedInFlow

    private val _authState = MutableStateFlow<Result<LoginData>?>(null)
    val authState: StateFlow<Result<LoginData>?> = _authState.asStateFlow()

    fun loginWithPassword(account: String, password: String) {
        viewModelScope.launch {
            userRepository.loginWithPassword(account, password).collect { result ->
                _authState.value = result
            }
        }
    }

    fun loginWithWeChat() {
        viewModelScope.launch {
            // Mock WeChat info
            val mockWeChatInfo = WeChatUserInfo(
                openId = "mock_openid_${System.currentTimeMillis()}",
                nickname = "微信用户",
                avatar = ""
            )
            userRepository.loginWithWeChat("mock_code", mockWeChatInfo).collect { result ->
                _authState.value = result
            }
        }
    }

    fun register(account: String, password: String, nickname: String?, skillLevel: String?, signature: String?) {
        viewModelScope.launch {
            userRepository.register(account, password, nickname, skillLevel, signature).collect { result ->
                _authState.value = result
            }
        }
    }

    fun clearAuthState() {
        _authState.value = null
    }
}
