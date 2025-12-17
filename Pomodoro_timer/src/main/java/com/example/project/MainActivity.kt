package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.screen.CompleteScreen
import com.example.project.screen.RecordScreen
import com.example.project.screen.SettingsScreen
import com.example.project.screen.TimerScreen
import com.example.project.ui.theme._0910_3Theme
import com.example.project.viewmodel.TimerViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _0910_3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PomodoroApp()
                }
            }
        }
    }
}

@Composable
fun PomodoroApp(viewModel: TimerViewModel = viewModel()) {
    var currentTab by remember { mutableStateOf("timer") }
    var isTimerRunning by remember { mutableStateOf(false) }

    val focusTime by viewModel.focusTime.collectAsState(initial = 25)
    val breakTime by viewModel.breakTime.collectAsState(initial = 5)
    val setCount by viewModel.setCount.collectAsState(initial = 4)
    val alarmType by viewModel.alarmType.collectAsState(initial = "both")

    val timeLeft by viewModel.timeLeft.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val isWorkTime by viewModel.isWorkTime.collectAsState()
    val currentSet by viewModel.currentSet.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()

    val records by viewModel.allRecords.collectAsState(initial = emptyList())

    // 타이머 동작
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            viewModel.tick()
        }
    }

    // 시간이 0이 되면 다음 단계로 + 알림
    LaunchedEffect(timeLeft) {
        if (timeLeft == 0 && isRunning) {
            viewModel.playAlarm(alarmType)
            viewModel.nextPhase()
        }
    }

    // 타이머 실행 중이면 탭 이동 막기
    LaunchedEffect(isRunning) {
        isTimerRunning = isRunning
    }

    // 완료 화면
    if (isFinished) {
        CompleteScreen(
            totalSets = setCount,
            focusTime = focusTime,
            records = records,
            onHomeClick = {
                viewModel.reset()
            },
            onRecordClick = {
                viewModel.reset()
                currentTab = "record"
            },
            onApplyRecommendation = { recommendedTime ->
                viewModel.saveFocusTime(recommendedTime)
                viewModel.reset()
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "기록") },
                        label = { Text("기록") },
                        selected = currentTab == "record",
                        onClick = { if (!isTimerRunning) currentTab = "record" },
                        enabled = !isTimerRunning
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "타이머") },
                        label = { Text("타이머") },
                        selected = currentTab == "timer",
                        onClick = { if (!isTimerRunning) currentTab = "timer" },
                        enabled = !isTimerRunning
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "설정") },
                        label = { Text("설정") },
                        selected = currentTab == "settings",
                        onClick = { if (!isTimerRunning) currentTab = "settings" },
                        enabled = !isTimerRunning
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentTab) {
                    "record" -> RecordScreen(
                        records = records,
                        onBackClick = { currentTab = "timer" }
                    )

                    "timer" -> {
                        if (isRunning) {
                            TimerScreen(
                                timeLeft = timeLeft,
                                isRunning = isRunning,
                                isWorkTime = isWorkTime,
                                currentSet = currentSet,
                                totalSets = setCount,
                                focusTime = focusTime,
                                breakTime = breakTime,
                                onTogglePause = { viewModel.togglePause() },
                                onReset = { viewModel.reset() },
                                onSkipBreak = { viewModel.skipBreak() }
                            )
                        } else {
                            TimerSetupScreen(
                                focusTime = focusTime,
                                breakTime = breakTime,
                                setCount = setCount,
                                onFocusTimeChange = { viewModel.saveFocusTime(it) },
                                onBreakTimeChange = { viewModel.saveBreakTime(it) },
                                onSetCountChange = { viewModel.saveSetCount(it) },
                                onStartClick = { viewModel.startTimer() }
                            )
                        }
                    }

                    "settings" -> AlarmSettingsScreen(
                        alarmType = alarmType,
                        onAlarmTypeChange = { viewModel.saveAlarmType(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimerSetupScreen(
    focusTime: Int,
    breakTime: Int,
    setCount: Int,
    onFocusTimeChange: (Int) -> Unit,
    onBreakTimeChange: (Int) -> Unit,
    onSetCountChange: (Int) -> Unit,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🍅 뽀모도로 타이머",
            fontSize = 28.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 집중 시간
        SettingCard(
            title = "집중 시간",
            value = focusTime,
            unit = "분",
            onDecrease = { if (focusTime > 5) onFocusTimeChange(focusTime - 5) },
            onIncrease = { if (focusTime < 120) onFocusTimeChange(focusTime + 5) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 휴식 시간
        SettingCard(
            title = "휴식 시간",
            value = breakTime,
            unit = "분",
            onDecrease = { if (breakTime > 5) onBreakTimeChange(breakTime - 5) },
            onIncrease = { if (breakTime < 60) onBreakTimeChange(breakTime + 5) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 세트 수
        SettingCard(
            title = "세트 수",
            value = setCount,
            unit = "세트",
            onDecrease = { if (setCount > 1) onSetCountChange(setCount - 1) },
            onIncrease = { if (setCount < 10) onSetCountChange(setCount + 1) }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 시작 버튼
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE88888))
        ) {
            Text("시작하기", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}

@Composable
fun SettingCard(
    title: String,
    value: Int,
    unit: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 18.sp)

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                FilledIconButton(
                    onClick = onDecrease,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE88888))
                ) {
                    Text("-", fontSize = 20.sp, color = Color.White)
                }

                Text(
                    text = "$value $unit",
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                FilledIconButton(
                    onClick = onIncrease,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE88888 ))
                ) {
                    Text("+", fontSize = 20.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AlarmSettingsScreen(
    alarmType: String,
    onAlarmTypeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "⚙️ 설정",
            fontSize = 28.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "알림 설정",
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                val options = listOf(
                    "sound" to "🔔 소리",
                    "none" to "🔇 무음"
                )

                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = alarmType == value,
                            onClick = { onAlarmTypeChange(value) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}