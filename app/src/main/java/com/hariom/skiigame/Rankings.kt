package com.hariom.skiigame

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsPage(modifier: Modifier = Modifier) {
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
    ) { paddingValues ->
        Column(modifier = Modifier.screenBody(paddingValues)) {

            val context = LocalContext.current

            var haptics = LocalHapticFeedback.current

            val scope = rememberCoroutineScope()

            val name = AppState.name ?: ""

            var rankingsData = readRankings(
                context
            ).collectAsState(emptyList())

            val sorted = rankingsData.value.sortedByDescending { it.duration }
            Row(
                modifier = Modifier.basicRow(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ranking")
                Text("Player Name")
                Text("Coin")
                Text("Duration")
            }

            LazyColumn() {
                itemsIndexed(sorted){index , item ->

                    val exi = name == item.name
                    println(exi)
                    println(AppState.name)

                    if (exi) {
                        Card(modifier = Modifier.basicRow()) {


                            Row(
                                modifier = Modifier.basicRow(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${index + 1}")
                                Text(item.name)
                                Text(item.coins.toString())
                                Text(item.duration.toString())
                            }
                        }
                    }else {
                        Row(
                            modifier = Modifier.basicRow(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${index + 1}")
                            Text(item.name)
                            Text(item.coins.toString())
                            Text(item.duration.toString())
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun fhdalsk() {
    RankingsPage()
}