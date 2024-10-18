package com.dicoding.japfatest.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class LocationHelper(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationCallback: LocationCallback

    // Object class to hold location data
    data class CurrentLocation(val latitude: Double, val longitude: Double)

    data class AddressResult(
        val fullAddress: String?,
        val locality: String?,
        val postalCode: String?,
        val countryName: String?
    )

    suspend fun getAddressFromLatLong(
        latitude: Double,
        longitude: Double
    ) : AddressResult? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    AddressResult(
                        fullAddress = address.getAddressLine(0),
                        locality = address.locality,
                        postalCode = address.postalCode,
                        countryName = address.countryName
                    )
                } else null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }!!
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationResult: (CurrentLocation?) -> Unit) {
        if (LocationPermissionHelper.hasLocationPermission(context)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationResult(CurrentLocation(it.latitude, it.longitude))
                } ?: run {
                    requestNewLocationData(onLocationResult)
                }
            }
        } else {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
            onLocationResult(null)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(onLocationResult: (CurrentLocation?) -> Unit) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).apply {
            setMinUpdateIntervalMillis(5000L)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if(location != null){
                    onLocationResult(CurrentLocation(location.latitude, location.longitude))
                } else {
                    onLocationResult(null)
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}