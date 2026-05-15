package com.hariom.skiigame

import android.adservices.adselection.GetAdSelectionDataOutcome
import android.content.Context
import android.service.notification.NotificationListenerService
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.savedstate.SavedStateReader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.collections.emptyList

data class Result(
    var name: String,
    var duration : Int,
    var coins : Int
)

object AppState {
    var name by mutableStateOf<String?>(null)
    var jacketColor by mutableStateOf<Color?>(null)
}


val Context.datastor by preferencesDataStore("all")

object PrefKeys {
    val RANKS = stringPreferencesKey("rank")
}


suspend fun SaveRan(context: Context, result: List<Result>){
    context.datastor.edit {
        val json = Gson().toJson(result)
        it[PrefKeys.RANKS] = json
    }
}

fun readRanks(context: Context): Flow<List<Result>> {
    return context.datastor.data.map {
       val json = it[PrefKeys.RANKS]

        if (json == null){
            emptyList()
        }else {
            var type = object  : TypeToken<List<Result>>()  {}.type
            Gson().fromJson(json, type)
        }
    }
}

suspend fun saveRank(context: Context, rank: Result){
    val currentList = readRanks(context).first().toMutableList()

    currentList.add(rank)

    SaveRan(context, currentList)
}