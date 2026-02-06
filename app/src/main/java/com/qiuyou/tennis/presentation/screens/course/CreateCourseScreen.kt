package com.qiuyou.tennis.presentation.screens.course

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qiuyou.tennis.presentation.navigation.Screen
import com.qiuyou.tennis.data.model.ActivityType
import com.qiuyou.tennis.data.model.VenueEntity
import com.qiuyou.tennis.data.repository.Result
import com.qiuyou.tennis.presentation.viewmodel.CreateActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateCourseScreen(
    navController: NavHostController,
    viewModel: CreateActivityViewModel = hiltViewModel()
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val venues by viewModel.venues.collectAsState()
    val createResult by viewModel.createResult.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var selectedVenue by remember { mutableStateOf<VenueEntity?>(null) }
    var customLocation by remember { mutableStateOf("") }
    var participants by remember { mutableStateOf("4") }
    var fee by remember { mutableStateOf("200") }
    var selectedSkillLevel by remember { mutableStateOf("3.0") }
    var courseType by remember { mutableStateOf("进阶私教课") }
    var description by remember { mutableStateOf("") }
    
    // Date & Time states
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    var dateString by remember { mutableStateOf(sdfDate.format(Date(selectedDateMillis))) }
    
    LaunchedEffect(selectedDateMillis) {
        dateString = sdfDate.format(Date(selectedDateMillis))
    }
    
    val skillLevels = listOf("入门", "1.5-2.5", "3.0-3.5", "4.0+")
    val courseTypes = listOf("私教课", "小班课", "公开课", "体能训练")
    
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
                    datePickerState.selectedDateMillis?.let { selectedDateMillis = it }
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发布课程") },
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
                label = { Text("课程名称") },
                placeholder = { Text("例如：张教练入门小班课") },
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
                    label = { Text("授课城市") },
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
            Text("授课场地", style = MaterialTheme.typography.titleSmall)
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
                    label = { Text("手动输入地点") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Date
            OutlinedTextField(
                value = dateString,
                onValueChange = { 
                    dateString = it 
                    try {
                        val parsed = sdfDate.parse(it)
                        if (parsed != null) selectedDateMillis = parsed.time
                    } catch (e: Exception) {}
                },
                label = { Text("开课日期") },
                placeholder = { Text("yyyy-MM-dd") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "选择日期")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Participants and Fee
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = participants,
                    onValueChange = { if (it.all { char -> char.isDigit() }) participants = it },
                    label = { Text("名额限制") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = fee,
                    onValueChange = { fee = it },
                    label = { Text("课程费用") },
                    prefix = { Text("¥") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Skill level
            Column {
                Text("适合水平", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    skillLevels.forEach { level ->
                        FilterChip(selected = selectedSkillLevel == level, onClick = { selectedSkillLevel = level }, label = { Text(level) })
                    }
                }
            }
            
            // Course type
            Column {
                Text("课程类型", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    courseTypes.forEach { type ->
                        FilterChip(selected = courseType == type, onClick = { courseType = type }, label = { Text(type) })
                    }
                }
            }
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("课程大纲及说明") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )
            
            Button(
                onClick = { 
                    viewModel.createActivity(
                        type = ActivityType.COURSE,
                        title = title.ifEmpty { "${customLocation.ifEmpty { "网球课程" }} - $courseType" },
                        venueId = selectedVenue?.id,
                        customLocation = customLocation,
                        startTime = selectedDateMillis,
                        endTime = selectedDateMillis + 3600000, // Default 1 hour
                        maxParticipants = participants.toIntOrNull() ?: 4,
                        fee = fee.toDoubleOrNull() ?: 0.0,
                        skillLevel = selectedSkillLevel,
                        activityType = courseType,
                        description = description
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = createResult !is Result.Loading && title.isNotEmpty() && (customLocation.isNotEmpty() || selectedVenue != null)
            ) {
                if (createResult is Result.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("发布课程")
                }
            }
        }
    }
}
