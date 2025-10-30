package com.example.weatherapp.network

import android.annotation.SuppressLint
import android.location.Geocoder
import com.example.weatherapp.data.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class WeatherDataRepository {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: WeatherData.CurrentLocation) -> Unit,
        onFailure: () -> Unit
    ) {
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            location ?: onFailure()
            onSuccess(
                WeatherData.CurrentLocation(
                    latitude = location.latitude,
                    longtitude = location.longitude
                )
            )
        }.addOnFailureListener{onFailure()}
    }

    @Suppress("DEPRECATION")
    fun updateAddressText(
        currentLocation: WeatherData.CurrentLocation,
        geocoder: Geocoder
    ): WeatherData.CurrentLocation {
        val latitude = currentLocation.latitude ?: return currentLocation
        val longitude = currentLocation.longtitude ?: return currentLocation
        return geocoder.getFromLocation(latitude, longitude, 1)?.let { addresses ->
            val address = addresses[0]
            val addressText = StringBuilder()

            addressText.append(address.locality).append(",")
            addressText.append(address.adminArea).append(",")
            addressText.append(address.countryName)

            currentLocation.copy(
                location = addressText.toString()
            )
        } ?: currentLocation

    }
}