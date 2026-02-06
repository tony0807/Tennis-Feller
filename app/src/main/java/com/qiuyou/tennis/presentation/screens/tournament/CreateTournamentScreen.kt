package com.qiuyou.tennis.presentation.screens.tournament

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
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
fun CreateTournamentScreen(
    navController: NavHostController,
    viewModel: CreateActivityViewModel = hiltViewModel()
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val venues by viewModel.venues.collectAsState()
    val createResult by viewModel.createResult.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var selectedVenue by remember { mutableStateOf<VenueEntity?>(null) }
    var customLocation by remember { mutableStateOf("") }
    var participants by remember { mutableStateOf(16) }
    var participantsMenuExpanded by remember { mutableStateOf(false) }
    var fee by remember { mutableStateOf("100") }
    var selectedSkillLevel by remember { mutableStateOf("3.0") }
    var tournamentType by remember { mutableStateOf("单打") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    // Date & Duration states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var selectedStartDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedEndDateMillis by remember { mutableStateOf(System.currentTimeMillis() + 86400000) } // Default +1 day
    var selectedDurationHours by remember { mutableIntStateOf(6) } // 默认6小时
    var durationMenuExpanded by remember { mutableStateOf(false) }
    var isMultiDay by remember { mutableStateOf(false) }
    
    val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedStartDateMillis)
    val endDatePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedEndDateMillis)
    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    
    var startDateString by remember { mutableStateOf(sdfDate.format(Date(selectedStartDateMillis))) }
    var endDateString by remember { mutableStateOf(sdfDate.format(Date(selectedEndDateMillis))) }
    
    LaunchedEffect(selectedStartDateMillis) {
        startDateString = sdfDate.format(Date(selectedStartDateMillis))
    }
    LaunchedEffect(selectedEndDateMillis) {
        endDateString = sdfDate.format(Date(selectedEndDateMillis))
    }
    
    // Duration options for tournaments (in hours)
    val durationOptions = listOf(4, 6, 8, 10, 12, 24) // 4h to all-day
    fun formatDurationHours(hours: Int): String = if (hours >= 24) "全天" else "${hours}小时"
    
    // Participant options
    val participantOptions = listOf(8, 16, 32, 64, 128, 256)
    
    val skillLevels = listOf("不限", "2.5", "3.0", "3.5", "4.0", "Open")
    val tournamentTypes = listOf("单打", "双打", "混双", "团体赛")
    
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

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerState.selectedDateMillis?.let { 
                        selectedStartDateMillis = it
                        // If end date is before start date, update end date
                        if (selectedEndDateMillis < it) {
                            selectedEndDateMillis = it + 86400000
                        }
                    }
                    showStartDatePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }
    
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerState.selectedDateMillis?.let { 
                        selectedEndDateMillis = it
                    }
                    showEndDatePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发布赛事") },
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("赛事名称") },
                placeholder = { Text("例如：2026春季网球公开赛") },
                modifier = Modifier.fillMaxWidth()
            )

            // City Selection
            var cityMenuExpanded by remember { mutableStateOf(false) }
            val cities = listOf("北京市", "上海市", "广州市", "深圳市", "杭州市", "成都市")
            Box {
                OutlinedTextField(
                    value = selectedCity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("举办城市") },
                    trailingIcon = { 
                        IconButton(onClick = { cityMenuExpanded = true }) {
                            Icon(Icons.Default.LocationOn, contentDescription = "选择城市")
                        } 
                    },
                    modifier = Modifier.fillMaxWidth().clickable { cityMenuExpanded = true }
                )
                DropdownMenu(expanded = cityMenuExpanded, onDismissRequest = { cityMenuExpanded = false }) {
                    cities.forEach { city ->
                        DropdownMenuItem(text = { Text(city) }, onClick = {
                            viewModel.selectCity(city)
                            cityMenuExpanded = false
                        })
                    }
                }
            }
            
            // Venue selection
            Text("举办场馆", style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(venues) { venue ->
                    FilterChip(
                        selected = selectedVenue?.id == venue.id,
                        onClick = { selectedVenue = venue; customLocation = venue.name },
                        label = { Text(venue.name) }
                    )
                }
            }
            
            if (selectedVenue == null) {
                OutlinedTextField(
                    value = customLocation,
                    onValueChange = { customLocation = it },
                    label = { Text("手动输入场馆") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Date & Duration
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Multi-day toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("赛事时间设置", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("跨天赛事", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isMultiDay,
                        onCheckedChange = { isMultiDay = it }
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Start Date
                    OutlinedTextField(
                        value = startDateString,
                        onValueChange = { 
                            startDateString = it 
                            try {
                                val parsed = sdfDate.parse(it)
                                if (parsed != null) selectedStartDateMillis = parsed.time
                            } catch (e: Exception) {}
                        },
                        label = { Text("开始日期") },
                        placeholder = { Text("yyyy-MM-dd") },
                        trailingIcon = {
                            IconButton(onClick = { showStartDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "选择日期")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (isMultiDay) {
                        // End Date for multi-day
                        OutlinedTextField(
                            value = endDateString,
                            onValueChange = {
                                endDateString = it
                                try {
                                    val parsed = sdfDate.parse(it)
                                    if (parsed != null) selectedEndDateMillis = parsed.time
                                } catch (e: Exception) {}
                            },
                            label = { Text("结束日期") },
                            placeholder = { Text("yyyy-MM-dd") },
                            trailingIcon = {
                                IconButton(onClick = { showEndDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "选择日期")
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Duration for single day
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = selectedDurationHours.toString(),
                                onValueChange = { 
                                    selectedDurationHours = it.toIntOrNull() ?: selectedDurationHours
                                },
                                label = { Text("时长(小时)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            // Participants and Fee
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = "${participants}人",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("名额限制") },
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
                    label = { Text("报名费") },
                    prefix = { Text("¥") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Skill level
            Column {
                Text("水平限制", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    skillLevels.forEach { level ->
                        FilterChip(selected = selectedSkillLevel == level, onClick = { selectedSkillLevel = level }, label = { Text(level) })
                    }
                }
            }
            
            // Tournament type
            Column {
                Text("赛事项目", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tournamentTypes.forEach { type ->
                        FilterChip(selected = tournamentType == type, onClick = { tournamentType = type }, label = { Text(type) })
                    }
                }
            }
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("赛事规则及说明") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )
            
            // Image picker for tournament description
            ImagePicker(
                images = selectedImages,
                onImagesChanged = { selectedImages = it },
                maxImages = 9
            )
            
            Button(
                onClick = { 
                    viewModel.createActivity(
                        type = ActivityType.TOURNAMENT,
                        title = title.ifEmpty { "${customLocation.ifEmpty { "网球赛事" }} - $tournamentType" },
                        venueId = selectedVenue?.id,
                        customLocation = customLocation,
                        startTime = selectedStartDateMillis,
                        endTime = if (isMultiDay) selectedEndDateMillis else selectedStartDateMillis + (selectedDurationHours * 3600000L),
                        maxParticipants = participants,
                        fee = fee.toDoubleOrNull() ?: 0.0,
                        skillLevel = selectedSkillLevel,
                        activityType = tournamentType,
                        description = description,
                        creatorParticipates = false, // Creator usually organizes tournament, not competes as a player occupying a slot
                        imageUris = selectedImages
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = createResult !is Result.Loading && title.isNotEmpty() && (customLocation.isNotEmpty() || selectedVenue != null)
            ) {
                if (createResult is Result.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("发布赛事")
                }
            }
        }
    }
}
