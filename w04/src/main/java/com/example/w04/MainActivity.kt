package com.example.w04

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.w04.ui.theme._0910_3Theme
import androidx.compose.material3.Card
import androidx.compose.material3.Card
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource


data class Message(val name: String, val msg: String)
data class Profile(val name: String, val intro: String)

@Composable
fun HomeScreen() {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(), //전체 화면을 차지
            contentAlignment = Alignment.Center // 중앙 정렬
        ) {
            //MessageCard(Message("Android", "Jetpack Compose"))
            ProfileCard(Profile("김태원", "안드로이드 앱 개발자"))
        }
    }
}



@Preview(
    name = "Profile Card Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)

@Composable
fun PreviewProfileCard() {
    TaewonyTheme {
        ProfileCard(Profile("김윤영", "모바일앱실습"))
    }
}

@Composable
fun ProfileCard(data: Profile) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.image),
            contentDescription = "프로필 사진",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape) // 동그랗게 잘라내기
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = data.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.intro,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TaewonyTheme(content: @Composable () -> Unit) {
    val darkColors = darkColorScheme()

    MaterialTheme (
        colorScheme = darkColors,
        typography = MaterialTheme.typography,
        content = content
    )
}

@Preview(
    name = "Message Card Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun PreviewMessageCard() {
    TaewonyTheme {
        MessageCard(Message("김윤영", "모바일앱실습"))
    }
}

/**
@Composable
fun ProfileCard(data: Profile) {

}
**/




@Composable
fun MessageCard(me: Message) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = me.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = me.msg,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
