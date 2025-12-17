package com.example.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    // 기록 추가
    @Insert
    suspend fun insert(record: StudyRecord)

    // 모든 기록 불러오기 (달력용)
    @Query("SELECT * FROM study_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<StudyRecord>>

    // 특정 날짜 기록 불러오기
    @Query("SELECT * FROM study_records WHERE date = :date")
    suspend fun getByDate(date: String): StudyRecord?

    // 특정 날짜 기록 업데이트 (같은 날 또 공부하면 누적)
    @Query("UPDATE study_records SET focusMinutes = focusMinutes + :minutes, completedSets = completedSets + :sets WHERE date = :date")
    suspend fun updateRecord(date: String, minutes: Int, sets: Int)
}