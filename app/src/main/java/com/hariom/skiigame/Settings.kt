package com.hariom.skiigame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigationevent.compose.rememberNavigationEventState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(modifier: Modifier = Modifier) {
    Scaffold(
    ) { paddingValues ->

        var sliderValue by remember { mutableStateOf(0f) }



        Column(modifier = Modifier.screenBody(paddingValues)) {
            Image(painter = painterResource(R.drawable.skiing_person), null,
                Modifier.size(290.dp))



            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                }
            )

            Button(onClick = {}) {
                Text("Done")
            }


        }
    }
}

@Preview
@Composable
private fun faslkjj() {
    SettingsPage()
}