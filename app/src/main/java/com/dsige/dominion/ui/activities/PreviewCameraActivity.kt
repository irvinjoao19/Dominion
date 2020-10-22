package com.dsige.dominion.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.OtPhoto
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.adapters.OtMultiPhotoAdapter
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_preview_camera.*
import java.io.File
import javax.inject.Inject


class PreviewCameraActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabOk -> if (galery) {
                formMultiPhoto()
            } else
                formPhoto()
            R.id.imgClose -> if (galery) {
                finish()
            } else {
                startActivity(
                    Intent(this, CameraActivity::class.java)
                        .putExtra("usuarioId", usuarioId)
                        .putExtra("id", id)
                )
                finish()
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel

    private var nameImg: String = ""
    private var usuarioId: Int = 0
    private var id: Int = 0
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
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                        }
                    })

                val otPhotoAdapter =
                    OtMultiPhotoAdapter(object : OnItemClickListener.OtMultiPhotoListener {
                        override fun onItemClick(s: String, view: View, position: Int) {
                            val f = File(Util.getFolder(this@PreviewCameraActivity), s)
                            Picasso.get().load(f)
                                .into(imageView, object : Callback {
                                    override fun onSuccess() {
                                        progressBar.visibility = View.GONE
                                    }

                                    override fun onError(e: Exception?) {
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
            Handler().postDelayed({
                val f = File(Util.getFolder(this), nameImg)
                Picasso.get().load(f)
                    .into(imageView, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                        }
                    })
            }, 200)
        }

        otViewModel.mensajeError.observe(this, {
            Util.toastMensaje(this, it,false)
        })

        otViewModel.mensajeSuccess.observe(this, {
            finish()
        })
    }

    private fun formPhoto() {
        val f = OtPhoto()
        f.otDetalleId = id
        f.nombrePhoto = nameImg
        f.urlPhoto = nameImg
        f.estado = 1
        otViewModel.insertPhoto(f)
    }

    private fun formMultiPhoto() {
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