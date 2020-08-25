package com.dsige.dominion.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Estado
import com.dsige.dominion.data.local.model.Grupo
import com.dsige.dominion.data.local.model.Ot
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.ui.activities.FormActivity
import com.dsige.dominion.ui.adapters.EstadoAdapter
import com.dsige.dominion.ui.adapters.GrupoAdapter
import com.dsige.dominion.ui.adapters.OtAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class MainFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextEstado -> spinnerDialog(1, "Estado")
            R.id.editTextGrupo -> spinnerDialog(2, "Tipo de Trabajo")
            R.id.fab -> startActivity(
                Intent(context, FormActivity::class.java)
                    .putExtra("otId", otId)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("tipo", tipo)
                    .putExtra("empresaId", empresaId)
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel
    private var usuarioId: Int = 0
    private var empresaId: Int = 0
    private var tipo: Int = 0
    private var otId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuarioId = it.getInt(ARG_PARAM1)
            tipo = it.getInt(ARG_PARAM2)
            empresaId = it.getInt(ARG_PARAM3)
        }
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
                startActivity(
                    Intent(context, FormActivity::class.java)
                        .putExtra("otId", o.otId)
                        .putExtra("usuarioId", o.usuarioId)
                        .putExtra("tipo", tipo)
                        .putExtra("empresaId", o.empresaId)
                )
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = otAdapter

        otViewModel.getOts()
            .observe(viewLifecycleOwner, Observer(otAdapter::submitList))
        otViewModel.search.value = null

        otViewModel.getMaxIdOt().observe(viewLifecycleOwner, Observer { s ->
            otId = if (s != null) {
                s + 1
            } else
                1
        })

        editTextGrupo.setOnClickListener(this)
        editTextEstado.setOnClickListener(this)
        fab.setOnClickListener(this)
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
                        editTextEstado.setText(e.abreviatura)
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = estadoAdapter
                otViewModel.getEstados().observe(this, Observer {
                    estadoAdapter.addItems(it)
                })
            }
            2 -> {
                val grupoAdapter =
                    GrupoAdapter(object : OnItemClickListener.GrupoListener {
                        override fun onItemClick(g: Grupo, view: View, position: Int) {
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
        fun newInstance(param1: Int, param2: Int, param3: Int) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                    putInt(ARG_PARAM3, param3)
                }
            }
    }
}