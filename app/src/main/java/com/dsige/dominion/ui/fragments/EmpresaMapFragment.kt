package com.dsige.dominion.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Filtro
import com.dsige.dominion.data.local.model.OtReporte
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EmpresaMapFragment : DaggerFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    LocationListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel
    lateinit var mMap: GoogleMap
    lateinit var locationManager: LocationManager
    lateinit var camera: CameraPosition

    private var empresaId: Int = 0
    private var personalId: Int = 0
    lateinit var f: Filtro

    private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Int = 10
    private var MIN_TIME_BW_UPDATES: Int = 5000
    private var isFirstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            empresaId = it.getInt(ARG_PARAM1)
            personalId = it.getInt(ARG_PARAM2)
        }

        f = Filtro(empresaId, personalId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_empresa_map, container, false)
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

        otViewModel.syncEmpresa(f)

        otViewModel.getEmpresas().observe(viewLifecycleOwner, Observer {
            mMap.clear()
            for (s: OtReporte in it) {
                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(s.latitud.toDouble(), s.longitud.toDouble()))
                            .title(s.otId.toString())
                            .icon(
                                Util.bitmapDescriptorFromVector(
                                    context!!, when (s.estado) {
                                        "Terminado" -> R.drawable.ic_place_blue
                                        else -> R.drawable.ic_place_red
                                    }
                                )
                            )
                    )
                }
            }
        })

    }

    companion object {
        @JvmStatic
        fun newInstance(p1: Int, p2: Int) =
            EmpresaMapFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, p1)
                    putInt(ARG_PARAM2, p2)
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
        if (p.title != "YO") {
            dialogEmpresas(p.title.toInt())
        }

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

    private fun dialogEmpresas(id: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.dialog_reporte_empresas, null)

        val linearLayoutLoad: ConstraintLayout = v.findViewById(R.id.linearLayoutLoad)
        val linearLayoutPrincipal: LinearLayout = v.findViewById(R.id.linearLayoutPrincipal)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val textView1: TextView = v.findViewById(R.id.textView1)
        val textView2: TextView = v.findViewById(R.id.textView2)
        val textView3: TextView = v.findViewById(R.id.textView3)
        val textView4: TextView = v.findViewById(R.id.textView4)
        val textView5: TextView = v.findViewById(R.id.textView5)
        val imageViewClose: ImageView = v.findViewById(R.id.imageViewClose)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        imageViewClose.setOnClickListener { dialog.dismiss() }

        Handler().postDelayed({
            otViewModel.getEmpresasById(id)
                .observe(this, Observer {
                    textViewTitle.text = it.nombreTipoOrdenTrabajo
                    textView1.text = it.nombreArea
                    textView2.text = it.nombreJC
                    textView3.text = it.direccion
                    textView4.text = String.format("Nro Orden : %s", it.nroObra)
                    textView5.text = String.format("Fecha Asignación : %s", it.fechaAsignacion)
                    linearLayoutLoad.visibility = View.GONE
                    linearLayoutPrincipal.visibility = View.VISIBLE
                })
        }, 800)
    }
}