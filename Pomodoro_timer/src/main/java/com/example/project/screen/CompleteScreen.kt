package com.example.project.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.data.StudyRecord

@Composable
fun CompleteScreen(
    totalSets: Int,
    focusTime: Int,
    records: List<StudyRecord>,
    onHomeClick: () -> Unit,
    onRecordClick: () -> Unit,
    onApplyRecommendation: (Int) -> Unit
) {
    val totalMinutes = totalSets * focusTime

    // 패턴 분석해서 추천 시간 계산
    val recommendation = analyzePattern(records, focusTime, totalSets)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "수고했어요!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 오늘 기록 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "오늘의 기록",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🍅 $totalSets 세트 완료!",
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "⏱️ 총 ${totalMinutes}분 집중",
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 추천 카드
        if (recommendation != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💡 집중 패턴 분석",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = recommendation.message,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )

                    if (recommendation.suggestedTime != focusTime) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onHomeClick
                            ) {
                                Text("유지하기", fontSize = 14.sp)
                            }

                            Button(
                                onClick = { onApplyRecommendation(recommendation.suggestedTime) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Text("${recommendation.suggestedTime}분 적용", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onRecordClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("📊 전체 기록 보기", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onHomeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("🏠 처음으로", fontSize = 16.sp)
        }
    }
}

data class Recommendation(
    val message: String,
    val suggestedTime: Int
)

fun analyzePattern(records: List<StudyRecord>, currentFocusTime: Int, completedSets: Int): Recommendation? {
    if (records.isEmpty()) {
        return Recommendation(
            message = "첫 번째 기록이에요! 꾸준히 하면 맞춤 추천을 해드릴게요 🌱",
            suggestedTime = currentFocusTime
        )
    }

    // 최근 7일 기록 분석
    val recentRecords = records.take(7)
    val avgSetsPerDay = recentRecords.map { it.completedSets }.average()
    val avgMinutesPerDay = recentRecords.map { it.focusMinutes }.average()

    return when {
        // 세트 완료율 높고, 현재 시간이 30분 미만이면 늘리기 추천
        avgSetsPerDay >= 3 && currentFocusTime < 30 -> {
            Recommendation(
                message = "최근 집중력이 좋아요! 💪\n${currentFocusTime}분 → ${currentFocusTime + 5}분으로 도전해볼까요?",
                suggestedTime = currentFocusTime + 5
            )
        }
        // 세트 완료율 높고, 꾸준히 하고 있으면 격려
        avgSetsPerDay >= 2 -> {
            Recommendation(
                message = "꾸준히 잘하고 있어요! 🔥\n현재 ${currentFocusTime}분이 딱 맞는 것 같아요.",
                suggestedTime = currentFocusTime
            )
        }
        // 기록이 적으면 시간 줄이기 추천
            avgSetsPerDay < 2 && currentFocusTime > 15 -> {
            Recommendation(
                message = "조금 짧게 시작해볼까요? 🌿\n${currentFocusTime}분 → ${currentFocusTime - 5}분으로 부담 없이!",
                suggestedTime = currentFocusTime - 5
            )
        }
        else -> {
            Recommendation(
                message = "오늘도 수고했어요! 🍅\n이 페이스 유지해봐요!",
                suggestedTime = currentFocusTime
            )
        }
    }
}