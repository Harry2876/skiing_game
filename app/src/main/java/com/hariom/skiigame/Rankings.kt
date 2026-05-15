package com.hariom.skiigame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsScreen(modifier: Modifier = Modifier, navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title =  {
                    Text("Rankings")
                },
                navigationIcon = {
                    Button(
                        onClick = {
                            navController.navigate(Routes.home.path)
                        }
                    ) {
                        Text("Back")
                    }
                }
            )
        }
    ) {paddingValues ->

        val context = LocalContext.current

        val ranksData = readRanks(context).collectAsState(emptyList()).value

        val sortedRanks = ranksData.sortedByDescending { 
            it.duration
        }






        Column(modifier = Modifier.screenBody(paddingValues)) {
            Row(modifier = Modifier.basicrowcols(),
                horizontalArrangement = Arrangement.SpaceBetween
                ) {
                Text("Rank")
                Text("Player Name")
                Text("Coin")
                Text("Duration")
            }
            LazyColumn() {
                itemsIndexed(sortedRanks){index , item->
                    Row(modifier = Modifier.basicrowcols(),
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
