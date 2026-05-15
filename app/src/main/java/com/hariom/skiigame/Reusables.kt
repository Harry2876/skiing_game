package com.hariom.skiigame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.basicRow()  = this.fillMaxWidth().padding(8.dp)

fun Modifier.screenBody(paddingValues: PaddingValues) = this.fillMaxSize().padding(paddingValues)

fun Modifier.basicCard() = this.fillMaxWidth().padding(8.dp)