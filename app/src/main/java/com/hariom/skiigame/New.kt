package com.hariom.skiigame

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.SoundPool
import android.util.Log
import androidx.collection.mutableLongListOf
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.indirect.IndirectTouchEvent
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.AppLaunchChecker
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlin.random.Random

enum class ItemType {
    COINS,
    OBSTACLES
}

data class Item(
    var x: Float,
    var y: Float,
    var type: ItemType
)

enum class GameState {
    RUNNING,
    PAUSED,
    OVER
}


@Composable
fun Greetings(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    //declaring the gamestate
    var gameState by remember { mutableStateOf(GameState.RUNNING) }

    var screenwidth by remember { mutableStateOf(0) }
    var screenheight by remember { mutableStateOf(0) }

    var lastTime = remember { System.currentTimeMillis() }

    var baseSpeed by remember { mutableStateOf(0f) }

    //adding the trees
    var bgX1 by remember { mutableStateOf(0f) }
    var bgX2 by remember { mutableStateOf(0f) }

    //addiong the spawn
    var spawntimer by remember { mutableStateOf(0f) }
    var items by remember { mutableStateOf(listOf<Item>()) }

    //adding the jump
    var velocityY by remember { mutableStateOf(0f) }
    var playerY by remember { mutableStateOf(0f) }
    val gravity = 2000f
    val jumpForce = -1300f

    //adding the coins stuff
    var coins by remember { mutableStateOf(10f) }
    var timer by remember { mutableStateOf(0f) }

    //show gameover cared
    var overcard by remember { mutableStateOf(false) }

    var musicPlayer = remember { ExoPlayer.Builder(context).build() }
    var soundPool = remember { SoundPool.Builder().setMaxStreams(5).build() }

    //adding interactive sounds
    var jump by remember { mutableStateOf(0) }
    var coinscollect by remember { mutableStateOf(0) }
    var gameover by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        jump = soundPool.load(context, R.raw.jump, 1)
        coinscollect = soundPool.load(context, R.raw.coin, 1)
        gameover = soundPool.load(context, R.raw.game_over, 1)
    }

    var jumpPressed by remember { mutableStateOf(false) }


    //adding the sensors
    var sensormanager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    //adding the titlt
    var tiltX by remember { mutableStateOf(0f) }

    //show swipe dialog
    var swipedialog by remember { mutableStateOf(false) }

    //adding swipe down
    var isBoosted by remember { mutableStateOf(false) }

    //adding invincibilyt
    var isInvisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        val uri = "android.resource://${context.packageName}/${R.raw.bgm}"

        val mediaitem = MediaItem.fromUri(uri)
        musicPlayer.setMediaItem(mediaitem)
        musicPlayer.prepare()
        musicPlayer.repeatMode = Player.REPEAT_MODE_ALL
        musicPlayer.play()
    }




    DisposableEffect(Unit) {
        val listner = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            }

            override fun onSensorChanged(p0: SensorEvent?) {
                tiltX = p0?.values!![0]
            }
        }

        sensormanager.registerListener(
            listner,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )

        onDispose {
            sensormanager.unregisterListener(
                listner
            )
            musicPlayer.release()
        }
    }



    LaunchedEffect(Unit) {
        while (true) {
            if (gameState == GameState.RUNNING) {
                var currentTime = System.currentTimeMillis()

                val delta = (currentTime - lastTime) / 1000f

                lastTime = currentTime

                //adding the jump sound
                if (jumpPressed) {
                    soundPool.play(
                        jump, 1f, 1f, 1, 0, 1f
                    )
                    jumpPressed = false
                }

                //changing the spoeed according to the tikt
                if (tiltX > 1f) {
                    baseSpeed = 300f
                    musicPlayer.volume = 0.3f
                } else if (isBoosted) {
                    baseSpeed = 900f
                } else{ baseSpeed = 600f
                musicPlayer.volume = 1f}

                //adding the invisible

                if (isInvisible){
                    coins -= delta
                    if (coins <= 0){
                        isInvisible = false
                    }
                }


                bgX1 -= baseSpeed * delta
                bgX2 -= baseSpeed * delta

                if (bgX1 < -screenwidth) {
                    bgX1 = bgX2 + screenwidth - 150f
                }

                if (bgX2 < -screenwidth) {
                    bgX2 = bgX2 + screenwidth - 150f
                }

                //increasing the timer
                timer += delta

                //adding the jump logic
                velocityY += gravity * delta
                playerY += velocityY * delta

                val groundY = screenheight - 350f

                if (playerY > groundY) {
                    playerY = groundY
                    velocityY = 0f
                }


                //adding item spawning
                spawntimer += delta
                if (spawntimer > 2f) {
                    items = items + Item(
                        x = screenwidth.toFloat(),
                        y = screenheight - 250f,
                        type = if (Random.nextDouble() < 0.8f) ItemType.COINS else ItemType.OBSTACLES
                    )
                    spawntimer = 0f
                }

                val updated = mutableListOf<Item>()

                items.forEach { item ->
                    val newX = item.x - baseSpeed * delta

                    val collided = checkCollison(
                        playerX = screenwidth / 2 - 150f,
                        playerY = playerY + 100f,
                        playerW = 100,
                        playerH = 100,
                        itemX = newX,
                        itemY = item.y,
                        30, 30
                    )

                    if (collided && !isInvisible) {

                        when (item.type) {
                            ItemType.COINS -> {
                                coins += 1
                                soundPool.play(
                                    coinscollect, 1f, 1f, 1, 0, 1f
                                )
                            }

                            ItemType.OBSTACLES -> {
                                soundPool.play(
                                    gameover, 1f, 1f, 1, 0, 1f
                                )
                                gameState = GameState.OVER
                                musicPlayer.stop()
                                overcard = true
                            }
                        }

                    } else {
                        updated.add(
                            item.copy(
                                x = newX
                            )
                        )
                    }
                }

                items = updated


            }

            delay(16)
        }
    }

    var dragX by remember { mutableStateOf(0f) }
    var dragY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                screenheight = size.height
                screenwidth = size.width
                bgX1 = 0f
                bgX2 = screenwidth.toFloat()
                playerY = screenheight - 350f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragX = 0f
                        dragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        if (dragAmount.x > dragAmount.y) {
                            dragX += dragAmount.x

                            if (dragX > 30f) {
                                swipedialog = true
                            }
                        }

                        if (dragAmount.y > dragAmount.x) {
                            dragY += dragAmount.y
                            Log.d("Drag", "Greetings: $dragY")
                            if (dragY > 30f) {
                                isBoosted = true
                            }
                        }
                    },
                    onDragEnd = {
                        dragX = 0f
                        dragY = 0f
                        isBoosted = false
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val groundY = screenheight - 350f
                        if (playerY >= groundY - 1f) {
                            velocityY = jumpForce
                            jumpPressed = true
                        }
                    },
                    onLongPress = {
                        isInvisible = true
                    },
                    onPress = {
                        tryAwaitRelease()
                        isInvisible = false
                    }
                )
            }) {

        Image(
            painter = painterResource(R.drawable.bg), null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(painter =  if (gameState == GameState.RUNNING){
                painterResource(R.drawable.pause)
            }else {
                painterResource(R.drawable.play)
            }, null,
                modifier = Modifier.clickable(
                    onClick = {
                         if (gameState == GameState.RUNNING) {
                            GameState.PAUSED
                             musicPlayer.pause()
                        } else {
                            GameState.RUNNING
                             musicPlayer.play()
                            lastTime = System.currentTimeMillis()

                        }
                    }
                ))
            Column {
                Text("Player name")
                Text(coins.toInt().toString())
                Text(timer.toInt().toString())
            }
        }

        if (swipedialog){
            gameState = GameState.PAUSED
            musicPlayer.pause()
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.Center)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("The game is in progress. Are you sure to quit?")
                        Button(onClick = {}) {
                            Text("Yes")
                        }
                        Button(onClick = {
                           gameState = GameState.RUNNING
                            lastTime = System.currentTimeMillis()
                            swipedialog = false
                            musicPlayer.play()
                        }) {
                            Text("No")
                        }
                    }
                }

            }
        }

        if (overcard){
            Card(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Game Over")
                    Text("Player Name ")
                    Text(coins.toInt().toString())
                    Text(timer.toInt().toString())

                    Row(modifier = Modifier.padding(8.dp)) {
                        Button(
                            onClick = {
                                overcard = false
                                gameState = GameState.RUNNING
                                coins = 10f
                                timer = 0f
                                musicPlayer.prepare()
                                musicPlayer.play()
                            }
                        ) {
                            Text("Restart")
                        }
                        Button(
                            onClick = {

                            }
                        ) {
                            Text("Go To Rankings")
                        }
                    }

                }
            }
        }

        if (isInvisible){
            Text("Invincibility Mode", modifier = Modifier.align(Alignment.Center))
        }


        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            Image(
                painter = painterResource(R.drawable.trees), null,
                modifier = Modifier.offset {
                    IntOffset(
                        x = bgX1.toInt(),
                        y = -50
                    )
                })
            Image(
                painter = painterResource(R.drawable.trees), null,
                modifier = Modifier.offset {
                    IntOffset(
                        x = bgX2.toInt(),
                        y = -50
                    )
                }
            )

            Rectangle(tilt = tiltX)
        }

        Image(
            painter = painterResource(R.drawable.skiing_person), null,
            modifier = Modifier
                .size(100.dp)
                .offset {
                    IntOffset(
                        x = screenwidth / 2 - 150,
                        y = playerY.toInt()
                    )
                })

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

@Composable
fun Rectangle(modifier: Modifier = Modifier, tilt: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .rotate(
                if (tilt > 1f) 0f else 10f
            )
    ) {
        drawRect(
            Color.White,
            topLeft = Offset(5f, 370f),
            size = Size(size.width + 30, 300f)
        )
    }
}

fun checkCollison(
    playerX: Float,
    playerY: Float,
    playerW: Int,
    playerH: Int,
    itemX: Float,
    itemY: Float,
    itemW: Int,
    itemH: Int
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

    return playerRect.overlaps(itemRect)
}

@Preview
@Composable
private fun ffasd() {
    Greetings()
}


