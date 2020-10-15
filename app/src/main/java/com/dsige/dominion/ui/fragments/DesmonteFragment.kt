package com.dsige.dominion.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager

import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.OtDetalle
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.activities.FormDetailActivity
import com.dsige.dominion.ui.adapters.OtDetalleAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.dsige.dominion.ui.activities.OtMapActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_desmonte.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class DesmonteFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabDesmonteR -> goDesmonteActivity(14)
            R.id.fabDesmonteG -> goDesmonteActivity(15)
        }
    }

    private fun goDesmonteActivity(t: Int) {
        if (estado != 0) {
            startActivity(
                Intent(context, FormDetailActivity::class.java)
                    .putExtra("otDetalleId", otDetalleId)
                    .putExtra("otId", otId)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("tipo", 7)
                    .putExtra("tipoDesmonte", t)
            )
        } else {
            viewPager?.currentItem = 0
            Util.toastMensaje(context!!, "Completar primer formulario")
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel
    private var viewPager: ViewPager? = null

    private var otId: Int = 0
    private var otDetalleId: Int = 0
    private var usuarioId: Int = 0
    private var tipo: Int = 0
    private var estado: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            otId = it.getInt(ARG_PARAM1)
            usuarioId = it.getInt(ARG_PARAM2)
            tipo = it.getInt(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_desmonte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        viewPager = activity!!.findViewById(R.id.viewPager)
        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)

        otViewModel.getOtById(otId).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                estado = it.estado
            }
        })

        otViewModel.getMaxIdOtDetalle().observe(viewLifecycleOwner, Observer { s ->
            otDetalleId = if (s != null) {
                s + 1
            } else
                1
        })

        val otDetalleAdapter = OtDetalleAdapter(object : OnItemClickListener.OtDetalleListener {
            override fun onItemClick(o: OtDetalle, view: View, position: Int) {
                when (view.id) {
                    R.id.imgEdit -> startActivity(
                        Intent(context, FormDetailActivity::class.java)
                            .putExtra("otDetalleId", o.otDetalleId)
                            .putExtra("otId", o.otId)
                            .putExtra("usuarioId", usuarioId)
                            .putExtra("tipo", o.tipoTrabajoId)
                            .putExtra("tipoDesmonte", o.tipoDesmonteId)
                            .putExtra("estado", o.estado)
                    )
                    R.id.imgDelete -> if (o.estado == 3) {
                        if (o.latitud.isNotEmpty() || o.longitud.isNotEmpty()) {
                            startActivity(
                                Intent(context, OtMapActivity::class.java)
                                    .putExtra("latitud", o.latitud)
                                    .putExtra("longitud", o.longitud)
                                    .putExtra("title", "Desmonte")
                                    .putExtra("mode", "walking")
                            )
                        } else {
                            otViewModel.setError("Desmonte no cuenta con Coordenadas")
                        }
                    } else {
                        confirmDelete(o)
                    }
                }
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = otDetalleAdapter

        otViewModel.getOtDetalleById(otId, 7)
            .observe(viewLifecycleOwner, Observer(otDetalleAdapter::submitList))

        otViewModel.mensajeError.observe(viewLifecycleOwner, {
            Util.toastMensaje(context!!, it)
        })

        fabDesmonteR.setOnClickListener(this)
        fabDesmonteG.setOnClickListener(this)
    }

    private fun confirmDelete(o: OtDetalle) {
        val dialog = MaterialAlertDialogBuilder(context!!)
            .setTitle("Mensaje")
            .setMessage("Se eliminaran las fotos que estan incluidas en este desmonte ?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                otViewModel.deleteOtDetalle(o, context!!)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: Int, param3: Int) =
            DesmonteFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                    putInt(ARG_PARAM3, param3)
                }
            }
    }
}
