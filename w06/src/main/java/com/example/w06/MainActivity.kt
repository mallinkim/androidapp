package com.example.w06


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.w06.ui.theme.gameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            gameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BubbleGameScreen()
                }
            }
        }
    }
}

data class Bubble(
    val id: Int,
    var position: Offset,
    val radius: Float,
    val color: Color,
    val creationTime: Long = System.currentTimeMillis(),
    val velocityX: Float = 0f,
    val velocityY: Float = 0f
)

// 게임 상태 data를 한군데 모아 편리하게 관리하는 클래스
class GameState(
    // 클래스를 생성할 때 초기 버블 리스트를 받을 수 있도록 파라미터 추가
    // 기본값으로 emptyList()를 지정하여, 파라미터 없이 GameState()로도 생성 가능
    initialBubbles: List<Bubble> = emptyList()
) {
    var bubbles by mutableStateOf(initialBubbles)
    var score by mutableStateOf(0)
    var isGameOver by mutableStateOf(false)
    var timeLeft by mutableStateOf(60) // 남은 시간: 60초로 시작
}

// 상단 UI를 별도의 Composable로 분리 (가독성 향상)
@Composable
fun GameStatusRow(score: Int, timeLeft: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Time: ${timeLeft}s", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

fun makeNewBubble(maxWidth: Dp, maxHeight: Dp): Bubble {
    return Bubble(
        id = Random.nextInt(),
        position = Offset(
            x = Random.nextFloat() * maxWidth.value,
            y = Random.nextFloat() * maxHeight.value
        ),
        radius = Random.nextFloat() * 50 + 50,
        velocityX = Random.nextFloat() * 5,
        velocityY = Random.nextFloat
            () * 5,
        color = Color(
            red = Random.nextInt(256),
            green = Random.nextInt(256),
            blue = Random.nextInt(256),
            alpha = 200
        )
    )
}

// 버블 UI를 그리는 Composable
@Composable
fun BubbleComposable(bubble: Bubble, onClick: () -> Unit) {
    Canvas(
        modifier = Modifier
            .size((bubble.radius * 2).dp)
            .offset(x = bubble.position.x.dp, y = bubble.position.y.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // 클릭 시 물결 효과 제거
                onClick = onClick
            )
    ) {
        // 3. 원은 Canvas의 정가운데에 그립니다.
        drawCircle(
            color = bubble.color,
            radius = size.width / 2, // / size.width는 Canvas의 실제 가로 픽셀(px) 크기
            center = center
        )
    }
}


//이 다음 코드가 안들어있는듯?
// --- 1. 버블 이동 계산 함수 분리 ---
fun updateBubblePositions(
    bubbles: List<Bubble>,
    canvasWidthPx: Float,
    canvasHeightPx: Float,
    density: Density
): List<Bubble> {
    return bubbles.map { bubble ->
        with(density) {
            // --- 1. 모든 dp 값을 px로 변환 ---
            val radiusPx = bubble.radius.dp.toPx()
            var xPx = bubble.position.x.dp.toPx()
            var yPx = bubble.position.y.dp.toPx()
            val vxPx = bubble.velocityX.dp.toPx()
            val vyPx = bubble.velocityY.dp.toPx()

            // --- 2. px 단위로 물리 계산 수행 ---
            xPx += vxPx
            yPx += vyPx

            var newVx = bubble.velocityX
            var newVy = bubble.velocityY

            if (xPx < radiusPx || xPx > canvasWidthPx - radiusPx) newVx *= -1
            if (yPx < radiusPx || yPx > canvasHeightPx - radiusPx) newVy *= -1

            xPx = xPx.coerceIn(radiusPx, canvasWidthPx - radiusPx)
            yPx = yPx.coerceIn(radiusPx, canvasHeightPx - radiusPx)

            // --- 3. 결과를 다시 dp로 변환하여 새 버블로 반환 ---
            bubble.copy(
                position = Offset(
                    x = xPx.toDp().value,
                    y = yPx.toDp().value
                ),
                velocityX = newVx,
                velocityY = newVy
            )
        }
    }
}


// --- 게임 전체 화면 ---
@SuppressLint("boxdBoxWithConstraintsScope", "UnusedBoxWithConstraintsScope")
@Composable
fun BubbleGameScreen() {
    val gameState = remember { GameState() }

    // --- 타이머 로직 ---
    LaunchedEffect(Unit) {
        while (!gameState.isGameOver && gameState.timeLeft > 0) {
            delay(1000L)
            gameState.timeLeft--
            if (gameState.timeLeft <= 0) {
                gameState.isGameOver = true
                break
            }

            // 3초 지난 버블 제거
            val currentTime = System.currentTimeMillis()
            gameState.bubbles = gameState.bubbles.filter {
                currentTime - it.creationTime < 3000
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (gameState.isGameOver) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "게임 종료!\n\n최종 점수: ${gameState.score}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        } else {
            GameStatusRow(score = gameState.score, timeLeft = gameState.timeLeft)

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val density = LocalDensity.current
                val canvasWidthPx = with(density) { maxWidth.toPx() }
                val canvasHeightPx = with(density) { maxHeight.toPx() }

                // --- 버블 생성 및 이동 로직 ---
                LaunchedEffect(Unit) { // 한번만 실행
                    while (!gameState.isGameOver) {
                        delay(16)

                        // 버블이 없으면 새로 3개 생성
                        if (gameState.bubbles.isEmpty()) {
                            val newBubbles = List(3) {
                                makeNewBubble(maxWidth, maxHeight)
                            }
                            gameState.bubbles = newBubbles
                        }

                        // 랜덤으로 새 버블 생성
                        if (Random.nextFloat() < 0.05f && gameState.bubbles.size < 15) {
                            val newBubble = makeNewBubble(maxWidth, maxHeight)
                            gameState.bubbles = gameState.bubbles + newBubble
                        }

                        // 버블 물리 이동
                        gameState.bubbles = updateBubblePositions(
                            gameState.bubbles,
                            canvasWidthPx,
                            canvasHeightPx,
                            density
                        )
                    }
                }

                // --- 버블 그리기 ---
                gameState.bubbles.forEach { bubble ->
                    BubbleComposable(bubble = bubble) {
                        // 클릭 시 점수 +1, 해당 버블 제거
                        gameState.score++
                        gameState.bubbles =
                            gameState.bubbles.filterNot { it.id == bubble.id }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun BubbleGamePreview() {
    gameTheme {
        BubbleGameScreen()
    }
}

