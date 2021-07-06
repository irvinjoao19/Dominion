package com.dsige.dominion.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dominion.BuildConfig
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Material
import com.dsige.dominion.data.local.model.OtDetalle
import com.dsige.dominion.data.local.model.OtPhoto
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.dsige.dominion.helper.Gps
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.adapters.MaterialAdapter
import com.dsige.dominion.ui.adapters.OtPhotoAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_form_detail.*
import java.io.File
import java.io.IOException
import java.util.*
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
        textViewTitulo.text = String.format("Tipo de Material")

        val materialAdapter = MaterialAdapter(object : OnItemClickListener.MaterialListener {
            override fun onItemClick(m: Material, view: View, position: Int) {
                d.tipoMaterialId = m.detalleId
                editTextMaterial.setText(m.descripcion)
                dialog.dismiss()
            }
        })
        recyclerView.adapter = materialAdapter
        otViewModel.getMateriales().observe(this, {
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


        val result = if (grupo == 3 && tipo == 6) {
            a * b
        } else {
            a * b * c
        }

//        val data = if (grupo == 4 && tipo == 6) {
//            result * 10
//        } else {
//            result
//        }

        textViewTotal.text = String.format("Total : %.2f", result)
        d.total = result
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    private fun confirmDelete(o: OtPhoto) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas eliminar esta archivo ?")
            .setPositiveButton("SI") { dialog, _ ->
                otViewModel.deletePhoto(o, this)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun popDetalleMenu(o: OtPhoto, view: View) {
        val popupMenu = PopupMenu(this, view)
        if (o.estado == 1) {
            popupMenu.menu.add(0, 1, 0, getText(R.string.delete))
        }
        if (o.toPdf) {
            popupMenu.menu.add(0, 3, 0, getText(R.string.ver_pdf))
        } else {
            popupMenu.menu.add(0, 2, 0, getText(R.string.ver))
        }


        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> confirmDelete(o)
                2 -> startActivity(
                    Intent(this, PreviewCameraActivity::class.java)
                        .putExtra("nameImg", o.nombrePhoto)
                        .putExtra("tipo", 2)
                )
                3 -> startActivity(
                    Intent(this, PreviewPdfActivity::class.java)
                        .putExtra("pdfName", o.urlPdf)
                )
            }
            false
        }
        popupMenu.show()
    }

    private fun formRegistro(tipo: String) {
        if (d.tipoTrabajoId == 6) {
            d.nombreTipoMaterial = editTextMaterial.text.toString()
        } else {
            d.nroPlaca = editTextMaterial.text.toString().uppercase()
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

        when {
            editTextCantidadPanos.text.toString().isEmpty() -> d.cantPanos = 0f
            else -> d.cantPanos = editTextCantidadPanos.text.toString().toFloat()
        }
        when {
            editTextHorizontal.text.toString().isEmpty() -> d.medHorizontal = 0.0
            else -> d.medHorizontal = editTextHorizontal.text.toString().toDouble()
        }
        when {
            editTextVertical.text.toString().isEmpty() -> d.medVertical = 0.0
            else -> d.medVertical = editTextVertical.text.toString().toDouble()
        }

        if (tipo == "3") {
            val gps = Gps(this)
            if (gps.isLocationEnabled()) {
                if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                    d.latitud = gps.latitude.toString()
                    d.longitud = gps.longitude.toString()
                    otViewModel.validateOtDetalle(d, tipo)
                }
            }
        } else {
            otViewModel.validateOtDetalle(d, tipo)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel
    lateinit var d: OtDetalle
    private var usuarioId: Int = 0
    private var size: Int = 0
    private var maxSize: Int = 10
    private var tipo: Int = 0
    private var grupo: Int = 0
    private var nameImg: String = ""
    private var direccion: String = ""
    private var distrito: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_detail)
        val b = intent.extras
        if (b != null) {
            d = OtDetalle()
            tipo = b.getInt("tipo")
            bindUI(
                b.getInt("otDetalleId"),
                b.getInt("otId"),
                b.getInt("usuarioId"),
                b.getInt("tipo"),
                b.getInt("tipoDesmonte"),
                b.getInt("estado"),
                b.getInt("grupo"),
                b.getInt("servicio"),
                b.getInt("viajeIndebido")
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
     * @grupo
     * 3 ->	ROTURA
     * 4 ->	REPARACION
     * 5 ->	RECOJO
     * @servicio
     * 2 -> Emergencia Baja Tensión
     */
    private fun bindUI(
        detalleId: Int,
        otId: Int,
        u: Int,
        tipo: Int,
        tipoDesmonte: Int,
        e: Int,
        g: Int,
        s: Int,
        viajeIndebido: Int
    ) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = when {
            tipo == 6 -> "Ot Medidas"
            tipoDesmonte == 14 -> "Desmonte Recojido"
            else -> "Genera Ot Desmonte"
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }


        if(viajeIndebido == 1){
            layoutForm.visibility = View.GONE
            fabMenu.visibility = View.GONE
        }

        grupo = g
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
                popDetalleMenu(o, view)
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = otPhotoAdapter

        otViewModel.getOtPhotoById(detalleId).observe(this, {
            size = it.size
            otPhotoAdapter.addItems(it)
            if (it.isNotEmpty())
                fabSave.visibility = View.VISIBLE
            else
                fabSave.visibility = View.GONE

            if (it.size == maxSize) {
                fabCamara.visibility = View.GONE
                fabGaleria.visibility = View.GONE
            }
        })

        otViewModel.getOtDetalleId(detalleId).observe(this, {
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
                editTextCantidadPanos.setText(it.cantPanos.toString())
                editTextHorizontal.setText(it.medHorizontal.toString())
                editTextVertical.setText(it.medVertical.toString())
            }
        })

        otViewModel.mensajeError.observe(this, {
            Util.toastMensaje(this, it, false)
        })

        otViewModel.mensajeSuccess.observe(this) {
            when (it) {
                "Ok" -> {
                    finish()
                    return@observe
                }
                "1" -> {
                    goCamera()
                    return@observe
                }
                "2" -> {
                    goGalery()
                    return@observe
                }
                "3" -> {
                    return@observe
                }
                else -> {
                    startActivity(
                        Intent(this, PreviewCameraActivity::class.java)
                            .putExtra("nameImg", it)
                            .putExtra("usuarioId", usuarioId)
                            .putExtra("id", d.otDetalleId)
                            .putExtra("galery", true)
                            .putExtra("tipo", 0)
                    )
                }
            }
        }

        if (g == 3 && tipo == 6) {
            editTextEspesor.visibility = View.GONE
        }

        if (tipo == 6) {
            textView1.hint = "Tipo de Material"
            editTextMaterial.setOnClickListener(this)
            editTextMaterial.isFocusable = false
            if (s == 2) {
                if (g == 3 || g == 4) {
                    textView2.visibility = View.VISIBLE
                    textView3.visibility = View.VISIBLE
                    textView4.visibility = View.VISIBLE
                    layout2.visibility = View.VISIBLE
                    layout3.visibility = View.VISIBLE
                    layout4.visibility = View.VISIBLE
                }
            }
            editTextEspesor.hint = "Espesor"
        } else {
            textView1.hint = "Placa Vehiculo"
            editTextMaterial.setCompoundDrawables(
                null, null, null, null
            )
            editTextMaterial.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            editTextEspesor.hint = "Altura"
        }

        if (tipoDesmonte == 15) {
            textView1.visibility = View.GONE
            editTextMaterial.visibility = View.GONE
        }

        if (e == 3) {
            fabMenu.visibility = View.GONE
        }

        editTextAncho.addTextChangedListener(this)
        editTextLargo.addTextChangedListener(this)
        editTextEspesor.addTextChangedListener(this)
        fabCamara.setOnClickListener(this)
        fabGaleria.setOnClickListener(this)
        fabSave.setOnClickListener(this)
    }

    private fun goCamera() {
        val gps = Gps(this)
        if (gps.isLocationEnabled()) {
            try {
                val addressObservable = Observable.just(
                    Geocoder(this)
                        .getFromLocation(
                            gps.getLatitude(), gps.getLongitude(), 1
                        )[0]
                )
                addressObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Address> {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onNext(address: Address) {
                            direccion = address.getAddressLine(0).toString()
                            distrito = address.locality.toString()
                            nameImg = Util.getFechaActualForPhoto(usuarioId)
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                                takePictureIntent.resolveActivity(this@FormDetailActivity.packageManager)
                                    ?.also {
                                        val photoFile: File? = try {
                                            Util.createImageFile(nameImg, this@FormDetailActivity)
                                        } catch (ex: IOException) {
                                            null
                                        }
                                        photoFile?.also {
                                            val uriSavedImage = FileProvider.getUriForFile(
                                                this@FormDetailActivity,
                                                BuildConfig.APPLICATION_ID + ".fileprovider",
                                                it
                                            )
                                            takePictureIntent.putExtra(
                                                MediaStore.EXTRA_OUTPUT,
                                                uriSavedImage
                                            )

                                            if (Build.VERSION.SDK_INT >= 24) {
                                                try {
                                                    val m =
                                                        StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                                                    m.invoke(null)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            cameraResultLauncher.launch(takePictureIntent)
                                        }
                                    }
                            }
                        }

                        override fun onError(e: Throwable) {
                            otViewModel.setError(e.toString())
                        }

                        override fun onComplete() {}
                    })
            } catch (e: IOException) {
                otViewModel.setError(e.toString())
            }
        } else {
            gps.showSettingsAlert(this)
        }
    }

    private fun goGalery() {
        otViewModel.setError("Maximo " + (maxSize - size) + " fotos para seleccionar")
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "image/*"
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        //startActivityForResult(i, Permission.GALERY_REQUEST)
        galeryResultLauncher.launch(i)
    }

    private val galeryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val gps = Gps(this)
                    if (gps.isLocationEnabled()) {
                        try {
                            otViewModel.setError("Cargando imagenes seleccionadas...")
                            val addressObservable = Observable.just(
                                Geocoder(this)
                                    .getFromLocation(
                                        gps.getLatitude(), gps.getLongitude(), 1
                                    )[0]
                            )
                            addressObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : Observer<Address> {
                                    override fun onSubscribe(d: Disposable) {}
                                    override fun onNext(address: Address) {
                                        otViewModel.getFilesFromGallery(
                                            size = (maxSize - size),
                                            usuarioId = usuarioId,
                                            context = this@FormDetailActivity,
                                            data = data,
                                            direccion = address.getAddressLine(0).toString(),
                                            distrito = address.locality.toString(),
                                            toPdf = false
                                        )
                                    }

                                    override fun onError(e: Throwable) {}
                                    override fun onComplete() {}
                                })
                        } catch (e: IOException) {
                            otViewModel.setError(e.toString())
                        }
                    } else {
                        gps.showSettingsAlert(this)
                    }
                }
            }
        }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                otViewModel.generatePhoto(
                    nameImg = nameImg,
                    context = this@FormDetailActivity,
                    direccion = direccion,
                    distrito = distrito,
                    id = d.otDetalleId,
                    toPdf = false
                )
            }
        }
}