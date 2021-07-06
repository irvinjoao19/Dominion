package com.dsige.dominion.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.activities.FormActivity
import com.dsige.dominion.ui.activities.OtMapActivity
import com.dsige.dominion.ui.adapters.EstadoAdapter
import com.dsige.dominion.ui.adapters.GrupoAdapter
import com.dsige.dominion.ui.adapters.OtAdapter
import com.dsige.dominion.ui.adapters.ServicioAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"
private const val ARG_PARAM6 = "param6"
private const val ARG_PARAM7 = "param7"

class MainFragment : DaggerFragment(), View.OnClickListener, TextView.OnEditorActionListener {

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (v.text.isNotEmpty()) {
            f.search = v.text.toString()
            val json = Gson().toJson(f)
            otViewModel.search.value = json
        }
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextEstado -> spinnerDialog(1, "Estado")
            R.id.editTextGrupo -> spinnerDialog(2, "Tipo de Trabajo")
            R.id.editTextServicio -> spinnerDialog(3, "Servicios")
            R.id.fab -> if (f.servicioId != 0) {
                if (f.tipo != 0) {
                    startActivity(
                        Intent(context, FormActivity::class.java)
                            .putExtra("otId", otId)
                            .putExtra("usuarioId", usuarioId)
                            .putExtra("tipo", f.tipo)
                            .putExtra("empresaId", empresaId)
                            .putExtra("servicioId", f.servicioId)
                    )
                } else {
                    otViewModel.setError("Seleccione Tipo de Trabajo")
                }
            } else
                otViewModel.setError("Seleccione Servicio")
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel
    private var usuarioId: Int = 0
    private var empresaId: Int = 0
    private var personalId: Int = 0
    private var tipo: Int = 0
    private var otId: Int = 0
    private var servicioId: Int = 0
    private var nombreServicio: String = ""
    private var nombreTipo: String = ""
    lateinit var f: Filtro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuarioId = it.getInt(ARG_PARAM1)
            empresaId = it.getInt(ARG_PARAM2)
            personalId = it.getInt(ARG_PARAM3)
            servicioId = it.getInt(ARG_PARAM4)

            nombreServicio = it.getString(ARG_PARAM5, "")
            tipo = it.getInt(ARG_PARAM6)
            nombreTipo = it.getString(ARG_PARAM7, "")
        }


        f = Filtro("", 3, tipo)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)

        val otAdapter = OtAdapter(object : OnItemClickListener.OtListener {
            override fun onItemClick(o: Ot, view: View, position: Int) {
//                if (o.estado != 0) {
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menu.add(0, 1, 0, getText(R.string.edit))
                popupMenu.menu.add(0, 2, 0, getText(R.string.mapa))
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        1 -> startActivity(
                            Intent(context, FormActivity::class.java)
                                .putExtra("otId", o.otId)
                                .putExtra("usuarioId", usuarioId)
                                .putExtra("tipo", o.tipoOrdenId)
                                .putExtra("empresaId", o.empresaId)
                                .putExtra("servicioId", o.servicioId)
                                .putExtra("personalId", o.personalJCId)
                        )
                        2 -> if (o.latitud.isNotEmpty() || o.longitud.isNotEmpty()) {
                            startActivity(
                                Intent(context, OtMapActivity::class.java)
                                    .putExtra("latitud", o.latitud)
                                    .putExtra("longitud", o.longitud)
                                    .putExtra("title", o.nombreEmpresa)
                                    .putExtra("mode", "driving")
                            )
                        } else {
                            otViewModel.setError("Ot no cuenta con Coordenadas")
                        }
                    }
                    false
                }
                popupMenu.show()
//                } else
//                    otViewModel.setError("Ot Cerrada")
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = otAdapter

        otViewModel.getOts().observe(viewLifecycleOwner){
            otAdapter.submitData(lifecycle, it)
        }

        editTextGrupo.setText(nombreTipo)
        editTextEstado.setText(String.format("Enviados al Jefe de Cuadrilla"))

        editTextServicio.setText(nombreServicio)
        f.servicioId = servicioId
        otViewModel.search.value = Gson().toJson(f)

        otViewModel.getMaxIdOt().observe(viewLifecycleOwner, { s ->
            otId = if (s != null) {
                s + 1
            } else
                1
        })

        editTextGrupo.setOnClickListener(this)
        editTextEstado.setOnClickListener(this)
        editTextServicio.setOnClickListener(this)
        editTextSearch.setOnEditorActionListener(this)
        fab.setOnClickListener(this)

        otViewModel.mensajeError.observe(viewLifecycleOwner, {
            Util.toastMensaje(requireContext(), it, false)
        })
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
                val estadoAdapter = EstadoAdapter(object : OnItemClickListener.EstadoListener {
                    override fun onItemClick(e: Estado, view: View, position: Int) {
                        f.estadoId = e.estadoId
                        otViewModel.search.value = Gson().toJson(f)
                        editTextEstado.setText(e.abreviatura)
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = estadoAdapter
                otViewModel.getEstados().observe(this, {
                    estadoAdapter.addItems(it)
                })
            }
            2 -> {
                if (f.servicioId == 0) {
                    otViewModel.setError("Seleccione Servicio Primero")
                    dialog.dismiss()
                    return
                }

                val grupoAdapter =
                    GrupoAdapter(object : OnItemClickListener.GrupoListener {
                        override fun onItemClick(g: Grupo, view: View, position: Int) {
                            f.tipo = g.grupoId
                            otViewModel.search.value = Gson().toJson(f)
                            editTextGrupo.setText(g.descripcion)
                            dialog.dismiss()
                        }
                    })
                recyclerView.adapter = grupoAdapter

                otViewModel.getGrupoByServicioId(f.servicioId).observe(this, {
                    grupoAdapter.addItems(it)
                })
            }
            3 -> {
                val servicioAdapter =
                    ServicioAdapter(object : OnItemClickListener.ServicioListener {
                        override fun onItemClick(s: Servicio, view: View, position: Int) {
                            f.servicioId = s.servicioId
                            f.tipo = 0
                            editTextGrupo.text = null
                            otViewModel.search.value = Gson().toJson(f)
                            editTextServicio.setText(s.nombreServicio)
                            dialog.dismiss()

                            spinnerDialog(2, "Tipo de Trabajo")
                        }
                    })
                recyclerView.adapter = servicioAdapter
                otViewModel.getServicios().observe(this, {
                    servicioAdapter.addItems(it)
                })
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(p1: Int, p2: Int, p3: Int, p4: Int, p5: String, p6: Int, p7: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, p1)
                    putInt(ARG_PARAM2, p2)
                    putInt(ARG_PARAM3, p3)
                    putInt(ARG_PARAM4, p4)
                    putString(ARG_PARAM5, p5)
                    putInt(ARG_PARAM6, p6)
                    putString(ARG_PARAM7, p7)
                }
            }
    }
}