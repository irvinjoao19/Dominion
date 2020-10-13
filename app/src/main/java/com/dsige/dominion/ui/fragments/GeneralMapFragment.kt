package com.dsige.dominion.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.DataParser
import com.dsige.dominion.helper.Util
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.maps.android.ui.IconGenerator
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GeneralMapFragment : DaggerFragment(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel

    lateinit var mMap: GoogleMap
    lateinit var locationManager: LocationManager
    lateinit var camera: CameraPosition

    private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Int = 10
    private var MIN_TIME_BW_UPDATES: Int = 5000
    private var isFirstTime: Boolean = true
    private var waypoints: String = ""

    private var param1: String? = null
    private var param2: String? = null

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationManager =
            context!!.getSystemService(DaggerAppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME_BW_UPDATES.toLong(),
            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
            this
        )
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME_BW_UPDATES.toLong(),
            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
            this
        )

        mMap.setOnMarkerClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_general_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)
        otViewModel.getOts().observe(viewLifecycleOwner, Observer { count ->
            mMap.clear()
            for (s: Ot in count) {
                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(s.latitud.toDouble(), s.longitud.toDouble()))
                            .title(s.nombreEmpresa)
                            .icon(Util.bitmapDescriptorFromVector(context!!, R.drawable.ic_place))
                    )
                }
            }
        })
        otViewModel.search.value = ""
    }

    private fun zoomToLocation(location: Location?) {
        if (location != null) {
            if (context != null) {
                camera = CameraPosition.Builder()
                    .target(LatLng(location.latitude, location.longitude))
                    .zoom(12f)  // limite 21
                    //.bearing(165) // 0 - 365°
                    .tilt(30f)        // limit 90
                    .build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

                mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(location.latitude, location.longitude))
                        .title("YO")
                        .icon(Util.bitmapDescriptorFromVector(context!!, R.drawable.ic_people))
                )
            }
        }
    }

    private fun getUrl(lat: String, lng: String, lat2: String, lng2: String): String {
        val str_origin = "origin=$lat,$lng"
        val str_dest = "destination=$lat2,$lng2"
        val mode = "mode=driving&alternatives=false"
        val sensor = "sensor=false"
        val parameters = "$str_origin&$str_dest&$mode&$sensor&$waypoints"
        val output = "json"

        Log.i(
            "TAG", String.format(
                "https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s",
                output,
                parameters,
                getString(R.string.google_maps_key)
            )
        )
        return String.format(
            "https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s",
            output,
            parameters,
            getString(R.string.google_maps_key)
        )
    }

    override fun onLocationChanged(location: Location) {
        if (isFirstTime) {
            zoomToLocation(location)
        }
        isFirstTime = false
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    @SuppressLint("StaticFieldLeak")
    private inner class FetchURL : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg strings: String): String { // For storing data from web service
            var data = ""
            try {
                data = downloadUrl(strings[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            val map: MapPrincipal = Gson().fromJson(s, MapPrincipal::class.java)
            val mapRoutes: List<MapRoute>? = map.routes
            if (mapRoutes != null) {
                for (r: MapRoute in map.routes) {
                    val mapLegs: List<MapLegs>? = r.legs
                    var i = 0
                    if (mapLegs != null) {
                        for (m: MapLegs in mapLegs) {
                            val start: MapStartLocation? = m.start_location
                            if (start != null) {
                                val position = LatLng(start.lat, start.lng)
                                if (context != null) {
                                    val iconFactory = IconGenerator(context)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(position)
                                            .title(m.start_address)
                                            .icon(
                                                BitmapDescriptorFactory.fromBitmap(
                                                    iconFactory.makeIcon(
                                                        (i++).toString()
                                                    )
                                                )
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            PointsParser().execute(s)
        }

        @Throws(IOException::class)
        private fun downloadUrl(strUrl: String): String {
            var data = ""
            var iStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(strUrl)
                // Creating an http connection to communicate with url
                urlConnection = url.openConnection() as HttpURLConnection
                // Connecting to url
                urlConnection.connect()
                // Reading data from url
                iStream = urlConnection.inputStream
                val br =
                    BufferedReader(InputStreamReader(iStream!!))
                val sb = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                data = sb.toString()
                br.close()
            } catch (e: Exception) {
                Log.d("mylog", "Exception downloading URL: $e")
            } finally {
                iStream?.close()
                urlConnection!!.disconnect()
            }
            return data
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class PointsParser :
        AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? =
                null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DataParser()
                routes = parser.parse(jObject)
            } catch (e: java.lang.Exception) {
                Log.d("mylog", e.toString())
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng>
            var lineOptions: PolylineOptions?
            for (i in result!!.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()
                val path = result[i]
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                lineOptions.addAll(points)
                lineOptions.width(7f)
                lineOptions.color(Color.BLUE)
                mMap.addPolyline(lineOptions)
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GeneralMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }
}
