package com.dsige.dominion.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Filtro
import com.dsige.dominion.data.local.model.OtPlazoDetalle
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.adapters.OtPlazoDetalleAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_form_detail.*
import kotlinx.android.synthetic.main.activity_form_detail.recyclerView
import kotlinx.android.synthetic.main.fragment_resumen.progressBar
import javax.inject.Inject

class OtPlazoActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ot_plazo)
        val b = intent.extras
        if (b != null) {
            bindUI(
                b.getInt("servicioId"), b.getInt("tipoId"),
                b.getInt("proveedorId"), b.getInt("usuarioId")
            )
        }
    }

    private fun bindUI(servicioId: Int, tipoId: Int, empresaId: Int, usuarioId: Int) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Ot Fuera de Plazo"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)

        val f = Filtro(servicioId, tipoId, empresaId, usuarioId)
        otViewModel.syncOtPlazoDetalle(f)

        val otPlazoDetalleAdapter =
            OtPlazoDetalleAdapter(object : OnItemClickListener.OtPlazoDetalleListener {
                override fun onItemClick(o: OtPlazoDetalle, view: View, position: Int) {
                    if (o.latitud.isNotEmpty() && o.longitud.isNotEmpty()) {
                        startActivity(
                            Intent(this@OtPlazoActivity, OtMapActivity::class.java)
                                .putExtra("latitud", o.latitud)
                                .putExtra("longitud", o.longitud)
                                .putExtra("title", o.empresaContratista)
                        )
                    }
                }
            })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = otPlazoDetalleAdapter

        Handler().postDelayed({
            otViewModel.getOtPlazoDetalles()
                .observe(this, Observer(otPlazoDetalleAdapter::submitList))
        }, 200)

        otViewModel.mensajeSuccess.observe(this, {
            if (it == "finish") {
                progressBar.visibility = View.GONE
            } else
                progressBar.visibility = View.VISIBLE
        })

        otViewModel.mensajeError.observe(this, {
            Util.toastMensaje(this, it)
        })
    }
}