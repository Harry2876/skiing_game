package com.hariom.skiigame

import android.graphics.ImageFormat
import android.graphics.Paint
import android.media.SoundPool
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.w3c.dom.Text
import kotlin.random.Random


enum class ItemType {
    COINS,
    OBSTACLE
}

data class Item(
    var x: Float,
    var y: Float,
    var type: ItemType
)


@Composable
fun Greetings(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    var screenwidth by remember { mutableStateOf(0) }
    var screenheight by remember { mutableStateOf(0) }

    var baseSpeed by remember { mutableStateOf(0f) }

    var lasttime = remember { System.currentTimeMillis() }


    var bgX1 by remember { mutableStateOf(0f) }
    var bgX2 by remember { mutableStateOf(0f) }

    var velocityY by remember { mutableStateOf(0f) }
    var playerY by remember { mutableStateOf(0f) }
    val gravity = 2000f
    val jumpforce = -1300f

    var items by remember { mutableStateOf(listOf<Item>()) }
    var spawnTimer by remember { mutableStateOf(0f) }

    var isLongPressed  by remember { mutableStateOf(false)}
    var invisibleTime by remember { mutableStateOf(0f) }

    var coins by remember { mutableStateOf(10f) }

    val soundPool = remember { SoundPool.Builder().setMaxStreams(5).build() }


    LaunchedEffect(Unit) {
        while (true) {

            val currentTime = System.currentTimeMillis()

            val delta = (currentTime - lasttime) / 1000f

            lasttime = currentTime

            baseSpeed = 300f

            //background trees
            bgX1 -= baseSpeed * delta
            bgX2 -= baseSpeed * delta

            if (bgX1 < -screenwidth) {
                bgX1 = bgX2 + screenwidth - 150f
            }
            if (bgX2 < -screenwidth) {
                bgX2 = bgX1 + screenwidth - 150f
            }

            //addign the jump
            velocityY += gravity * delta
            playerY += velocityY * delta

            val groundY = screenheight - 350f

            if (playerY > groundY) {
                playerY = groundY
                velocityY = 0f
            }

            //adding the spawning logic
            spawnTimer += delta
            if (spawnTimer > 1f) {
                items = items + Item(
                    x = screenwidth.toFloat(),
                    y = screenheight - 200f,
                    type = if (Random.nextFloat() < 0.8f) ItemType.COINS else ItemType.OBSTACLE
                )
                spawnTimer = 0f
            }

            //adding the invisibilty
            if (isLongPressed){
                invisibleTime -= delta
                coins -= delta

                if (coins <= 0){
                    isLongPressed = false
                }

            }

            val updated = mutableListOf<Item>()

            items.forEach { item ->
                val newX = item.x - baseSpeed * delta

                val collison = checkCollison(
                    playerX = screenwidth/2 - 150f,
                    playerY = playerY + 100,
                    playerW = 100,
                    playerH = 100,
                    itemY = item.y ,
                    itemX = newX,
                    itemW = 30,
                    itemH = 30
                )

                if (collison && !isLongPressed){
                    when(item.type) {
                        ItemType.COINS -> {
                            coins += 1
                        }

                        ItemType.OBSTACLE -> {}
                    }
                }else {
                    updated.add(
                        item.copy(
                            x  = newX
                        )
                    )
                }

            }

            items = updated






            delay(16)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .onSizeChanged { size ->
                screenwidth = size.width
                screenheight = size.height
                bgX1 = 0f
                bgX2 = screenwidth.toFloat()
                playerY = screenheight - 350f
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val groundY = screenheight - 350f
                        if (playerY >= groundY - 1f) {
                            velocityY = jumpforce
                        }
                    },
                    onLongPress = {
                        isLongPressed = true
                        invisibleTime = 1f
                    }
                    ,
                    onPress = {
                        tryAwaitRelease()
                        isLongPressed = false
                    }
                )
            }) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Text(coins.toInt().toString())
        }

        if (isLongPressed){
            Text("Invinsible modewa",
                Modifier.align(Alignment.Center))
        }



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Image(
                painterResource(R.drawable.trees), null,
                Modifier.offset {
                    IntOffset(
                        x = bgX1.toInt(),
                        y = -50
                    )
                }
            )

            Image(
                painterResource(R.drawable.trees), null,
                Modifier.offset {
                    IntOffset(
                        x = bgX2.toInt(),
                        y = -50
                    )
                }
            )
        }

        Image(
            painterResource(R.drawable.skiing_person),
            null,
            modifier = Modifier
                .size(100.dp)
                .offset {
                    IntOffset(
                        x = screenwidth / 2 - 150,
                        y = playerY.toInt()
                    )
                }
        )

        items.forEach { item ->
            Image(
                painter = if (item.type == ItemType.COINS) {
                    painterResource(R.drawable.coin)
                } else {
                    painterResource(R.drawable.obstacle)
                }, null,
                modifier = Modifier
                    .size(30.dp)
                    .offset {
                        IntOffset(
                            x = item.x.toInt(),
                            y = item.y.toInt()
                        )
                    })
        }


    }


}

fun checkCollison(
    playerX : Float,
    playerY : Float,
    playerW : Int,
    playerH : Int,
    itemX : Float,
    itemY : Float,
    itemW : Int,
    itemH : Int
): Boolean {
    val playerRect = Rect(
        playerX, playerY,
        playerX + playerW,
        playerY + playerH
    )

    val itemRect = Rect(
        itemX, itemY,
        itemX + itemW,
        itemY + itemH
    )

    return  playerRect.overlaps(itemRect)
}

@Preview
@Composable
fun fhds(modifier: Modifier = Modifier) {
    Greetings()
}