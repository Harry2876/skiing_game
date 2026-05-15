package com.hariom.skiigame

import android.media.Image
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {

    Scaffold() {paddingValues ->
        Box(modifier = Modifier.screenBody(paddingValues)){
            androidx.compose.foundation.Image(painter = painterResource(R.drawable.bg
            ), null,
                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)

            var name by remember { mutableStateOf("") }


            Column {
                Text("Go Skiing")
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    placeholder = {
                        Text(
                            "Player name"
                        )
                    }
                )
                
                val context = LocalContext.current
                val haptics = LocalHapticFeedback.current

                Button(onClick = {
                    if (name.isEmpty()){
                        Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    AppState.name = name
                    navController.navigate(Routes.game.path)
                    haptics.performHapticFeedback(HapticFeedbackType.ToggleOn)
                }) {
                    Text("Start Game")
                }
                Button(onClick = {
                    navController.navigate(Routes.rankings.path)
                }) {
                    Text("Rankings")
                }
                Button(onClick = {
                    navController.navigate(Routes.settings.path)
                }) {
                    Text("Setting")
                }
            }
        }
    }
    
}