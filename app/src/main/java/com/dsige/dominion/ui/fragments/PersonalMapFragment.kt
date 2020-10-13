package com.dsige.dominion.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Filtro
import com.dsige.dominion.data.local.model.JefeCuadrilla
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class PersonalMapFragment : DaggerFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    LocationListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel
    lateinit var mMap: GoogleMap
    lateinit var locationManager: LocationManager
    lateinit var camera: CameraPosition

    private var servicioId: Int = 0
    private var tipoId: Int = 0
    private var proveedorId: Int = 0
    lateinit var f: Filtro

    private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Int = 10
    private var MIN_TIME_BW_UPDATES: Int = 5000
    private var isFirstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            servicioId = it.getInt(ARG_PARAM1)
            tipoId = it.getInt(ARG_PARAM2)
            proveedorId = it.getInt(ARG_PARAM3)
        }

        f = Filtro(Util.getFecha(), servicioId, tipoId, proveedorId)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        otViewModel.syncJefeCuadrilla(f)

        otViewModel.getJefeCuadrillas().observe(viewLifecycleOwner, {
            mMap.clear()
            for (s: JefeCuadrilla in it) {
                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                    Picasso.get().load(s.icono).into(object : Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        }
                        override fun onBitmapFailed(
                            e: java.lang.Exception?, errorDrawable: Drawable?) {
                        }
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(s.latitud.toDouble(), s.longitud.toDouble()))
                                    .title(s.cuadrillaId.toString())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            )
                        }
                    })
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(p1: Int, p2: Int, p3: Int) =
            PersonalMapFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, p1)
                    putInt(ARG_PARAM2, p2)
                    putInt(ARG_PARAM3, p3)
                }
            }
    }

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

    override fun onMarkerClick(p: Marker): Boolean {
        dialogJefeCuadrilla(p.title.toInt())
        return false
    }

    override fun onLocationChanged(location: Location?) {
        if (isFirstTime) {
            zoomToLocation(location)
        }
        isFirstTime = false
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

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
                        .icon(Util.bitmapDescriptorFromVector(context!!, R.drawable.ic_place))
                )
            }
        }
    }

    private fun dialogJefeCuadrilla(id: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.dialog_reporte_personal, null)

        val linearLayoutLoad: ConstraintLayout = v.findViewById(R.id.linearLayoutLoad)
        val linearLayoutPrincipal: LinearLayout = v.findViewById(R.id.linearLayoutPrincipal)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val textView1: TextView = v.findViewById(R.id.textView1)
        val textView2: TextView = v.findViewById(R.id.textView2)
        val textView3: TextView = v.findViewById(R.id.textView3)
        val textView4: TextView = v.findViewById(R.id.textView4)
        val imageViewClose: ImageView = v.findViewById(R.id.imageViewClose)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        imageViewClose.setOnClickListener { dialog.dismiss() }

        Handler().postDelayed({
            otViewModel.getJefeCuadrillaById(id)
                .observe(this, {
                    textViewTitle.text = it.nombreJefe
                    textView1.text = it.empresa
                    textView2.text = String.format("Cant Ot Asignados : %s", it.asignado)
                    textView3.text = String.format("Cant Ot Terminados : %s", it.terminado)
                    textView4.text = String.format("Cant Ot Pendiente : %s", it.pendiente)
                    linearLayoutLoad.visibility = View.GONE
                    linearLayoutPrincipal.visibility = View.VISIBLE
                })
        }, 800)
    }
}