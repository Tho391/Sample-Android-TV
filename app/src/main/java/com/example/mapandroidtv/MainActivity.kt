package com.example.mapandroidtv

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.mapandroidtv.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }

        setUpMap()

        binding.button.setOnClickListener {
            requestLocation()

        }
    }

    private fun requestLocation() {
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val location = getLocationByProvider(LocationManager.NETWORK_PROVIDER)
        Log.i("Main", "Location: ${location?.latitude}")

//        Dexter.withContext(this)
//            .withPermissions(permissions)
//            .withListener(object : MultiplePermissionsListener {
//                @SuppressLint("MissingPermission")
//                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
//
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    p0: MutableList<PermissionRequest>?,
//                    p1: PermissionToken?
//                ) {
//                    Log.i(
//                        "Main",
//                        "onPermissionRationaleShouldBeShown"
//                    )
//                }
//
//            })

    }

    private fun addMarker(lastLocation: Location) {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        val markerOptions =
            MarkerOptions().position(latLng)
        googleMap.addMarker(markerOptions)
        googleMap.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(
                    latLng,
                    15F
                )
            )
        )
    }

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun setUpMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it

            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(0.0, 0.0))
                    .title("Marker")
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationByProvider(provider: String): Location? {
        var location: Location? = null
        val locationManager: LocationManager = applicationContext
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(provider)) {
            return null
        }
        try {
            if (locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider)
            }
        } catch (e: IllegalArgumentException) {
            Log.d("Main", "Cannot access Provider $provider")
        }
        return location
    }
}