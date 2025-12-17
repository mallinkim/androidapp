package com.example.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.project.data.StudyRecord
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun RecordScreen(
    records: List<StudyRecord>,
    onBackClick: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedRecord by remember { mutableStateOf<StudyRecord?>(null) }

    val recordMap = records.associateBy { it.date }

    // 날짜 클릭시 팝업
    if (selectedRecord != null) {
        Dialog(onDismissRequest = { selectedRecord = null }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📅 ${selectedRecord!!.date}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🍅 ${selectedRecord!!.completedSets} 세트 완료",
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "⏱️ 총 ${selectedRecord!!.focusMinutes}분 집중",
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val hours = selectedRecord!!.focusMinutes / 60
                            val mins = selectedRecord!!.focusMinutes % 60
                            Text(
                                text = if (hours > 0) "= ${hours}시간 ${mins}분" else "= ${mins}분",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedRecord = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 상단 타이틀
        Text(
            text = "📊 집중 기록",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 주간 그래프
        WeeklyGraph(records = records)

        Spacer(modifier = Modifier.height(24.dp))

        // 월 선택
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Text("◀ 이전", fontSize = 16.sp)
            }

            Text(
                text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Text("다음 ▶", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 요일 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = when (day) {
                        "일" -> Color.Red
                        "토" -> Color.Blue
                        else -> Color.Black
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 달력 그리드
        val daysInMonth = getDaysInMonth(currentMonth)

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(daysInMonth) { day ->
                if (day == 0) {
                    Box(modifier = Modifier.aspectRatio(1f))
                } else {
                    val dateStr = "%d-%02d-%02d".format(currentMonth.year, currentMonth.monthValue, day)
                    val record = recordMap[dateStr]

                    DayCell(
                        day = day,
                        record = record,
                        isToday = dateStr == LocalDate.now().toString(),
                        onClick = {
                            if (record != null) {
                                selectedRecord = record
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 이번 달 통계
        val monthRecords = records.filter {
            it.date.startsWith("${currentMonth.year}-%02d".format(currentMonth.monthValue))
        }
        val totalMinutes = monthRecords.sumOf { it.focusMinutes }
        val totalSets = monthRecords.sumOf { it.completedSets }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "이번 달 통계",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "총 집중 시간: ${totalMinutes / 60}시간 ${totalMinutes % 60}분",
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "집중한 날: ${monthRecords.size}일",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun WeeklyGraph(records: List<StudyRecord>) {
    val today = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"))
    val startOfWeek = today.with(DayOfWeek.MONDAY)

    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

    val recordMap = records.associateBy { it.date }
    val weekData = weekDays.map { date ->
        recordMap[date.toString()]?.focusMinutes ?: 0
    }

    val maxMinutes = (weekData.maxOrNull() ?: 60).coerceAtLeast(60)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F8)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📈 이번 주 집중량",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weekData.forEachIndexed { index, minutes ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 시간 표시
                        if (minutes > 0) {
                            Text(
                                text = "${minutes}분",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // 막대
                        val barHeight = if (minutes > 0) {
                            (minutes.toFloat() / maxMinutes * 80).dp
                        } else {
                            4.dp
                        }

                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    when {
                                        minutes >= 120 -> Color(0xFFB91C1C)
                                        minutes >= 60 -> Color(0xFFEF4444)
                                        minutes > 0 -> Color(0xFFFCA5A5)
                                        else -> Color(0xFFE5E5E5)
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // 요일
                        Text(
                            text = dayLabels[index],
                            fontSize = 12.sp,
                            fontWeight = if (weekDays[index] == today) FontWeight.Bold else FontWeight.Normal,
                            color = if (weekDays[index] == today) Color(0xFFEF4444) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 주간 총계
            val weekTotal = weekData.sum()
            Text(
                text = "이번 주 총 ${weekTotal / 60}시간 ${weekTotal % 60}분",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    record: StudyRecord?,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val hasRecord = record != null
    val backgroundColor = when {
        hasRecord && record!!.focusMinutes >= 240 -> Color(0xFFB91C1C)
        hasRecord && record!!.focusMinutes >= 180 -> Color(0xFFDC2626)
        hasRecord && record!!.focusMinutes >= 120 -> Color(0xFFEF4444)
        hasRecord && record!!.focusMinutes >= 60 -> Color(0xFFF87171)
        hasRecord -> Color(0xFFFECACA)
        isToday -> Color(0xFFFEF2F2)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(enabled = hasRecord) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
            if (hasRecord) {
                Text(
                    text = "🍅",
                    fontSize = 10.sp
                )
            }
        }
    }
}

fun getDaysInMonth(yearMonth: YearMonth): List<Int> {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startDayOfWeek = firstDay.dayOfWeek.value % 7

    val days = mutableListOf<Int>()

    repeat(startDayOfWeek) { days.add(0) }

    for (day in 1..daysInMonth) {
        days.add(day)
    }

    return days
}