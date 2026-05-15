package com.hariom.skiigame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    Scaffold() {paddingValues ->
        Column(modifier = Modifier.screenBody(paddingValues)) {


            var sliderval by remember { mutableStateOf(0f) }

            val color = when {
                sliderval < 0.5f -> {
                    Color.Red
                }sliderval < 0.2f -> {
                    Color.Black
                }
                sliderval > 0.6f -> {
                    Color.Magenta
                }
                sliderval < 0.9f -> {
                    Color.Cyan
                }
                else -> {
                    Color.Transparent
                }
            }
            Image(painter = painterResource(R.drawable.skiing_person), null,
                modifier = Modifier
                    .size(200.dp).background(color = color))



            Row(modifier = Modifier.padding(16.dp)) {


                Slider(
                    value = sliderval,
                    onValueChange = {
                        sliderval = it
                    }
                )

            }

            Button(onClick = {}) {
                Text("Done")
            }


        }
    }
    
}

@Preview
@Composable
private fun djkas() {
    SettingsScreen()
}