package com.dsige.dominion.ui.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.MapLegs
import com.dsige.dominion.data.local.model.MapPrincipal
import com.dsige.dominion.data.local.model.MapRoute
import com.dsige.dominion.data.local.model.MapStartLocation
import com.dsige.dominion.helper.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class OtMapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    TaskLoadedCallback {

    lateinit var camera: CameraPosition
    private lateinit var mMap: GoogleMap
    private var mapView: View? = null
    lateinit var place1: MarkerOptions
    lateinit var place2: MarkerOptions
    lateinit var locationManager: LocationManager

    private var minDistanceChangeForUpdates: Int = 10
    private var minTimeBwUpdates: Int = 5000
    private var isFirstTime: Boolean = true

    private var latitud: String = ""
    private var longitud: String = ""
    private var title: String = ""
    private var mode: String = ""

    override fun onResume() {
        super.onResume()
        val gps = Gps(this)
        if (!gps.isLocationEnabled()) {
            gps.showSettingsAlert(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ot_map)
        val b = intent.extras
        if (b != null) {
            latitud = b.getString("latitud")!!
            longitud = b.getString("longitud")!!
            title = b.getString("title")!!
            mode = b.getString("mode")!!
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val permisos = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap = googleMap

            val sydney = LatLng(latitud.toDouble(), longitud.toDouble())
            mMap.addMarker(MarkerOptions().position(sydney).title(title))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

            mMap.isMyLocationEnabled = true
            mMap.isTrafficEnabled = true

            if (mapView?.findViewById<View>(Integer.parseInt("1")) != null) {
                // Get the button view
                val locationButton =
                    (mapView!!.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(
                        Integer.parseInt("2")
                    )
                // and next place it, on bottom right (as Google Maps app)
                val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
                // position on right bottom
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                layoutParams.setMargins(0, 0, 30, 30)
            }

            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTimeBwUpdates.toLong(),
                minDistanceChangeForUpdates.toFloat(),
                this
            )
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTimeBwUpdates.toLong(),
                minDistanceChangeForUpdates.toFloat(),
                this
            )
        } else {
            ActivityCompat.requestPermissions(this, permisos, 1)
        }
    }

    private fun zoomToLocation(location: Location) {
        camera = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            .zoom(12f)  // limite 21
            //.bearing(165) // 0 - 365°
            .tilt(30f)        // limit 90
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
    }

    private fun getUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=" + origin.latitude + "," + origin.longitude
        val strDest = "destination=" + dest.latitude + "," + dest.longitude
        val mode = "mode=$mode&alternatives=true"
        val parameters = "$strOrigin&$strDest&$mode"
        val output = "json"

        return String.format(
            "https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s",
            output, parameters, getString(R.string.google_maps_key)
        )
    }

    override fun onLocationChanged(location: Location) {
        if (isFirstTime) {
            zoomToLocation(location)
            place1 =
                MarkerOptions().position(LatLng(location.latitude, location.longitude)).title("YO")
            place2 = MarkerOptions().position(LatLng(latitud.toDouble(), longitud.toDouble()))
                .title(title)
            FetchUrl(getUrl(place1.position, place2.position), "driving", this)
            isFirstTime = false
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    override fun onTaskDone(vararg values: Any) {
        val polyline = mMap.addPolyline(values[0] as PolylineOptions)
        polyline.isClickable = true
    }

    override fun onTaskRoutes(values: MapPrincipal) {
        val mapRoutes: List<MapRoute> = values.routes
        if (mapRoutes.isNotEmpty()) {
            for (r: MapRoute in values.routes) {
                val mapLegs: List<MapLegs> = r.legs
                if (mapLegs.isNotEmpty()) {
                    for (m: MapLegs in mapLegs) {
                        val start: MapStartLocation? = m.start_location
                        if (start != null) {
                            val position = LatLng(start.lat, start.lng)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(m.start_address)
                                    .icon(
                                        Util.bitmapDescriptorFromVector(
                                            this@OtMapActivity,
                                            R.drawable.ic_people
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}