package com.hariom.skiigame

import android.media.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class ItemsType {
    COIN,
    OBSTACLES
}

data class Itemmm(
    var x: Float,
    var y: Float,
    var type: ItemsType,
    var isPassed: Boolean = false
)

@Composable
fun GameScreen(modifier: Modifier = Modifier) {

    var screewidht by remember { mutableStateOf(0) }
    var screeheight by remember { mutableStateOf(0) }
    var lastTime = remember { System.currentTimeMillis() }

    var baseSpeed by remember { mutableStateOf(0f) }

    //background trees
    var bgX1 by remember { mutableStateOf(0f) }
    var bgX2 by remember { mutableStateOf(0f) }

    //player and jump
    var velocityY by remember { mutableStateOf(0f) }
    var playerY by remember { mutableStateOf(0f) }
    val gravity = 2000f
    val jumpForce = -1300f

    //adding the items spawn
    var items by remember { mutableStateOf(listOf<Itemmm>()) }
    var spawnTimer by remember { mutableStateOf(0f) }

    var obstaclesPassed by remember { mutableStateOf(0) }

    //adding the score
    var score by remember { mutableStateOf(0f) }


    LaunchedEffect(Unit) {
        while (true) {

            val currenttime = System.currentTimeMillis()

            val delta = (currenttime - lastTime) / 1000f

            lastTime = currenttime

            baseSpeed = 400f

            //adding the background trees
            bgX1 -= baseSpeed * delta
            bgX2 -= baseSpeed * delta

            if (bgX1 < -screewidht) {
                bgX1 = bgX2 + screewidht - 150f
            }

            if (bgX2 < -screewidht) {
                bgX2 = bgX1 + screewidht - 150f
            }

            //adding the player jump
            velocityY += gravity * delta
            playerY += velocityY * delta

            val groundY = screeheight - 350f
            if (playerY > groundY){
                playerY = groundY
                velocityY = 0f
            }

            spawnTimer += delta
            if (spawnTimer > 2f){
                items = items + Itemmm(
                    x = screewidht.toFloat(),
                    y = screeheight - 250f,
                    type = if (Random.nextDouble() < 0.7f) ItemsType.COIN else ItemsType.OBSTACLES
                )
                spawnTimer = 0f
            }
            var updated = mutableListOf<Itemmm>()

            //item movememnt and collison
            items?.forEach { itemmm ->
                var newX = itemmm.x - baseSpeed * delta

                if (itemmm.type == ItemsType.OBSTACLES&&
                    !itemmm.isPassed && newX < screewidht /2 - 150){
                    obstaclesPassed++
                    itemmm.isPassed = true
                }

                var collided = checkCollisons(
                    playerX = screewidht / 2 -150f,
                    playerY = playerY + 100f,
                    100,100,
                    itemX = newX,
                    itemY = itemmm.y,
                    30,30
                )

                if (collided){

                }else {
                    updated.add(
                        itemmm.copy(
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
            .onSizeChanged { size ->
                screeheight = size.height
                screewidht = size.width
                bgX1 = 0f
                bgX2 = screewidht.toFloat()
                playerY = screeheight - 350f
            }.pointerInput(Unit){
                detectTapGestures(
                    onTap = {
                        val groundY = screeheight - 350f
                        if (playerY >= groundY - 1f){
                            velocityY =jumpForce
                        }
                    }
                )
            }) {

        androidx.compose.foundation.Image(
            painter = painterResource(
                R.drawable.bg
            ), null,
            modifier = Modifier.size(screewidht.dp, screeheight.dp),
            contentScale = ContentScale.FillBounds
        )


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(
                    Alignment.BottomCenter
                )
        ) {

            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.trees), null,
                modifier = Modifier.offset {
                    IntOffset(
                        x = bgX1.toInt(),
                        y = -100
                    )
                }
            )

            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.trees), null,
                modifier = Modifier.offset {
                    IntOffset(
                        x = bgX2.toInt(),
                        y = -100
                    )
                }
            )

        }

        androidx.compose.foundation.Image(
            painter = painterResource(
                R.drawable.skiing_person
            ), null,
            modifier = Modifier
                .size(100.dp)
                .offset {
                    IntOffset(
                        x = screewidht / 2 - 150,
                        y = playerY.toInt()
                    )
                }
        )

        items.forEach {itemmm ->
            androidx.compose.foundation.Image(painter =
            if (itemmm.type == ItemsType.COIN){
                painterResource(R.drawable.coin)
            }else{
                painterResource(R.drawable.obstacle)
            }, null,
                modifier = Modifier.size(30.dp).offset{
                    IntOffset(
                        x = itemmm.x.toInt(),
                        y = itemmm.y.toInt()
                    )
                })
        }

    }


}


fun checkCollisons(
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
        playerX , playerY,
        playerX + playerW,
        playerY + playerH
    )

    val itemRect = Rect(
        itemX, itemY,
        itemX + itemW,
        itemY  + itemH
    )

    return playerRect.overlaps(itemRect)
}



    @Preview
@Composable
private fun sjhk() {
    GameScreen()
}
