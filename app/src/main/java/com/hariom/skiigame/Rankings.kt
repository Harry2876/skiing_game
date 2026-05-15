package com.hariom.skiigame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(modifier: Modifier = Modifier) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Rankings")
                },
                navigationIcon = {

                    Button(
                        onClick = {}
                    ) {
                        Text("Back")
                    }
                }
            )
        }
    ) {paddingValues ->
        Column(modifier = Modifier.screenBody(paddingValues)) {
            Row(modifier = Modifier.basicRow(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Ranking")
                Text("Player Name")
                Text("Coin")
                Text("Duration")
            }
        }
    }
    
}

@Preview
@Composable
private fun fads() {
    RankingScreen()
}