package com.dsige.dominion.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.OtDetalle
import com.dsige.dominion.data.local.model.OtPhoto
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.adapters.OtMultiPhotoAdapter
import com.dsige.dominion.ui.adapters.OtPhotoBajaTensionAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_preview_camera.*
import kotlinx.android.synthetic.main.fragment_general.*
import java.io.File
import javax.inject.Inject


class PreviewCameraActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabOk -> if (galery) {
                formMultiPhoto()
            } else
                formPhoto()
            R.id.imgClose -> when {
                tipo == 2 -> {
                    finish()
                    return
                }
                galery -> finish()
                else -> {
                    startActivity(
                        Intent(this, CameraActivity::class.java)
                            .putExtra("usuarioId", usuarioId)
                            .putExtra("id", id)
                            .putExtra("tipo", tipo)
                    )
                    finish()
                }
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel

    private var nameImg: String = ""
    private var usuarioId: Int = 0
    private var id: Int = 0
    private var tipo: Int = 0 // 1 -> cabecera 0 -> detalle
    private var galery: Boolean = false

    lateinit var lista: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_camera)
        val b = intent.extras
        if (b != null) {
            usuarioId = b.getInt("usuarioId")
            galery = b.getBoolean("galery")
            id = b.getInt("id")
            tipo = b.getInt("tipo")
            if (galery) {
                lista = Gson().fromJson(
                    b.getString("nameImg")!!,
                    object : TypeToken<List<String?>?>() {}.type
                )
            } else {
                nameImg = b.getString("nameImg")!!
            }
            bindUI()
        }
    }

    private fun bindUI() {
        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)

        fabOk.setOnClickListener(this)
        imgClose.setOnClickListener(this)
        if (galery) {
            Handler().postDelayed({
                val file = File(Util.getFolder(this), lista[0])
                Picasso.get().load(file)
                    .into(imageView, object : Callback {
                        override fun onError(e: Exception?) {}
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }
                    })

                val otPhotoAdapter =
                    OtMultiPhotoAdapter(object : OnItemClickListener.OtMultiPhotoListener {
                        override fun onItemClick(s: String, view: View, position: Int) {
                            val f = File(Util.getFolder(this@PreviewCameraActivity), s)
                            Picasso.get().load(f)
                                .into(imageView, object : Callback {
                                    override fun onError(e: Exception?) {}
                                    override fun onSuccess() {
                                        progressBar.visibility = View.GONE
                                    }
                                })
                        }
                    })
                recyclerView.itemAnimator = DefaultItemAnimator()
                recyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = otPhotoAdapter
                otPhotoAdapter.addItems(lista)
                recyclerView.visibility = View.VISIBLE
            }, 200)
        } else {
            if (nameImg.isEmpty()) {
                Handler().postDelayed({
                    val otPhotoAdapter =
                        OtPhotoBajaTensionAdapter(object : OnItemClickListener.OtPhotoListener {
                            override fun onItemClick(o: OtPhoto, view: View, position: Int) {
                                when (view.id) {
                                    R.id.imgDelete -> confirmPhotoBajaTension(o)
                                    else -> {
                                        val f = File(
                                            Util.getFolder(this@PreviewCameraActivity), o.urlPhoto
                                        )
                                        Picasso.get().load(f)
                                            .into(imageView, object : Callback {
                                                override fun onError(e: Exception?) {}
                                                override fun onSuccess() {
                                                    progressBar.visibility = View.GONE
                                                }
                                            })
                                    }
                                }
                            }
                        })
                    recyclerView.itemAnimator = DefaultItemAnimator()
                    recyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = otPhotoAdapter
                    recyclerView.visibility = View.VISIBLE

                    otViewModel.getOtPhotoBajaTension(id).observe(this, {
                        if (it.isNotEmpty()) {
                            val file = File(Util.getFolder(this), it[0].urlPhoto)
                            Picasso.get().load(file)
                                .into(imageView, object : Callback {
                                    override fun onError(e: Exception?) {}
                                    override fun onSuccess() {
                                        progressBar.visibility = View.GONE
                                    }
                                })
                        } else {
                            finish()
                        }
                        otPhotoAdapter.addItems(it)
                    })
                }, 200)
            } else {
                Handler().postDelayed({
                    val url = Util.UrlFoto + nameImg
                    Picasso.get().load(url)
                        .into(imageView, object : Callback {
                            override fun onError(e: Exception?) {
                                val f = File(Util.getFolder(this@PreviewCameraActivity), nameImg)
                                Picasso.get().load(f)
                                    .into(imageView, object : Callback {
                                        override fun onError(e: Exception?) {}
                                        override fun onSuccess() {
                                            progressBar.visibility = View.GONE
                                        }
                                    })
                            }

                            override fun onSuccess() {
                                progressBar.visibility = View.GONE
                            }
                        })
                }, 200)
            }
        }


        if (tipo == 2) {
            fabOk.visibility = View.GONE
        }

        otViewModel.mensajeError.observe(this, {
            Util.toastMensaje(this, it, false)
        })

        otViewModel.mensajeSuccess.observe(this, {
            finish()
        })
    }

    private fun formPhoto() {
        if (tipo == 1) {
            val t = OtDetalle()
            t.otId = id
            t.tipoMaterialId = 24
            t.tipoTrabajoId = 6
            t.estado = 1

            val f = OtPhoto()
            f.nombrePhoto = nameImg
            f.urlPhoto = nameImg
            f.estado = 1
            f.otId = id
            val fotos = ArrayList<OtPhoto>()
            fotos.add(f)
            t.photos = fotos
            otViewModel.insertOtPhotoCabecera(t)
        } else {
            val f = OtPhoto()
            f.otDetalleId = id
            f.nombrePhoto = nameImg
            f.urlPhoto = nameImg
            f.estado = 1
            otViewModel.insertPhoto(f)
        }
    }

    private fun formMultiPhoto() {
        if (tipo == 1) {
            val t = OtDetalle()
            t.otId = id
            t.tipoMaterialId = 24
            t.tipoTrabajoId = 6
            t.estado = 1

            val fotos = ArrayList<OtPhoto>()
            for (d: String in lista) {
                val p = OtPhoto()
                p.nombrePhoto = d
                p.urlPhoto = d
                p.otId = id
                p.estado = 1
                fotos.add(p)
            }
            t.photos = fotos
            otViewModel.insertOtPhotoCabecera(t)
        } else {
            val f: ArrayList<OtPhoto> = ArrayList()
            if (lista.size > 0)
                for (d: String in lista) {
                    val p = OtPhoto()
                    p.otDetalleId = id
                    p.nombrePhoto = d
                    p.urlPhoto = d
                    p.estado = 1
                    f.add(p)
                }
            otViewModel.insertMultiPhoto(f)
        }
    }

    private fun confirmPhotoBajaTension(o: OtPhoto) {
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