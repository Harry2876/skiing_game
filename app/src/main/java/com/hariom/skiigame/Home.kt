package com.hariom.skiigame

import android.graphics.Outline
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.w3c.dom.Text

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    Scaffold(){paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)){
            Image(painter = painterResource(R.drawable.bg), null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            var name by remember { mutableStateOf("") }


            Column(modifier = Modifier
                .basicCol()
                .align(Alignment.Center)) {
                Text("Go Skiing")
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    placeholder = {
                        Text("Player name")
                    }
                )

                val context = LocalContext.current

                var haptics = LocalHapticFeedback.current

                val scope = rememberCoroutineScope()



                Button(
                    onClick = {
                        if (name.isEmpty()){
                            Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        AppState.name = name

                        scope.launch {
                            SaveName(context, name)
                        }

                        haptics.performHapticFeedback(
                            HapticFeedbackType.ContextClick
                        )
                        navController.navigate(Routes.game.path)}
                ) {
                    Text("Start Game")
                }
                Button(
                    onClick = {navController.navigate(Routes.rankings.path)}
                ) {
                    Text("Rankings")
                }
                Button(
                    onClick = {navController.navigate(Routes.settings.path)}
                ) {
                    Text("Setting")
                }
            }




        }


    }
}
