package com.example.w05

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 라이트 모드 색상 팔레트
private val LightColors = lightColorScheme(
    primary = Color(0xFF7E57C2),     // 보라색 버튼
    onPrimary = Color.White,          // 버튼 안 글자
    background = Color(0xFFFDFDFD),   // 배경색
    onBackground = Color.Black
)

// 다크 모드 색상 팔레트
private val DarkColors = darkColorScheme(
    primary = Color(0xFFB39DDB),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White
)

// 테마 정의
@Composable
fun TestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}
