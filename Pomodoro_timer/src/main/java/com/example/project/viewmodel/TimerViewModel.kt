package com.example.project.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.AppDatabase
import com.example.project.data.SettingsDataStore
import com.example.project.data.StudyRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import android.content.Context
import android.media.RingtoneManager
import android.os.Build


class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsDataStore = SettingsDataStore(application)
    private val database = AppDatabase.getDatabase(application)
    private val studyDao = database.studyDao()

    // 설정값
    val focusTime = settingsDataStore.focusTime
    val breakTime = settingsDataStore.breakTime
    val setCount = settingsDataStore.setCount

    // 타이머 상태
    private val _timeLeft = MutableStateFlow(25 * 60)
    val timeLeft: StateFlow<Int> = _timeLeft

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _isWorkTime = MutableStateFlow(true)
    val isWorkTime: StateFlow<Boolean> = _isWorkTime

    private val _currentSet = MutableStateFlow(1)
    val currentSet: StateFlow<Int> = _currentSet

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished

    // 기록
    val allRecords = studyDao.getAllRecords()

    // 알림 설정
    val alarmType = settingsDataStore.alarmType

    // 알림 실행
    fun playAlarm(type: String) {
        if (type == "sound") {
            playSound()
        }
    }

    private fun playSound() {
        try {
            val toneGen = android.media.ToneGenerator(
                android.media.AudioManager.STREAM_ALARM,
                100
            )
            toneGen.startTone(android.media.ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // 알림 설정 저장
    fun saveAlarmType(type: String) {
        viewModelScope.launch {
            settingsDataStore.saveAlarmType(type)
        }
    }

    // 설정 저장
    fun saveFocusTime(minutes: Int) {
        viewModelScope.launch {
            settingsDataStore.saveFocusTime(minutes)
        }
    }

    fun saveBreakTime(minutes: Int) {
        viewModelScope.launch {
            settingsDataStore.saveBreakTime(minutes)
        }
    }

    fun saveSetCount(count: Int) {
        viewModelScope.launch {
            settingsDataStore.saveSetCount(count)
        }
    }

    // 타이머 시작
    fun startTimer() {
        viewModelScope.launch {
            val focus = focusTime.first()
            _timeLeft.value = focus * 60
            _isRunning.value = true
            _isWorkTime.value = true
            _currentSet.value = 1
            _isFinished.value = false
        }
    }

    // 1초 감소
    fun tick() {
        if (_timeLeft.value > 0) {
            _timeLeft.value -= 1
        }
    }

    // 일시정지/재개
    fun togglePause() {
        _isRunning.value = !_isRunning.value
    }

    // 다음 단계로 (집중 → 휴식 → 집중...)
    fun nextPhase() {
        viewModelScope.launch {
            val focus = focusTime.first()
            val breakMins = breakTime.first()
            val sets = setCount.first()

            if (_isWorkTime.value) {
                // 집중 끝 → 세트 단위로 저장!
                saveOneSet(focus)

                if (_currentSet.value >= sets) {
                    // 모든 세트 완료
                    _isFinished.value = true
                    _isRunning.value = false
                } else {
                    // 휴식 시작
                    _isWorkTime.value = false
                    _timeLeft.value = breakMins * 60
                }
            } else {
                // 휴식 끝 → 다음 세트 시작
                _currentSet.value += 1
                _isWorkTime.value = true
                _timeLeft.value = focus * 60
            }
        }
    }

    // 휴식 건너뛰기
    fun skipBreak() {
        viewModelScope.launch {
            val focus = focusTime.first()
            val sets = setCount.first()

            if (!_isWorkTime.value) {
                // 휴식 중일 때만 작동
                if (_currentSet.value >= sets) {
                    // 마지막 세트였으면 완료
                    _isFinished.value = true
                    _isRunning.value = false
                } else {
                    // 다음 세트로
                    _currentSet.value += 1
                    _isWorkTime.value = true
                    _timeLeft.value = focus * 60
                }
            }
        }
    }

    // 세트 1개 완료 시 저장
    private fun saveOneSet(focusMinutes: Int) {
        viewModelScope.launch {
            val today = LocalDate.now(ZoneId.of("Asia/Seoul")).toString()
            val existing = studyDao.getByDate(today)

            if (existing != null) {
                studyDao.updateRecord(today, focusMinutes, 1)
            } else {
                studyDao.insert(
                    StudyRecord(
                        date = today,
                        focusMinutes = focusMinutes,
                        completedSets = 1
                    )
                )
            }
        }
    }

    // 리셋
    fun reset() {
        viewModelScope.launch {
            val focus = focusTime.first()
            _timeLeft.value = focus * 60
            _isRunning.value = false
            _isWorkTime.value = true
            _currentSet.value = 1
            _isFinished.value = false
        }
    }
/*
    // 테스트용 가짜 데이터 추가
    fun addTestData() {
        viewModelScope.launch {
            val testData = listOf(
                StudyRecord(date = "2025-12-01", focusMinutes = 40, completedSets = 1),
                StudyRecord(date = "2025-12-03", focusMinutes = 75, completedSets = 3),
                StudyRecord(date = "2025-12-06", focusMinutes = 130, completedSets = 5),
                StudyRecord(date = "2025-12-09", focusMinutes = 200, completedSets = 8),
                StudyRecord(date = "2025-12-11", focusMinutes = 250, completedSets = 10)
            )
            testData.forEach { record ->
                val existing = studyDao.getByDate(record.date)
                if (existing == null) {
                    studyDao.insert(record)
                }
            }
        }
    }
*/
}