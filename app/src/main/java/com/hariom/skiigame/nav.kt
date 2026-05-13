package com.hariom.skiigame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Routes(val path : String){
    data object home : Routes("home")
    data object game : Routes("game")
    data object rankings : Routes("rankings")
    data object settings : Routes("settings")
}

@Composable
fun MainNav(modifier: Modifier = Modifier) {
    val navController  = rememberNavController()

    NavHost(
        navController  = navController,
        startDestination = Routes.home.path
    ){
        composable(Routes.home.path){HomeScreen(navController = navController)}
        composable(Routes.game.path){GameScreen(navController = navController)}
        composable(Routes.rankings.path){RankingsPage()}
        composable(Routes.settings.path){SettingsPage()}
    }
}