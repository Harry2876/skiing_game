package com.hariom.skiigame

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


sealed class Routes(val path: String){
    data object home : Routes("home")
    data object game : Routes("game")
    data object settings : Routes("settings")
    data object rankings : Routes("rankings")
}

@Composable
fun MainNav(modifier: Modifier = Modifier) {
    val navcontroller = rememberNavController()

    NavHost(
        navController = navcontroller,
        startDestination = Routes.home.path
    ) {
        composable(Routes.home.path){HomeScreen(navController = navcontroller)}
        composable(Routes.game.path){GameScreen()}
        composable(Routes.settings.path){SettingsScreen(navController = navcontroller)}
        composable(Routes.rankings.path){RankingsScreen(navController = navcontroller)}
    }
}