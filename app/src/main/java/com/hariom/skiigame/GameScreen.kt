package com.hariom.skiigame

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.SoundPool
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.disableHotReloadMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.sourceInformationMarkerEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


enum class ItemType {
    COIN, OBSTACLE
}

data class Item(
    var x: Float, var y: Float, var type: ItemType
)

enum class GameState {
    RUNNING, PAUSED, ENDED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current

    var screenWidth by remember { mutableStateOf(0) }
    var screenHeight by remember { mutableStateOf(0) }

    var lastTime = remember { System.currentTimeMillis() }

    var bgX1 by remember { mutableStateOf(0f) }
    var bgX2 by remember { mutableStateOf(0f) }

    var spawnTimer by remember { mutableStateOf(0f) }
    var items by remember { mutableStateOf(listOf<Item>()) }

    var playerY by remember { mutableStateOf(0f) }
    var velocityY by remember { mutableStateOf(0f) }

    val gravity = 2000f
    val jumpForce = -1300f

    var coins by remember { mutableStateOf(10f) }
    var timer by remember { mutableStateOf(0f) }

    var name by remember { mutableStateOf("") }

    var gameState by remember { mutableStateOf(GameState.RUNNING) }

    var dragtopbottom by remember { mutableStateOf(false) }
    var draglefttor by remember { mutableStateOf(false) }

    var baseSpeed by remember { mutableStateOf(0f) }

    var gameEndDialog by remember { mutableStateOf(false) }

    var isInvisible by remember { mutableStateOf(false) }

    val haptics = LocalHapticFeedback.current

    var invisibleTiemr by remember { mutableStateOf(2f) }

    val player = remember { ExoPlayer.Builder(context).build() }

    var soundPool = SoundPool.Builder().setMaxStreams(5).build()

    var jumpsound by remember { mutableStateOf(0) }
    var coinsound by remember { mutableStateOf(0) }
    var gameoversound by remember { mutableStateOf(0) }

    var jumptriggered by remember { mutableStateOf(false) }

     LaunchedEffect(Unit) {
         coinsound = soundPool.load(context, R.raw.coin, 1)
         jumpsound = soundPool.load(context, R.raw.jump, 1)
         gameoversound = soundPool.load(context, R.raw.game_over, 1)
     }

    LaunchedEffect(Unit) {
        val uri = "android.resource://${context.packageName}/${R.raw.bgm}"

        val media = MediaItem.fromUri(uri)

        player.setMediaItem(media)
        player.prepare()
        player.repeatMode = Player.REPEAT_MODE_ALL

        player.play()
    }

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var tiltX by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()

    name = readName(context).collectAsState("").value

    AppState.name = name




    DisposableEffect(Unit) {
        val listner = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            }

            override fun onSensorChanged(p0: SensorEvent?) {
                tiltX   = p0?.values!![0]
            }
        }

        sensorManager.registerListener(
            listner,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )

        onDispose {
            sensorManager.unregisterListener(listner)
            player.release()
        }
    }





    LaunchedEffect(Unit) {
        while (true) {

            if (gameState == GameState.RUNNING) {


                val currentTime = System.currentTimeMillis()

                val delta = (currentTime - lastTime) / 1000f

                lastTime = currentTime


                if (dragtopbottom) {
                    baseSpeed = 900f
                } else if (tiltX > 2f) {
                    player.volume = 0.3f
                    baseSpeed = 200f
                }else {
                    player.volume = 1f
                    baseSpeed = 500f

                }


                bgX1 -= baseSpeed * delta
                bgX2 -= baseSpeed * delta

                if (bgX1 < -screenWidth) {
                    bgX1 = bgX2 + screenWidth
                }
                if (bgX2 < -screenWidth) {
                    bgX2 = bgX2 + screenWidth
                }

                //adding the jump stuff

                velocityY += gravity * delta
                playerY += velocityY * delta

                val groundY = screenHeight - 300f

                if (playerY > groundY) {
                    playerY = groundY
                    velocityY = 0f
                }

                timer += delta

                if (isInvisible){
                    invisibleTiemr -= delta
                     coins -= delta

                    when {
                        false -> {
                            isInvisible = false
                        }
                        coins <=0 -> {
                            isInvisible = false
                        }
                    }
                }

                println(jumptriggered)

                if (jumptriggered){
                    soundPool.play(
                        jumpsound,
                        1f, 1f,1, 0 ,1f
                    )
                    jumptriggered = false
                }



                //spawong the items
                spawnTimer += delta

                if (spawnTimer > 1f) {
                    items = items + Item(
                        x = screenWidth.toFloat(),
                        y = screenHeight.toFloat() - 250f,
                        type = if (Random.nextFloat() < 0.9f) ItemType.COIN else ItemType.OBSTACLE
                    )
                    spawnTimer = 0f
                }

                val updatedItems = mutableStateListOf<Item>()

                items.forEach { item ->
                    val newX = item.x - baseSpeed * delta


                    var collided = checkCollison(
                        playerX = (screenWidth / 2 - 150).toFloat(),
                        playerY = playerY,
                        playerW = 100,
                        playerH = 100,
                        itemX = newX,
                        itemY = screenHeight - 250f,
                        itemW = 30,
                        30
                    )


                    if (collided  && !isInvisible) {
                        when (item.type) {
                            ItemType.COIN -> {
                                soundPool.play(
                                    coinsound,
                                    1f, 1f,1, 0 ,1f
                                )
                                coins += 1
                            }

                            ItemType.OBSTACLE -> {
                                haptics.performHapticFeedback(
                                    HapticFeedbackType.ContextClick
                                )
                                soundPool.play(
                                    gameoversound,
                                    1f, 1f,1, 0 ,1f
                                )
                                gameState = GameState.ENDED
                                player.stop()
                                gameEndDialog = true
                                scope.launch {
                                    addRanking(context,
                                           name = name,
                                            coins = coins.toInt(),
                                            duration = timer.toInt()
                                    )
                                }
                            }
                        }
                    } else {
                        updatedItems.add(
                            item.copy(
                                x = newX
                            )
                        )
                    }
                }

                items = updatedItems


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
                screenHeight = size.height
                screenWidth = size.width
                bgX1 = 0f
                bgX2 = screenWidth.toFloat()
                playerY = screenHeight - 350f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragX = 0f
                        dragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        if (dragAmount.y > dragAmount.x) {
                            dragY += dragAmount.y

                            if (dragY > 50f) {
                                dragtopbottom = true
                            }
                        }

                        if (dragAmount.x > dragAmount.y) {
                            dragX += dragAmount.x

                            if (dragX > 50f) {
                                draglefttor = true
                            }
                        }


                    },
                    onDragEnd = {
                        dragX = 0f
                        dragY = 0f
                        dragtopbottom = false
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val groundY = screenHeight - 350f
                        if (playerY >= groundY - 1f) {
                            velocityY = jumpForce
                            jumptriggered = true
                        }

                    },
                   onLongPress = {
                       if (coins > 0){
                           isInvisible = true
                           invisibleTiemr = 1f
                       }
                    },
                    onPress = {
                        tryAwaitRelease()
                        isInvisible = false
                    })
            }) {




        println(tiltX)



        Image(
            painterResource(R.drawable.bg),
            null,
            modifier = Modifier.size(screenWidth.dp, screenHeight.dp),
            contentScale = ContentScale.FillBounds
        )

        name = AppState.name ?: ""

        Box(
            modifier = Modifier
                .basicRow()
                .align(Alignment.TopStart)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().systemBarsPadding(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = if (gameState == GameState.RUNNING) {
                        painterResource(R.drawable.pause)
                    } else {
                        painterResource(R.drawable.play)
                    }, null,
                    modifier = Modifier.clickable(
                        onClick = {
                            if (gameState == GameState.RUNNING) {
                                gameState = GameState.PAUSED
                                player.pause()
                            } else if (gameState == GameState.PAUSED) {
                                gameState = GameState.RUNNING
                                lastTime = System.currentTimeMillis()
                                player.play()
                            }
                        }
                    ))
                Column {
                    Text(name)
                    Text(coins.toInt().toString())
                    Text(timer.toInt().toString())
                }
            }
        }

        if (dragtopbottom) {
            Text("Bottom swipe triggereed")
        }
        if (isInvisible) {
            Box(
                modifier = Modifier
                    .basicRow()
                    .align(Alignment.Center)
            ) {
                Column {
                    Text("Invincibility Mode")
                    Text("Ends in $invisibleTiemr")
                }
            }
        }

        if (draglefttor) {
            gameState = GameState.PAUSED
            player.pause()
            Box(
                modifier = Modifier
                    .basicRow()
                    .align(Alignment.Center)
            ) {
                AlertDialog(
                    onDismissRequest = {
                        draglefttor = false
                        gameState = GameState.RUNNING
                        lastTime = System.currentTimeMillis()
                        player.play()
                    },
                    text = {
                        Text("The game is in progress. Are you sure to quit?")
                    },
                    dismissButton = {
                        Button(onClick = {
                            draglefttor = false
                            gameState = GameState.RUNNING
                            lastTime = System.currentTimeMillis()
                            player.play()
                        }) {
                            Text("No")

                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            draglefttor = false
                        }) {
                            Text("Yes")

                        }
                    }

                )
            }
        }
        if (gameEndDialog) {
            Box(
                modifier = Modifier
                    .basicRow()
                    .align(Alignment.Center)
            ) {
                Dialog(
                    onDismissRequest = {}
                ) {
                    Card(modifier = Modifier.basicCol()) {
                        Column(
                            modifier = Modifier
                                .basicCol()
                        ) {
                            Text("Game Over")
                            Text(name)
                            Text(coins.toInt().toString())
                            Text(timer.toInt().toString())

                            Row(Modifier.basicRow()) {


                                Button(
                                    onClick = {
                                        gameState = GameState.RUNNING
                                        coins = 10f
                                        timer = 0f
                                        gameEndDialog = false
                                    }
                                ) { Text("Restart") }

                                Button(
                                    onClick = {
                                        navController.navigate(Routes.rankings.path){
                                            popUpTo(Routes.game.path){inclusive = true}
                                        }
                                    }
                                ) { Text("Go To Rankings") }
                            }
                        }
                    }
                }
            }
        }




        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Image(
                painter = painterResource(R.drawable.trees), null, modifier = Modifier.offset {
                    IntOffset(
                        x = bgX1.toInt(), y = 0
                    )
                })
            Image(
                painter = painterResource(R.drawable.trees), null, modifier = Modifier.offset {
                    IntOffset(
                        x = bgX2.toInt(), y = 0
                    )
                })
            Rectangle(rotv =  tiltX)
        }


        Image(
            painterResource(R.drawable.skiing_person), null, Modifier
                .size(100.dp)
                .offset {
                    IntOffset(
                        x = screenWidth / 2 - 150, y = playerY.toInt()
                    )
                })

        items.forEach { item ->
            Image(
                painter = if (item.type == ItemType.COIN) {
                    painterResource(R.drawable.coin)
                } else {
                    painterResource(R.drawable.obstacle)
                }, null, modifier = Modifier
                    .size(30.dp)
                    .rotate(if (tiltX > 1) 0f else 2f)
                    .offset {
                        IntOffset(
                            x = item.x.toInt(), y = item.y.toInt()
                        )
                    }
            )
        }


    }


}

@Composable
fun Rectangle(modifier: Modifier = Modifier, rotv : Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .rotate(if (rotv > 2f) 0f else 10f)
    ) {
        drawRect(
            Color.White, topLeft = Offset(10f, 380f), size = Size(size.width + 30f, 250f)
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
    itemH: Int,
): Boolean {
    val playerRect = Rect(
        playerX, playerY, playerX + playerW, playerY + playerH
    )

    val itemRect = Rect(
        itemX, itemY, itemX + itemW, itemY + itemH
    )

    return playerRect.overlaps(itemRect)
}
