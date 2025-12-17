package com.example.project.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    focusTime: Int,
    breakTime: Int,
    setCount: Int,
    alarmType: String,
    onFocusTimeChange: (Int) -> Unit,
    onBreakTimeChange: (Int) -> Unit,
    onSetCountChange: (Int) -> Unit,
    onAlarmTypeChange: (String) -> Unit,
    onStartClick: () -> Unit,
    onRecordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🍅 뽀모도로 타이머 🍅",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 집중 시간 설정
        SettingItem(
            title = "집중 시간",
            value = focusTime,
            unit = "분",
            onDecrease = { if (focusTime > 5) onFocusTimeChange(focusTime - 5) },
            onIncrease = { if (focusTime < 120) onFocusTimeChange(focusTime + 5) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 휴식 시간 설정
        SettingItem(
            title = "휴식 시간",
            value = breakTime,
            unit = "분",
            onDecrease = { if (breakTime > 5) onBreakTimeChange(breakTime - 5) },
            onIncrease = { if (breakTime < 60) onBreakTimeChange(breakTime + 5) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 세트 수 설정
        SettingItem(
            title = "세트 수",
            value = setCount,
            unit = "세트",
            onDecrease = { if (setCount > 1) onSetCountChange(setCount - 1) },
            onIncrease = { if (setCount < 10) onSetCountChange(setCount + 1) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 알림 설정
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "알림 설정",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val options = listOf(
                    "sound" to "🔔 소리",
                    "none" to "🔇 무음"
                )

                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = alarmType == value,
                                onClick = { onAlarmTypeChange(value) }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
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

        Spacer(modifier = Modifier.height(32.dp))

        // 시작 버튼
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("시작하기", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 기록 보기 버튼
        OutlinedButton(
            onClick = onRecordClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("📊 공부 기록 보기", fontSize = 16.sp)
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    value: Int,
    unit: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 18.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledIconButton(onClick = onDecrease) {
                    Text("-", fontSize = 20.sp)
                }

                Text(
                    text = "$value $unit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                FilledIconButton(onClick = onIncrease) {
                    Text("+", fontSize = 20.sp)
                }
            }
        }
    }
}