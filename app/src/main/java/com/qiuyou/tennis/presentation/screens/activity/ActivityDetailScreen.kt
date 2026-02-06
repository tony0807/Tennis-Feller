package com.qiuyou.tennis.presentation.screens.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qiuyou.tennis.R
import com.qiuyou.tennis.data.model.ActivityEntity
import com.qiuyou.tennis.data.model.ActivityType
import com.qiuyou.tennis.data.model.UserEntity
import com.qiuyou.tennis.presentation.viewmodel.ActivityDetailUiState
import com.qiuyou.tennis.presentation.viewmodel.ActivityDetailViewModel
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.presentation.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActivityDetailScreen(
    activityId: String,
    navController: NavHostController,
    viewModel: ActivityDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val registrationResult by viewModel.registrationResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(activityId) {
        viewModel.loadActivity(activityId)
    }
    
    LaunchedEffect(registrationResult) {
        when (val result = registrationResult) {
            is Result.Success -> {
                snackbarHostState.showSnackbar("操作成功")
                viewModel.clearRegistrationResult()
                // Redirect back to list after successful join as requested
                navController.navigateUp()
            }
            is Result.Error -> {
                if (result.message == "请先登录") {
                    navController.navigate(Screen.Login.route)
                } else {
                    snackbarHostState.showSnackbar(result.message)
                }
                viewModel.clearRegistrationResult()
            }
            else -> {}
        }
    }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val title = when (uiState) {
                        is ActivityDetailUiState.Success -> {
                            val activity = (uiState as ActivityDetailUiState.Success).activity
                            when (activity.type) {
                                ActivityType.PLAY -> "约球详情"
                                ActivityType.TOURNAMENT -> "赛事详情"
                                ActivityType.COURSE -> "课程详情"
                            }
                        }
                        else -> "活动详情"
                    }
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState is ActivityDetailUiState.Success) {
                        val activity = (uiState as ActivityDetailUiState.Success).activity
                        IconButton(onClick = { 
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "分享活动")
                                putExtra(android.content.Intent.EXTRA_TEXT, "我在球友APP发现了一个很棒的活动：${activity.title}\n时间：${java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.CHINA).format(java.util.Date(activity.startTime))}\n地点：${activity.customLocation ?: "未知地点"}\n快来报名吧！")
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "分享活动到"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share))
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is ActivityDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ActivityDetailUiState.Success -> {
                ActivityDetailContent(
                    activity = state.activity,
                    participants = state.participants,
                    isRegistered = state.isRegistered,
                    isCreator = state.isCreator,
                    paddingValues = paddingValues,
                    onJoinClick = { viewModel.registerActivity(activityId) },
                    onCancelClick = { viewModel.cancelRegistration(activityId) },
                    onDeleteClick = { viewModel.deleteActivity(activityId) }
                )
            }
            is ActivityDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActivityDetailContent(
    activity: ActivityEntity,
    participants: List<UserEntity>,
    isRegistered: Boolean,
    isCreator: Boolean,
    paddingValues: PaddingValues,
    onJoinClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = activity.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        // Time and Date Card
        val sdfDate = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
        val sdfTime = SimpleDateFormat("HH:mm", Locale.CHINA)
        val dateStr = sdfDate.format(Date(activity.startTime))
        val timeStr = "${sdfTime.format(Date(activity.startTime))}-${sdfTime.format(Date(activity.endTime))}"
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // Location Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO: Open map */ }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = activity.customLocation ?: "默认场地",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "点击查看位置",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Activity Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoItem(
                label = "水平要求",
                value = activity.skillLevel,
                modifier = Modifier.weight(1f)
            )
            InfoItem(
                label = "活动类型",
                value = activity.activityType,
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoItem(
                label = "参与人数",
                value = "${activity.currentParticipants}/${activity.maxParticipants}人",
                modifier = Modifier.weight(1f)
            )
            InfoItem(
                label = "费用",
                value = "¥${activity.fee}/人",
                modifier = Modifier.weight(1f)
            )
        }
        
        // Participant list
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "已报名 (${participants.size}/${activity.maxParticipants})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (participants.isEmpty()) {
                Text(
                    text = "暂无报名人员",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(participants) { user ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = user.nickname,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Text(
                                text = user.nickname,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                modifier = Modifier.width(60.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        
        // Description
        if (!activity.description.isNullOrEmpty()) {
            Column {
                Text(
                    text = "活动说明",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Images display
                if (!activity.images.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    val imageUrls = try {
                        activity.images.removePrefix("[").removeSuffix("]")
                            .split(",")
                            .map { it.trim().removeSurrounding("\"") }
                            .filter { it.isNotEmpty() }
                    } catch (e: Exception) {
                        emptyList<String>()
                    }

                    if (imageUrls.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            maxItemsInEachRow = 3
                        ) {
                            imageUrls.forEach { url ->
                                coil.compose.AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(MaterialTheme.shapes.small),
                                    contentScale = ContentScale.Crop,
                                    placeholder = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery),
                                    error = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_report_image)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Action Buttons
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (isRegistered) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.cancel_registration))
                }
            } else {
                Button(
                    onClick = onJoinClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = activity.currentParticipants < activity.maxParticipants
                ) {
                    Text(if (activity.currentParticipants < activity.maxParticipants) "立即报名" else "已满员")
                }
            }
            
            // Delete/Cancel Activity button for Creator
            if (isCreator) {
                var showDeleteDialog by remember { mutableStateOf(false) }
                
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("取消活动 (解散)")
                }
                
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("确认取消活动？") },
                        text = { Text("取消后活动将被删除，且不可恢复。") },
                        confirmButton = {
                            TextButton(onClick = {
                                onDeleteClick()
                                showDeleteDialog = false
                            }) { Text("确认取消", color = MaterialTheme.colorScheme.error) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) { Text("暂不") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
