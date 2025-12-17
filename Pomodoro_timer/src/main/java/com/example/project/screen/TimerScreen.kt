package com.example.project.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.material3.LocalTextStyle

@Composable
fun TimerScreen(
    timeLeft: Int,
    isRunning: Boolean,
    isWorkTime: Boolean,
    currentSet: Int,
    totalSets: Int,
    focusTime: Int,
    breakTime: Int,
    onTogglePause: () -> Unit,
    onReset: () -> Unit,
    onSkipBreak: () -> Unit  // 휴식 건너뛰기 추가
) {
    val totalTime = if (isWorkTime) focusTime * 60 else breakTime * 60
    val progress = if (totalTime > 0) timeLeft.toFloat() / totalTime else 0f

    val timerColor = if (isWorkTime) Color(0xFFC75050) else Color(0xFF9CB686)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 상태 표시
        Text(
            text = if (isWorkTime) "🍅 집중 시간" else "☕ 휴식 시간",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 세트 표시
        Text(
            text = "$currentSet / $totalSets 세트",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 원형 타이머
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(250.dp)) {
                // 배경 원 (연한 색)
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = true,
                    style = Fill
                )
                // 진행 원 (채워지는 부분)
                drawArc(
                    color = timerColor,
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = true,
                    style = Fill
                )
            }

            // 시간 표시
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(timeLeft),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // 버튼들
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 일시정지/재개 버튼
            Button(
                onClick = onTogglePause,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFFF59E0B) else timerColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isRunning) "일시정지" else "계속하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 리셋 버튼
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "처음으로",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 휴식 시간일 때만 건너뛰기 버튼 표시
        if (!isWorkTime) {
            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onSkipBreak,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "휴식 건너뛰기 ⏭️",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}