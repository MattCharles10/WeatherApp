package com.example.weatherapp.storage

import android.content.Context
import androidx.core.content.edit
import com.example.weatherapp.data.WeatherData
import com.google.gson.Gson

class SharedPreferencesManager(context: Context, private val gson: Gson) {

    companion object {
        private const val PREF_NAME = "WeatherAppPref"
        private const val KEY_CURRENT_LOCATION = "currentLocation"
    }

    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


    fun saveCurrentLocation(currentLocation: WeatherData.CurrentLocation) {
        val currentLocationJson = gson.toJson(currentLocation)
        sharedPreferences.edit {
            putString(KEY_CURRENT_LOCATION, currentLocationJson)
            apply() // Make sure changes are saved
        }
    }


    fun getCurrentLocation(): WeatherData.CurrentLocation? {
        val currentLocationJson = sharedPreferences.getString(KEY_CURRENT_LOCATION, null)
        return if (currentLocationJson != null) {
            gson.fromJson(currentLocationJson, WeatherData.CurrentLocation::class.java)
        } else {
            null
        }
    }


    fun clear() {
        sharedPreferences.edit {
            remove(KEY_CURRENT_LOCATION)
            apply()
        }
    }
}
