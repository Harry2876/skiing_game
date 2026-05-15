package com.hariom.skiigame

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.screenBody(paddingValues: PaddingValues) = this.fillMaxSize().padding(paddingValues)

fun Modifier.basicrowcols() = this.fillMaxWidth().padding(8.dp)

fun Modifier.basicCards() = this.fillMaxWidth().padding(8.dp)