package com.hariom.skiigame

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

fun Modifier.screenBody(paddingValues: PaddingValues) = this.fillMaxSize()
    .padding(paddingValues)

fun Modifier.basicCol() = this.fillMaxWidth().padding(8.dp)

fun Modifier.basicRow() = this.fillMaxWidth().padding(8.dp)


object AppState {
    var name by mutableStateOf<String?>(null)
}