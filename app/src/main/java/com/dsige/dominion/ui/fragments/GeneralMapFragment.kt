package com.dsige.dominion.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.paging.map

import com.dsige.dominion.R
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GeneralMapFragment : DaggerFragment(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel

    private lateinit var mMap: GoogleMap
    lateinit var locationManager: LocationManager
    lateinit var camera: CameraPosition

    private var minDistanceChangeForUpdates: Int = 10
    private var minTimeBwUpdates: Int = 5000
    private var isFirstTime: Boolean = true

    private var param1: String? = null
    private var param2: String? = null

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationManager =
            requireContext().getSystemService(DaggerAppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true

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

        mMap.setOnMarkerClickListener(this)

        otViewModel.getOts().observe(viewLifecycleOwner, {count ->
            mMap.clear()
            count.map {s->
                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(s.latitud.toDouble(), s.longitud.toDouble()))
                            .title(s.nombreEmpresa)
                            .icon(Util.bitmapDescriptorFromVector(requireContext(), R.drawable.ic_place))
                    )
                }
            }
        })
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
                        .icon(Util.bitmapDescriptorFromVector(requireContext(), R.drawable.ic_people))
                )
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        if (isFirstTime) {
            zoomToLocation(location)
        }
        isFirstTime = false
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

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