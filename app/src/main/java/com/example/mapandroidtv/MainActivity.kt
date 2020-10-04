package com.example.mapandroidtv

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.mapandroidtv.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var googleMap: GoogleMap

    private val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }

        setUpMap()

        binding.button.setOnClickListener {
            Log.i("Main", "button click")
//            requestLocationViaLocationManager()
            requestLocationViaFuseLocationClient()
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestLocationViaFuseLocationClient() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10 * 1000 //10s
            fastestInterval = 5 * 1000
        }

//        fusedLocationProviderClient.lastLocation
//            .addOnSuccessListener {
//                Log.i("Main", "addOnSuccessListener: ${it?.latitude}")
//            }.addOnFailureListener {
//                Log.e("Main", "addOnFailureListener: ${it.message}")
//            }


        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult?) {
                    Log.i("Main", "onLocationResult: ${p0?.locations?.size}")
                }
            },
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationViaLocationManager() {

        // Request a static location from the location manager

        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, false)
        Log.i("Main", "provider: $provider")
//        provider?.let {
//            val location = locationManager.getLastKnownLocation(provider)
//            Log.i("Main", "location: ${location?.latitude}")
//        }

        provider?.let {
            locationManager.requestLocationUpdates(
                provider,
                100,
                10F,
                object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        Log.i("Main", "onLocationChanged")
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                        Log.i("Main", "onStatusChanged")
                    }

                    override fun onProviderEnabled(provider: String?) {
                        Log.i("Main", "onProviderEnabled")

                    }

                    override fun onProviderDisabled(provider: String?) {
                        Log.i("Main", "onProviderDisabled")

                    }

                })
        }

//        fusedLocationClient.requestLocationUpdates(
//            getLocationRequest(),
//            object : LocationCallback() {
//                override fun onLocationAvailability(p0: LocationAvailability?) {
//                    super.onLocationAvailability(p0)
//                }
//
//                override fun onLocationResult(p0: LocationResult?) {
//                    super.onLocationResult(p0)
//                }
//            },
//            Looper.getMainLooper()
//        )


        //val location = getLocationByProvider(LocationManager.NETWORK_PROVIDER)
        //Log.i("Main", "Location: ${location?.latitude}")

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
}