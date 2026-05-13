package com.hariom.skiigame

import android.app.appfunctions.ExecuteAppFunctionResponse
import android.content.Context
import androidx.annotation.StyleRes
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


data class Rankings(
    var rank: Int = 0,
    val name : String,
    val duration : Int,
    val coins : Int
)

val Context.dataStore by preferencesDataStore("app")

object PrefKeys {
    val NAME = stringPreferencesKey("name")
    val RANKINGS = stringPreferencesKey("rankigns")
}

suspend fun SaveName(context: Context, name: String){
    context.dataStore.edit {
        it[PrefKeys.NAME] = name
    }
}

fun readName(context: Context): Flow<String>{
    return context.dataStore.data.map {
        it[PrefKeys.NAME] ?: ""
    }
}

suspend fun SaveRank(context: Context, rank : List<Rankings>){

    val json = Gson().toJson(rank)

    context.dataStore.edit {
        it[PrefKeys.RANKINGS] = json
    }
}
fun readRankings(context: Context): Flow<List<Rankings>> {
    return context.dataStore.data.map {
        val json = it[PrefKeys.RANKINGS]

        if (json == null){
            emptyList()
        }else {
            val type = object  : TypeToken< List< Rankings>>() {}.type
            Gson().fromJson(json, type)
        }
    }
}

suspend fun addRanking(context: Context, coins: Int, duration: Int, name: String){
    val currentlist = readRankings(context).first().toMutableList()

    val rank = currentlist.size + 1

    currentlist.add(
        Rankings(
            rank = rank,
            coins = coins,
            name = name,
            duration = duration
        )
    )

    SaveRank(context, currentlist)
}