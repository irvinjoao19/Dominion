package com.dsige.dominion.ui.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Distrito
import com.dsige.dominion.data.local.model.Ot
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Gps
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.adapters.DistritoAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_general.*
import java.util.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"

class GeneralFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextDistritos -> spinnerDialog()
            R.id.imageViewDireccion -> getAddress()
            R.id.imageViewReferencia -> microPhone("Referencia", 1)
            R.id.imageViewDescripcion -> microPhone("Descripción de Trabajo", 2)
            R.id.fabGenerate -> formOt()
        }
    }

    private fun getAddress() {
        val gps = Gps(context!!)
        if (gps.isLocationEnabled()) {
            progressBarLugar.visibility = View.VISIBLE
            Util.getLocationName(context!!, editTextDireccion, gps.location!!, progressBarLugar)
        } else {
            gps.showSettingsAlert(context!!)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel

    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null
    private var viewPager: ViewPager? = null

    lateinit var t: Ot
    private var otId: Int = 0
    private var usuarioId: Int = 0
    private var tipo: Int = 0
    private var empresaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        t = Ot()

        arguments?.let {
            otId = it.getInt(ARG_PARAM1)
            usuarioId = it.getInt(ARG_PARAM2)
            tipo = it.getInt(ARG_PARAM3)
            empresaId = it.getInt(ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_general, container, false)
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
                editTextNumero.setText(it.nroObra)
                editTextDireccion.setText(it.direccion)
                editTextDistritos.setText(it.nombreDistritoId)
                editTextReferencia.setText(it.referenciaOt)
                editTextDescripcion.setText(it.descripcionOt)
            }
        })

        otViewModel.mensajeError.observe(viewLifecycleOwner, Observer {
            //closeLoad()
            Util.toastMensaje(context!!, it)
        })

        otViewModel.mensajeSuccess.observe(viewLifecycleOwner, Observer {
            viewPager?.currentItem = 1
            Util.toastMensaje(context!!, it)
        })

        editTextDistritos.setOnClickListener(this)
        fabGenerate.setOnClickListener(this)
        imageViewDireccion.setOnClickListener(this)
        imageViewReferencia.setOnClickListener(this)
        imageViewDescripcion.setOnClickListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: Int, param3: Int, param4: Int) =
            GeneralFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                    putInt(ARG_PARAM3, param3)
                    putInt(ARG_PARAM4, param4)
                }
            }
    }

    private fun formOt() {
        val gps = Gps(context!!)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                t.otId = otId
                t.usuarioId = usuarioId
                t.nroObra = editTextNumero.text.toString()
                t.descripcionOt = editTextDescripcion.text.toString()
                t.direccion = editTextDireccion.text.toString()
                t.referenciaOt = editTextReferencia.text.toString()
                t.nombreDistritoId = editTextDistritos.text.toString()
                t.latitud = gps.latitude.toString()
                t.longitud = gps.longitude.toString()
                t.fechaAsignacion = Util.getFecha()
                t.fechaRegistro = Util.getFecha()
                t.horaAsignacion = Util.getHora()
                t.empresaId = empresaId
                t.estado = 2
                otViewModel.validateOt(t)
            }
        } else {
            gps.showSettingsAlert(context!!)
        }
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textView)
        textViewTitle.text = String.format("Enviando..")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun closeLoad() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    private fun spinnerDialog() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val progressBar: ProgressBar = v.findViewById(R.id.progressBar)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val layoutSearch: TextInputLayout = v.findViewById(R.id.layoutSearch)
        val editTextSearch: TextInputEditText = v.findViewById(R.id.editTextSearch)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)

        progressBar.visibility = View.GONE
        layoutSearch.visibility = View.VISIBLE

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        textViewTitulo.text = String.format("Distritos")

        val distritoAdapter = DistritoAdapter(object : OnItemClickListener.DistritoListener {
            override fun onItemClick(d: Distrito, view: View, position: Int) {
                t.distritoId = d.distritoId
                editTextDistritos.setText(d.nombreDistrito)
                dialog.dismiss()
            }
        })
        recyclerView.adapter = distritoAdapter
        otViewModel.getDistritos().observe(this, Observer {
            distritoAdapter.addItems(it)
        })
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int, i1: Int, i2: Int
            ) {

            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int, i1: Int, i2: Int
            ) {

            }

            override fun afterTextChanged(editable: Editable) {
                distritoAdapter.getFilter().filter(editTextSearch.text.toString())
            }
        })
    }

    private fun microPhone(titulo: String, permission: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, titulo)

        try {
            startActivityForResult(intent, permission)
        } catch (a: ActivityNotFoundException) {
            Util.toastMensaje(context!!, "Dispositivo no compatible para esta opción")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            val result: ArrayList<String>? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val y = result?.get(0)!!

            if (requestCode == 1) {
                editTextReferencia.setText(y)
            } else {
                editTextDescripcion.setText(y)
            }
        }
    }
}