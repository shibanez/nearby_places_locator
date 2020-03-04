package com.example.nearbyplaceslocator

import android.content.ContentProvider
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nearbyplaceslocator.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.jar.Manifest


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var MY_PERMISSIONS_ACCESS_COARSE_LOCATION: Int = 1
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var placesRecyclerView: RecyclerView
    private lateinit var httpClient: OkHttpClient
    private lateinit var currentLocation: LatLng
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //private lateinit var placesAdapter: RecylerView.Adapter<*>
    //private lateinit var recyclerViewLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.cardSearch.setOnClickListener {
            getNewCoordinates()
        }
        //mapFragment.map.setOnClickListener
    }

    private fun getNewCoordinates() {
        val newCoordinatesContent = Intent(Intent.ACTION_PICK).apply {}
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        getCurrentLocation()

        mMap.setOnMapClickListener {
            setCurrentLocation(it)
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_ACCESS_COARSE_LOCATION)
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                currentLocation = LatLng(it.latitude, it.longitude)
                setCurrentLocation(currentLocation)
            }
        }
    }

    private fun setCurrentLocation(location: LatLng) {
        val locationPosition: CameraPosition = CameraPosition.Builder()
            .target(location)
            .zoom(15.5f)
            .bearing(0f)
            .tilt(25f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(locationPosition))

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(location))
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_ACCESS_COARSE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

}
