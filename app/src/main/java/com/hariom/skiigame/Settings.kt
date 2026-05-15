package com.hariom.skiigame

import android.graphics.Color
import android.graphics.ColorFilter
import android.media.Image
import android.transition.Slide
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(modifier: Modifier = Modifier,navController: NavController) {
    Scaffold(
    ) { paddingValues ->
        Column(modifier = Modifier.screenBody(paddingValues)) {

            Spacer(Modifier.height(34.dp))


            var slidervalue by remember { mutableStateOf(0f) }

            val color = when{
                slidervalue < 0.3f -> {
                    androidx.compose.ui.graphics.Color.Red
                }
                slidervalue < 0.5f -> {
                    androidx.compose.ui.graphics.Color.Cyan
                }
                slidervalue > 0.6f -> {
                    androidx.compose.ui.graphics.Color.Gray
                }
                slidervalue > 0.9f -> {
                    androidx.compose.ui.graphics.Color.Magenta
                }
                else -> {
                    androidx.compose.ui.graphics.Color.Blue
                }
            }



            Box(modifier = Modifier.fillMaxWidth().padding(8.dp),
                contentAlignment = Alignment.Center) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.skiing_person),
                    null,
                    modifier = Modifier.size(150.dp)
                )
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.skiing_jacket),
                    null,
                    modifier = Modifier.size(150.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color = color,
                        blendMode = BlendMode.SrcAtop)
                )
            }

            Row(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Slider(
                    value = slidervalue,
                    onValueChange = {
                        slidervalue = it
                    }
                )
            }

            Button(onClick = {
                navController.navigate(Routes.home.path)
                AppState.jacketColor = color
            }) {
                Text("Done")
            }



        }
    }
}
