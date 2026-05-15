package com.hariom.skiigame

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Routes(val path: String){
    data object  home : Routes("home")
    data object  game : Routes("game")
    data object  ranking : Routes("ranking")
    data object  setting : Routes("setting")
}

@Composable
fun MainNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.home.path
    ) {
        composable(Routes.home.path){HomeScreen(navController = navController)}
        composable(Routes.game.path){GameScreen()}
        composable(Routes.ranking.path){RankingScreen()}
        composable(Routes.setting.path){SettingsScreen()}
    }
}