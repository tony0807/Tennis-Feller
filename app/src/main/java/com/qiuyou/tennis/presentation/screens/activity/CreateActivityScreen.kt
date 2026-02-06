package com.qiuyou.tennis.presentation.screens.activity

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qiuyou.tennis.R
import com.qiuyou.tennis.presentation.components.ImagePicker
import com.qiuyou.tennis.presentation.navigation.Screen
import com.qiuyou.tennis.data.model.ActivityType
import com.qiuyou.tennis.data.model.VenueEntity
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.presentation.viewmodel.CreateActivityViewModel
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateActivityScreen(
    activityType: String,
    navController: NavHostController,
    viewModel: CreateActivityViewModel = hiltViewModel()
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val venues by viewModel.venues.collectAsState()
    val createResult by viewModel.createResult.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var selectedVenue by remember { mutableStateOf<VenueEntity?>(null) }
    var customLocation by remember { mutableStateOf("") }
    var participants by remember { mutableStateOf(4) }
    var participantsMenuExpanded by remember { mutableStateOf(false) }
    var fee by remember { mutableStateOf("") }
    var selectedSkillLevel by remember { mutableStateOf("3.0") }
    var selectedActivityType by remember { mutableStateOf("娱乐双打") }
    var description by remember { mutableStateOf("") }
    var creatorParticipates by remember { mutableStateOf(true) } // 发布者是否参加
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    // Date & Time states
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedStartTimeHour by remember { mutableIntStateOf(10) }
    var selectedStartTimeMinute by remember { mutableIntStateOf(0) }
    var selectedDuration by remember { mutableIntStateOf(120) } // 默认2小时（120分钟）
    var durationMenuExpanded by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    val startTimePickerState = rememberTimePickerState(initialHour = selectedStartTimeHour, initialMinute = selectedStartTimeMinute)
    
    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    var dateString by remember { mutableStateOf(sdfDate.format(Date(selectedDateMillis))) }
    var startTimeString by remember { mutableStateOf(String.format("%02d:%02d", selectedStartTimeHour, selectedStartTimeMinute)) }
    
    // Sync with picker selections
    LaunchedEffect(selectedDateMillis) {
        dateString = sdfDate.format(Date(selectedDateMillis))
    }
    LaunchedEffect(selectedStartTimeHour, selectedStartTimeMinute) {
        startTimeString = String.format("%02d:%02d", selectedStartTimeHour, selectedStartTimeMinute)
    }
    
    // Calculate end time based on start time and duration
    val endHour = (selectedStartTimeHour + selectedDuration / 60) % 24
    val endMinute = (selectedStartTimeMinute + selectedDuration % 60) % 60
    val endTimeString = String.format("%02d:%02d", endHour, endMinute)
    val timeRangeString = "$startTimeString - $endTimeString"
    
    // Duration options
    val durationOptions = listOf(60, 90, 120, 150, 180, 240) // 1h, 1.5h, 2h, 2.5h, 3h, 4h
    fun formatDuration(minutes: Int): String = when {
        minutes < 60 -> "${minutes}分钟"
        minutes % 60 == 0 -> "${minutes / 60}小时"
        else -> "${minutes / 60}小时${minutes % 60}分钟"
    }
    
    // Time slot options (30-minute intervals)
    val timeSlots = (6..22).flatMap { hour ->
        listOf(0, 30).map { minute -> Pair(hour, minute) }
    }
    
    // Participant options
    val participantOptions = listOf(2, 3, 4, 5, 6, 7, 8, 10, 12, 16, 20)
    
    val skillLevels = listOf("2.5以下", "2.5", "3.0", "3.0+", "3.5", "4.0", "4.0+")
    val activityTypes = listOf("单打", "双打", "娱乐双打", "拉球", "练习", "混双")
    
    LaunchedEffect(createResult) {
        when (createResult) {
            is Result.Success -> {
                viewModel.clearCreateResult()
                navController.navigateUp()
            }
            is Result.Error -> {
                val error = createResult as Result.Error
                if (error.message == "请先登录") {
                    navController.navigate(Screen.Login.route)
                }
            }
            else -> {}
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDateMillis = it
                    }
                    showDatePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showStartTimePicker) {
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedStartTimeHour = startTimePickerState.hour
                    selectedStartTimeMinute = startTimePickerState.minute
                    showStartTimePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) { Text("取消") }
            },
            title = { Text("选择开始时间") },
            text = {
                TimePicker(state = startTimePickerState)
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when (activityType) {
                            "PLAY" -> "发布约球"
                            "COURSE" -> "发布课程"
                            "TOURNAMENT" -> "发布赛事"
                            else -> "发布活动"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // City Selection Dropdown
            var cityMenuExpanded by remember { mutableStateOf(false) }
            val cities = listOf("北京市", "上海市", "广州市", "深圳市", "杭州市", "成都市")
            
            Box {
                OutlinedTextField(
                    value = selectedCity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("城市") },
                    trailingIcon = { 
                        IconButton(onClick = { cityMenuExpanded = true }) {
                            Icon(Icons.Default.LocationOn, contentDescription = "选择城市")
                        } 
                    },
                    modifier = Modifier.fillMaxWidth().clickable { cityMenuExpanded = true }
                )
                DropdownMenu(
                    expanded = cityMenuExpanded,
                    onDismissRequest = { cityMenuExpanded = false }
                ) {
                    cities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                viewModel.selectCity(city)
                                cityMenuExpanded = false
                            }
                        )
                    }
                }
            }
            
            // Venue selection (horizontal list)
            Text("选择球场", style = MaterialTheme.typography.titleSmall)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(venues) { venue ->
                    FilterChip(
                        selected = selectedVenue?.id == venue.id,
                        onClick = { 
                            selectedVenue = venue
                            customLocation = venue.name
                        },
                        label = { Text(venue.name) }
                    )
                }
            }
            
            if (selectedVenue == null) {
                OutlinedTextField(
                    value = customLocation,
                    onValueChange = { customLocation = it },
                    label = { Text("手动输入地点") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Date picker
            OutlinedTextField(
                value = dateString,
                onValueChange = { 
                    dateString = it 
                    try {
                        val parsedDate = sdfDate.parse(it)
                        if (parsedDate != null) {
                            selectedDateMillis = parsedDate.time
                        }
                    } catch (e: Exception) {}
                },
                label = { Text("日期") },
                placeholder = { Text("yyyy-MM-dd") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "选择日期")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Time range selection (start time + duration)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start time picker
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = startTimeString,
                        onValueChange = {
                            startTimeString = it
                            try {
                                val parts = it.split(":", "：")
                                if (parts.size == 2) {
                                    val h = parts[0].trim().toInt()
                                    val m = parts[1].trim().toInt()
                                    if (h in 0..23 && m in 0..59) {
                                        selectedStartTimeHour = h
                                        selectedStartTimeMinute = m
                                    }
                                }
                            } catch (e: Exception) {}
                        },
                        label = { Text("开始时间") },
                        placeholder = { Text("HH:mm") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Duration dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = formatDuration(selectedDuration),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("时长") },
                        modifier = Modifier.fillMaxWidth().clickable { durationMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = durationMenuExpanded,
                        onDismissRequest = { durationMenuExpanded = false }
                    ) {
                        durationOptions.forEach { duration ->
                            DropdownMenuItem(
                                text = { Text(formatDuration(duration)) },
                                onClick = {
                                    selectedDuration = duration
                                    durationMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Display calculated time range
            Text(
                text = "时间段：$timeRangeString",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Activity Title (Auto-fill suggestion)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("活动标题") },
                placeholder = { Text("例如：朝阳公园拉球") },
                modifier = Modifier.fillMaxWidth()
            )

            // Participants and Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Participant count dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = "${participants}人",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("人数") },
                        modifier = Modifier.fillMaxWidth().clickable { participantsMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = participantsMenuExpanded,
                        onDismissRequest = { participantsMenuExpanded = false }
                    ) {
                        participantOptions.forEach { count ->
                            DropdownMenuItem(
                                text = { Text("${count}人") },
                                onClick = {
                                    participants = count
                                    participantsMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = fee,
                    onValueChange = { fee = it },
                    label = { Text("费用") },
                    placeholder = { Text("0") },
                    prefix = { Text("¥") },
                    suffix = { Text("/人") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Creator participates option
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = creatorParticipates,
                    onCheckedChange = { creatorParticipates = it }
                )
                Text(
                    text = "我也参加",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (creatorParticipates) {
                    Text(
                        text = "（还需 ${participants - 1} 人）",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Skill level selection
            Column {
                Text(
                    text = stringResource(R.string.skill_level),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
            
            // Activity type selection
            Column {
                Text(
                    text = stringResource(R.string.activity_type),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activityTypes.forEach { type ->
                        FilterChip(
                            selected = selectedActivityType == type,
                            onClick = { selectedActivityType = type },
                            label = { Text(type) }
                        )
                    }
                }
            }
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                placeholder = { Text("活动说明（选填）") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            // Image picker for activity description
            ImagePicker(
                images = selectedImages,
                onImagesChanged = { selectedImages = it },
                maxImages = 9
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit button
            Button(
                onClick = { 
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = selectedDateMillis
                    calendar.set(Calendar.HOUR_OF_DAY, selectedStartTimeHour)
                    calendar.set(Calendar.MINUTE, selectedStartTimeMinute)
                    val startTime = calendar.timeInMillis
                    calendar.add(Calendar.MINUTE, selectedDuration)
                    val endTime = calendar.timeInMillis
                    
                    viewModel.createActivity(
                        type = when (activityType) {
                            "PLAY" -> ActivityType.PLAY
                            "COURSE" -> ActivityType.COURSE
                            "TOURNAMENT" -> ActivityType.TOURNAMENT
                            else -> ActivityType.PLAY
                        },
                        title = title.ifEmpty { "${customLocation.ifEmpty { "网球活动" }} - $selectedActivityType" },
                        venueId = selectedVenue?.id,
                        customLocation = customLocation,
                        startTime = startTime,
                        endTime = endTime,
                        maxParticipants = participants,
                        fee = fee.toDoubleOrNull() ?: 0.0,
                        skillLevel = selectedSkillLevel,
                        activityType = selectedActivityType,
                        description = description,
                        creatorParticipates = creatorParticipates,
                        imageUris = selectedImages
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = createResult !is Result.Loading && (customLocation.isNotEmpty() || selectedVenue != null)
            ) {
                if (createResult is Result.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.publish))
                }
            }
            
            if (createResult is Result.Error) {
                Text(
                    text = (createResult as Result.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
