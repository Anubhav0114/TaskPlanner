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

    private val collectionNames: ArrayList<CollectionRawData> = ArrayList()
    private val sharedPref: SharedPreferences
    private val collectionFlow: MutableSharedFlow<List<CollectionRawData>> = MutableSharedFlow(replay = 1)


    init {
        sharedPref = contextApp.getSharedPreferences("app_pref", Context.MODE_PRIVATE)
        collectionNames.addAll(parseCollection())
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        emitCollection()
    }

    private fun parseCollection(): List<CollectionRawData> {
        val collection: ArrayList<CollectionRawData> = ArrayList()
        val temp = sharedPref.getString("collection", "1234567,All")!!.split("|")
        for (data in temp){
            val d = data.split(",")
            collection.add(CollectionRawData(d[0].toLong(), d[1]))
        }

        return collection.toList()
    }

    fun getCollection(): Flow<List<CollectionRawData>> = collectionFlow.asSharedFlow()

    fun getSyncData(): String {
        return sharedPref.getString("collection", "1234567,All")?: "1234567,All"
    }

    fun saveSyncData(data: String){
        val editor = sharedPref.edit()
        editor.putString("collection", data)
        editor.apply()
    }


    fun addCollectionItem(name: String) {
        val id = generateUniqueId()
        collectionNames.add(CollectionRawData(id, name))
        saveCollection()
    }

    fun removeCollectionItem(id: Long) {
        for (item in collectionNames){
            if(item.id == id){
                collectionNames.remove(item)
                break
            }
        }
        saveCollection()
    }

    fun isCollectionExist(name: String): Boolean {
        var isExist = false
        for (item in collectionNames){
            if(item.name == name){
                isExist = true
                break
            }
        }

        return isExist
    }

    fun renameCollectionName(id: Long, newName: String){
        var index = -1
        for (i in collectionNames.indices){
            if(collectionNames[i].id == id){
                index = i
                break
            }
        }

        if(index != -1){
            collectionNames[index].name = newName
            saveCollection()
        }
    }


    fun getCollectionId(name: String): Long{
        for (item in collectionNames){
            if(item.name == name) return item.id
        }

        return 0
    }

    private fun saveCollection() {
        var str = ""
        for (temp in collectionNames) {
            str += if(temp.name == "All"){
                temp.toString()
            }else{
                "|${temp}"
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
            collectionFlow.emit(parseCollection())
        }
    }


}