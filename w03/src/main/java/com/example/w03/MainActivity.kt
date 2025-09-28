package com.example.w03

import android.graphics.Color.rgb
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.w03.ui.theme._0910_3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _0910_3Theme {
                    HomeScreen()

            }
        }
    }
}

@Composable()
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("roise",
            style = MaterialTheme.typography.headlineMedium
        )
        Image(
            painter = painterResource(id = R.drawable.image1),
            contentDescription = "Jetpack Compose 로고",
            modifier = Modifier
                .size(300.dp) // 이미지 크기 지정
                .padding(16.dp)
        )
        Text("toxic till the end",
            style = MaterialTheme.typography.headlineMedium
        )
        Text("ROSÉ",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(rgb(177, 27, 53)),   // 버튼 배경색
                contentColor = Color.White     // 글자 색
            )) {
            Text(text="재생"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
