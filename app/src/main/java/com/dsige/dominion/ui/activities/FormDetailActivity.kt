package com.dsige.dominion.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
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
import com.dsige.dominion.data.local.model.Material
import com.dsige.dominion.data.local.model.OtDetalle
import com.dsige.dominion.data.local.model.OtPhoto
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Permission
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.adapters.MaterialAdapter
import com.dsige.dominion.ui.adapters.OtPhotoAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_form_detail.*
import javax.inject.Inject

class FormDetailActivity : DaggerAppCompatActivity(), View.OnClickListener, TextWatcher {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextMaterial -> spinnerDialog()
            R.id.fabCamara -> formRegistro("1")
            R.id.fabGaleria -> formRegistro("2")
            R.id.fabSave -> formRegistro("3")
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel
    lateinit var d: OtDetalle
    private var usuarioId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_detail)
        val b = intent.extras
        if (b != null) {
            d = OtDetalle()
            bindUI(
                b.getInt("otDetalleId"),
                b.getInt("otId"),
                b.getInt("usuarioId"),
                b.getInt("tipo"),
                b.getInt("tipoDesmonte")
            )
        }
    }

    /**
     * @tipo
     * 6 ->	Medidas
     * 7 ->	DESMONTE
     * @tipoDesmonte
     * 14 -> Desmonte Recojido
     * 15 -> Genera Ot Desmonte
     */
    private fun bindUI(detalleId: Int, otId: Int, u: Int, tipo: Int, tipoDesmonte: Int) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = when {
            tipo == 6 -> "Ot Medidas"
            tipoDesmonte == 14 -> "Desmonte Recojido"
            else -> "Genera Ot Desmonte"
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        usuarioId = u
        d.otDetalleId = detalleId
        d.otId = otId
        d.tipoTrabajoId = tipo
        d.tipoDesmonteId = tipoDesmonte
        d.nombreTipoDemonte = when (tipoDesmonte) {
            14 -> "Desmonte Recojido"
            else -> "Genera Ot Desmonte"
        }

        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)

        val otPhotoAdapter = OtPhotoAdapter(object : OnItemClickListener.OtPhotoListener {
            override fun onItemClick(o: OtPhoto, view: View, position: Int) {
                confirmDelete(o)
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = otPhotoAdapter

        otViewModel.getOtPhotoById(detalleId)
            .observe(this, Observer {
                otPhotoAdapter.addItems(it)
                if (it.isNotEmpty())
                    fabSave.visibility = View.VISIBLE
                else
                    fabSave.visibility = View.GONE
            })

        otViewModel.getOtDetalleId(detalleId).observe(this, Observer {
            if (it != null) {
                d = it
                if (it.tipoTrabajoId == 6) {
                    editTextMaterial.setText(it.nombreTipoMaterial)
                } else {
                    editTextMaterial.setText(it.nroPlaca)
                }
                editTextAncho.setText(it.ancho.toString())
                editTextLargo.setText(it.largo.toString())
                editTextEspesor.setText(it.espesor.toString())
            }
        })

        otViewModel.mensajeError.observe(this, Observer {
            Util.toastMensaje(this, it)
        })

        otViewModel.mensajeSuccess.observe(this, Observer { s ->
            when (s) {
                "Ok" -> {
                    finish()
                    return@Observer
                }
                "1" -> {
                    goCamera()
                    return@Observer
                }
                "2" -> {
                    goGalery()
                    return@Observer
                }
                else -> {
                    startActivity(
                        Intent(this, PreviewCameraActivity::class.java)
                            .putExtra("nameImg", s)
                            .putExtra("usuarioId", usuarioId)
                            .putExtra("id", d.otDetalleId)
                            .putExtra("galery", true)
                    )
                }
            }
        })

        if (tipo == 6) {
            textView1.hint = "Tipo de Material"
            editTextMaterial.setOnClickListener(this)
            editTextMaterial.isFocusable = false
        } else {
            textView1.hint = "Placa Vehiculo"
            editTextMaterial.setCompoundDrawables(
                null, null, null, null
            )
            editTextMaterial.inputType = InputType.TYPE_CLASS_TEXT
        }

        if (tipoDesmonte == 15) {
            textView1.visibility = View.GONE
            editTextMaterial.visibility = View.GONE
        }

        editTextAncho.addTextChangedListener(this)
        editTextLargo.addTextChangedListener(this)
        editTextEspesor.addTextChangedListener(this)
        fabCamara.setOnClickListener(this)
        fabGaleria.setOnClickListener(this)
        fabSave.setOnClickListener(this)
    }

    private fun spinnerDialog() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.dialog_combo, null)

        val progressBar: ProgressBar = v.findViewById(R.id.progressBar)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        progressBar.visibility = View.GONE
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

        val materialAdapter = MaterialAdapter(object : OnItemClickListener.MaterialListener {
            override fun onItemClick(m: Material, view: View, position: Int) {
                d.tipoMaterialId = m.detalleId
                editTextMaterial.setText(m.descripcion)
                dialog.dismiss()
            }
        })
        recyclerView.adapter = materialAdapter
        otViewModel.getMateriales().observe(this, Observer {
            materialAdapter.addItems(it)
        })
    }

    override fun afterTextChanged(p0: Editable?) {
        val a = when {
            editTextAncho.text.toString().isEmpty() -> 0.0
            else -> editTextAncho.text.toString().toDouble()
        }
        val b = when {
            editTextLargo.text.toString().isEmpty() -> 0.0
            else -> editTextLargo.text.toString().toDouble()
        }

        val c = when {
            editTextEspesor.text.toString().isEmpty() -> 0.0
            else -> editTextEspesor.text.toString().toDouble()
        }

        val result = a * b * c
        textViewTotal.text = String.format("Total : %.2f", result)
        d.total = result
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    private fun formRegistro(tipo: String) {
        if (d.tipoTrabajoId == 6) {
            d.nombreTipoMaterial = editTextMaterial.text.toString()
        } else {
            d.nroPlaca = editTextMaterial.text.toString()
        }

        d.estado = if (tipo == "3") 1 else 2

        when {
            editTextAncho.text.toString().isEmpty() -> d.ancho = 0.0
            else -> d.ancho = editTextAncho.text.toString().toDouble()
        }
        when {
            editTextLargo.text.toString().isEmpty() -> d.largo = 0.0
            else -> d.largo = editTextLargo.text.toString().toDouble()
        }
        when {
            editTextEspesor.text.toString().isEmpty() -> d.espesor = 0.0
            else -> d.espesor = editTextEspesor.text.toString().toDouble()
        }
        otViewModel.validateOtDetalle(d, tipo)
    }

    private fun goCamera() {
        startActivity(
            Intent(this, CameraActivity::class.java)
                .putExtra("usuarioId", usuarioId)
                .putExtra("id", d.otDetalleId)
        )
    }

    private fun goGalery() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(i, Permission.GALERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Permission.GALERY_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                otViewModel.generarArchivo(
                    Util.getFechaActualForPhoto(usuarioId),
                    this,
                    data
                )
            }
        }
    }

    private fun confirmDelete(o: OtPhoto) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas eliminar esta foto ?")
            .setPositiveButton("SI") { dialog, _ ->
                otViewModel.deletePhoto(o, this)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }
}