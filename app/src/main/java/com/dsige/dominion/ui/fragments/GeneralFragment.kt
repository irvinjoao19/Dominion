package com.dsige.dominion.ui.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Distrito
import com.dsige.dominion.data.local.model.Ot
import com.dsige.dominion.data.local.model.Sed
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.dsige.dominion.helper.Gps
import com.dsige.dominion.helper.Permission
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.activities.CameraActivity
import com.dsige.dominion.ui.activities.PreviewCameraActivity
import com.dsige.dominion.ui.adapters.DistritoAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_general.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"
private const val ARG_PARAM6 = "param6"

class GeneralFragment : DaggerFragment(), View.OnClickListener, TextView.OnEditorActionListener {

    override fun onClick(v: View) {
        if (v is MaterialCheckBox) {
            val checked: Boolean = v.isChecked
            when (v.id) {
                R.id.checkViaje -> {
                    if (checked) {
                        fabCamara.visibility = View.VISIBLE
                        fabGaleria.visibility = View.VISIBLE
                    } else {
                        confirmDeletePhotos()
                    }
                }
            }
            return
        }

        when (v.id) {
            R.id.editTextDistritos -> spinnerDialog()
            R.id.imageViewDireccion -> getAddress()
            R.id.imageViewReferencia -> microPhone("Referencia", 1)
            R.id.imageViewDescripcion -> microPhone("Descripción de Trabajo", 3)
            R.id.fabGenerate -> formOt()
            R.id.imageViewSed -> if (t.distritoIdGps != 0) {
                clearSed()
            } else {
                if (editTextSed.text.toString().isNotEmpty()) {
                    searchSed(editTextSed.text.toString())
                } else
                    otViewModel.setError("Ingrese Nro de Sed")
            }

            R.id.fabPreviewCamera -> goPreviewPhoto()
            R.id.fabCamara -> if (t.estado != 0) {
                goCamera()
            } else
                otViewModel.setError("Completar formulario")
            R.id.fabGaleria -> if (t.estado != 0) {
                goGalery()
            } else
                otViewModel.setError("Completar formulario")
        }
    }

    override fun onEditorAction(v: TextView, p1: Int, p2: KeyEvent?): Boolean {
        val sed = v.text.toString()
        if (sed.isNotEmpty()) {
            searchSed(sed)
        }
        return false
    }

    private fun getAddress() {
        val gps = Gps(requireContext())
        if (gps.isLocationEnabled()) {
            progressBarLugar.visibility = View.VISIBLE
            Util.getLocationName(
                requireContext(),
                editTextDireccion,
                editTextDistritos,
                gps.getLatitude(),
                gps.getLongitude(),
                progressBarLugar,
                servicioId
            )
        } else {
            gps.showSettingsAlert(requireContext())
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel

    lateinit var builder: AlertDialog.Builder
    private var viewPager: ViewPager? = null

    lateinit var t: Ot
    private var otId: Int = 0
    private var usuarioId: Int = 0
    private var tipo: Int = 0
    private var empresaId: Int = 0
    private var servicioId: Int = 0
    private var personalId: Int = 0
    private var size: Int = 0
    private var maxSize: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        t = Ot()

        arguments?.let {
            otId = it.getInt(ARG_PARAM1)
            usuarioId = it.getInt(ARG_PARAM2)
            tipo = it.getInt(ARG_PARAM3)
            empresaId = it.getInt(ARG_PARAM4)
            servicioId = it.getInt(ARG_PARAM5)
            personalId = it.getInt(ARG_PARAM6)
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
        viewPager = requireActivity().findViewById(R.id.viewPager)
        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)


        if (servicioId == 2) {
            editTextNumero.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            layoutSuministro.visibility = View.VISIBLE
            layoutSed.visibility = View.VISIBLE
            imageViewSed.visibility = View.VISIBLE
            checkViaje.visibility = View.VISIBLE

            otViewModel.getCountOtPhotoBajaTension(otId).observe(viewLifecycleOwner, {
                size = it
                if (it > 0) {
                    fabPreviewCamera.visibility = View.VISIBLE
                } else {
                    fabPreviewCamera.visibility = View.GONE
                }
                if (it == maxSize) {
                    fabCamara.visibility = View.INVISIBLE
                    fabGaleria.visibility = View.INVISIBLE
                } else {
                    if (checkViaje.isChecked) {
                        fabCamara.visibility = View.VISIBLE
                        fabGaleria.visibility = View.VISIBLE
                    }
                }
            })
        }else{
            editTextNumero.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        }
        otViewModel.getOtById(otId).observe(viewLifecycleOwner, {
            if (it != null) {
                t = it
                editTextNumero.setText(it.nroObra)
                editTextNumero.isEnabled = false
                editTextDireccion.setText(it.direccion)
                editTextDistritos.setText(it.nombreDistritoId)
                editTextReferencia.setText(it.referenciaOt)
                editTextDescripcion.setText(it.descripcionOt)
                if (it.estado == 0) {
                    fabGenerate.visibility = View.GONE
                }
                editTextSuministro.setText(it.suministroTD)
                editTextSed.setText(it.nroSed)

                if (it.viajeIndebido == 1) {
                    checkViaje.isChecked = true
                    fabCamara.visibility = View.VISIBLE
                    fabGaleria.visibility = View.VISIBLE
                }
                if (it.fechaInicioTrabajo.isEmpty()) {
                    t.fechaInicioTrabajo = Util.getFechaActual()
                }
            } else {
                t.fechaInicioTrabajo = Util.getFechaActual()
            }
        })

        otViewModel.mensajeError.observe(viewLifecycleOwner, {
            //closeLoad()
            Util.toastMensaje(context!!, it, false)
        })

        otViewModel.mensajeSuccess.observe(viewLifecycleOwner, {
            startActivity(
                Intent(requireContext(), PreviewCameraActivity::class.java)
                    .putExtra("id", otId)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("tipo", 1)
                    .putExtra("galery", true)
                    .putExtra("nameImg", it)
            )
        })

        otViewModel.mensajeGeneral.observe(viewLifecycleOwner) {
            if (servicioId == 2) {
                if (checkViaje.isChecked) {
                    if (size == 0) {
                        goCamera()
                    }
                } else {
                    viewPager?.currentItem = 1
                    Util.toastMensaje(context!!, it, false)
                }
            } else {
                viewPager?.currentItem = 1
                Util.toastMensaje(context!!, it, false)
            }
        }

        editTextDistritos.setOnClickListener(this)
        fabGenerate.setOnClickListener(this)
        imageViewDireccion.setOnClickListener(this)
        imageViewReferencia.setOnClickListener(this)
        imageViewDescripcion.setOnClickListener(this)

        imageViewSed.setOnClickListener(this)
        checkViaje.setOnClickListener(this)
        fabCamara.setOnClickListener(this)
        fabGaleria.setOnClickListener(this)
        editTextSed.setOnEditorActionListener(this)
        fabPreviewCamera.setOnClickListener(this)
    }

    private fun formOt() {
        val gps = Gps(context!!)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                t.otId = otId
                t.tipoOrdenId = tipo
                t.nombreTipoOrden = when (tipo) {
                    3 -> "ROTURA"
                    4 -> "REPARACION"
                    else -> "RECOJO"
                }
                t.usuarioId = usuarioId
                t.personalJCId = usuarioId
                t.nroObra = editTextNumero.text.toString().toUpperCase(Locale.getDefault())
                t.descripcionOt = editTextDescripcion.text.toString()
                t.direccion = editTextDireccion.text.toString()
                t.referenciaOt = editTextReferencia.text.toString()
                t.nombreDistritoId = editTextDistritos.text.toString()
                t.suministroTD = editTextSuministro.text.toString()
                t.nroSed = editTextSed.text.toString()
                t.viajeIndebido = if (checkViaje.isChecked) 1 else 0
                t.latitud = gps.latitude.toString()
                t.longitud = gps.longitude.toString()
                t.fechaAsignacion = Util.getFecha()
                t.fechaRegistro = Util.getFecha()
                t.horaAsignacion = Util.getHora()
                t.empresaId = empresaId
                t.servicioId = servicioId
                t.estadoId = 4
                t.estado = 2
                t.activeNotificacion = 1
                t.fechaXOt = Util.getFecha()
                otViewModel.validateOt(t)
            }
        } else {
            gps.showSettingsAlert(context!!)
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
        otViewModel.getDistritos().observe(this, {
            distritoAdapter.addItems(it)
        })
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(c: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(c: CharSequence, i: Int, i1: Int, i2: Int) {}
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
            Util.toastMensaje(context!!, "Dispositivo no compatible para esta opción", false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == Permission.GALERY_REQUEST) {
                if (data != null) {
                    val gps = Gps(requireContext())
                    if (gps.isLocationEnabled()) {
                        try {
                            otViewModel.setError("Cargando imagenes seleccionadas...")
                            val addressObservable = Observable.just(
                                Geocoder(requireContext())
                                    .getFromLocation(
                                        gps.getLatitude(), gps.getLongitude(), 1
                                    )[0]
                            )
                            addressObservable.subscribeOn(Schedulers.io())
                                .delay(1000, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : Observer<Address> {
                                    override fun onSubscribe(d: Disposable) {}
                                    override fun onNext(address: Address) {
                                        otViewModel.generarArchivo(
                                            (maxSize - size), usuarioId, requireContext(), data,
                                            address.getAddressLine(0).toString(),
                                            address.locality.toString()
                                        )
                                    }

                                    override fun onError(e: Throwable) {}
                                    override fun onComplete() {}
                                })
                        } catch (e: IOException) {
                            otViewModel.setError(e.toString())
                        }
                    } else {
                        gps.showSettingsAlert(requireContext())
                    }
                }

            } else {
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

    companion object {
        @JvmStatic
        fun newInstance(p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int) =
            GeneralFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, p1)
                    putInt(ARG_PARAM2, p2)
                    putInt(ARG_PARAM3, p3)
                    putInt(ARG_PARAM4, p4)
                    putInt(ARG_PARAM5, p5)
                    putInt(ARG_PARAM6, p6)
                }
            }
    }

    private fun searchSed(sed: String) {
        otViewModel.getSed(sed)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Sed> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onNext(s: Sed) {
                    t.nroSed = s.codigo
                    t.distritoIdGps = s.distritoId
                    t.distritoId = s.distritoId
                    editTextDistritos.setText(s.distrito)
                    editTextSed.isEnabled = false
                    imageViewSed.setImageResource(R.drawable.ic_remove)
                }

                override fun onError(e: Throwable) {
                    otViewModel.setError(e.message.toString())
                }
            })
    }

    private fun clearSed() {
        t.nroSed = ""
        t.distritoIdGps = 0
        t.distritoId = 0
        editTextDistritos.text = null
        editTextSed.isEnabled = true
        editTextSed.text = null

        editTextSed.post {
            editTextSed.requestFocusFromTouch()
            val lManager: InputMethodManager =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            lManager.showSoftInput(editTextSed, 0)
        }

        imageViewSed.setImageResource(R.drawable.ic_send)
    }

    private fun goPreviewPhoto() {
        startActivity(
            Intent(requireContext(), PreviewCameraActivity::class.java)
                .putExtra("id", otId)
                .putExtra("usuarioId", usuarioId)
                .putExtra("tipo", 2)
                .putExtra("galery", false)
                .putExtra("nameImg", "")
        )
    }

    private fun goCamera() {
        startActivity(
            Intent(context, CameraActivity::class.java)
                .putExtra("id", otId)
                .putExtra("usuarioId", usuarioId)
                .putExtra("tipo", 1)
        )
    }

    private fun goGalery() {
        otViewModel.setError("Maximo " + (maxSize - size) + " fotos para seleccionar")
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "image/*"
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(i, Permission.GALERY_REQUEST)
    }

    private fun confirmDeletePhotos() {
        val dialog = MaterialAlertDialogBuilder(context!!)
            .setTitle("Mensaje")
            .setMessage("Al desactivar se eliminaran las fotos pendientes ?")
            .setPositiveButton("SI") { dialog, _ ->
                otViewModel.deleteOtPhotoBajaTension(otId, context!!)
                fabCamara.visibility = View.GONE
                fabGaleria.visibility = View.GONE
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                checkViaje.isChecked = true
                fabCamara.visibility = View.VISIBLE
                fabGaleria.visibility = View.VISIBLE
                dialog.cancel()
            }
        dialog.show()
    }
}