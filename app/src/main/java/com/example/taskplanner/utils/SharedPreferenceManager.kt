package com.example.taskplanner.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class SharedPreferenceManager(private val externalScope: CoroutineScope, contextApp: Context) : OnSharedPreferenceChangeListener {

    private val collectionNames: ArrayList<String> = ArrayList()
    private val sharedPref: SharedPreferences
    private val collectionFlow: MutableSharedFlow<List<String>> = MutableSharedFlow(replay = 1)


    init {
        sharedPref = contextApp.getSharedPreferences("app_pref", Context.MODE_PRIVATE)
        collectionNames.addAll(sharedPref.getString("collection", "All")!!.split("|"))
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        emitCollection()
    }

    fun getCollection(): Flow<List<String>> =collectionFlow.asSharedFlow()


    fun addCollectionItem(name: String) {
        collectionNames.add(name)
        saveCollection()
    }

    fun removeCollectionItem(name: String) {
        collectionNames.remove(name)
        saveCollection()
    }

    private fun saveCollection() {
        var str = ""
        for (temp in collectionNames) {
            str += if(temp == "All"){
                temp
            }else{
                "|$temp"
            }
        }

        val editor = sharedPref.edit()
        editor.putString("collection", str)
        editor.apply()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
       emitCollection()
    }

    private fun emitCollection(){
        externalScope.launch {
            collectionFlow.emit(sharedPref.getString("collection", "All")!!.split("|"))
        }
    }


}