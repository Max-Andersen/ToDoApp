package com.toloknov.summerschool.todoapp.ui.divkit

import android.content.Context
import org.json.JSONObject

class AssetReader(private val context: Context) {

    fun read(filename: String): JSONObject {
        val data = context.assets.open(filename).bufferedReader().use { it.readText() }
        return JSONObject(data)
    }
}