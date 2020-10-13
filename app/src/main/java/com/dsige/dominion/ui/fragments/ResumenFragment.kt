package com.dsige.dominion.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Filtro
import com.dsige.dominion.data.local.model.Grupo
import com.dsige.dominion.data.local.model.Proveedor
import com.dsige.dominion.data.local.model.Servicio
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.activities.MapActivity
import com.dsige.dominion.ui.adapters.GrupoAdapter
import com.dsige.dominion.ui.adapters.ProveedorAdapter
import com.dsige.dominion.ui.adapters.ServicioAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.dsige.dominion.data.viewModel.ViewModelFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_resumen.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"

class ResumenFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextServicio -> spinnerDialog(1, "Servicio")
            R.id.editTextGrupo -> spinnerDialog(2, "Tipo de Trabajo")
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel

    private var usuarioId: Int = 0
    private var empresaId: Int = 0
    private var personalId: Int = 0
    private var servicioId: Int = 0
    private var nombreServicio: String = ""
    lateinit var f: Filtro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            usuarioId = it.getInt(ARG_PARAM1)
            empresaId = it.getInt(ARG_PARAM2)
            personalId = it.getInt(ARG_PARAM3)
            servicioId = it.getInt(ARG_PARAM4)
            nombreServicio = it.getString(ARG_PARAM5)!!
        }

        f = Filtro(Util.getFecha(), "M", servicioId, 3)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_resumen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)

        editTextServicio.setText(nombreServicio)
        editTextGrupo.setText(String.format("ROTURA"))

        otViewModel.getProveedor(f)

        val proveedorAdapter = ProveedorAdapter(object : OnItemClickListener.ProveedorListener {
            override fun onItemClick(p: Proveedor, view: View, position: Int) {
                when (view.id) {
                    R.id.imgMap -> startActivity(
                        Intent(context!!, MapActivity::class.java)
                            .putExtra("tipo", 1)
                            .putExtra("empresaId", p.empresaId)
                            .putExtra("personalId", personalId)
                    )
                    R.id.imgPeople -> startActivity(
                        Intent(context!!, MapActivity::class.java)
                            .putExtra("tipo", 2)
                            .putExtra("servicioId", f.servicioId)
                            .putExtra("tipoId", f.tipo)
                            .putExtra("proveedorId", p.empresaId)
                    )
                }
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = proveedorAdapter

        otViewModel.getProveedores()
            .observe(viewLifecycleOwner, Observer(proveedorAdapter::submitList))

        otViewModel.mensajeSuccess.observe(viewLifecycleOwner, Observer {
            if (it == "finish") {
                progressBar.visibility = View.GONE
            } else
                progressBar.visibility = View.VISIBLE
        })

        editTextServicio.setOnClickListener(this)
        editTextGrupo.setOnClickListener(this)
    }

    private fun spinnerDialog(tipo: Int, title: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val progressBar: ProgressBar = v.findViewById(R.id.progressBar)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        progressBar.visibility = View.GONE
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, DividerItemDecoration.VERTICAL
            )
        )
        textViewTitulo.text = title
        when (tipo) {
            1 -> {
                val servicioAdapter =
                    ServicioAdapter(object : OnItemClickListener.ServicioListener {
                        override fun onItemClick(s: Servicio, view: View, position: Int) {
                            f.servicioId = s.servicioId
                            otViewModel.getProveedor(f)
                            editTextServicio.setText(s.nombreServicio)
                            dialog.dismiss()
                        }
                    })
                recyclerView.adapter = servicioAdapter
                otViewModel.getServicios().observe(this, Observer {
                    servicioAdapter.addItems(it)
                })
            }
            2 -> {
                val grupoAdapter =
                    GrupoAdapter(object : OnItemClickListener.GrupoListener {
                        override fun onItemClick(g: Grupo, view: View, position: Int) {
                            f.tipo = g.grupoId
                            otViewModel.getProveedor(f)
                            editTextGrupo.setText(g.descripcion)
                            dialog.dismiss()
                        }
                    })
                recyclerView.adapter = grupoAdapter
                otViewModel.getGrupos().observe(this, Observer {
                    grupoAdapter.addItems(it)
                })
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(p1: Int, p2: Int, p3: Int, p4: Int, p5: String) =
            ResumenFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, p1)
                    putInt(ARG_PARAM2, p2)
                    putInt(ARG_PARAM3, p3)
                    putInt(ARG_PARAM4, p4)
                    putString(ARG_PARAM5, p5)
                }
            }
    }
}