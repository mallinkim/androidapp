package com.example.project.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_records")
data class StudyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,           // 날짜 (예: "2025-01-15")
    val focusMinutes: Int,      // 총 집중 시간 (분)
    val completedSets: Int      // 완료한 세트 수
)