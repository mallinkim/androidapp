package com.example.project.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val FOCUS_TIME = intPreferencesKey("focus_time")
        val BREAK_TIME = intPreferencesKey("break_time")
        val SET_COUNT = intPreferencesKey("set_count")
        val ALARM_TYPE = stringPreferencesKey("alarm_type")
    }

    // 설정값 불러오기
    val focusTime: Flow<Int> = context.dataStore.data.map { it[FOCUS_TIME] ?: 25 }
    val breakTime: Flow<Int> = context.dataStore.data.map { it[BREAK_TIME] ?: 5 }
    val setCount: Flow<Int> = context.dataStore.data.map { it[SET_COUNT] ?: 4 }
    val alarmType: Flow<String> = context.dataStore.data.map { it[ALARM_TYPE] ?: "both" }

    // 설정값 저장하기
    suspend fun saveFocusTime(minutes: Int) {
        context.dataStore.edit { it[FOCUS_TIME] = minutes }
    }

    suspend fun saveBreakTime(minutes: Int) {
        context.dataStore.edit { it[BREAK_TIME] = minutes }
    }

    suspend fun saveSetCount(count: Int) {
        context.dataStore.edit { it[SET_COUNT] = count }
    }

    suspend fun saveAlarmType(type: String) {
        context.dataStore.edit { it[ALARM_TYPE] = type }
    }
}