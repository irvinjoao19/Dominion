package com.dsige.dominion.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.OtPhoto
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_preview_camera.*
import java.io.File
import javax.inject.Inject

class PreviewCameraActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabOk -> formPhoto()
            R.id.fabClose -> if (galery) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_camera)
        val b = intent.extras
        if (b != null) {
            nameImg = b.getString("nameImg")!!
            usuarioId = b.getInt("usuarioId")
            galery = b.getBoolean("galery")
            id = b.getInt("id")
            bindUI()
        }
    }

    private fun bindUI() {
        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)

        fabClose.setOnClickListener(this)
        fabOk.setOnClickListener(this)
        textViewImg.text = nameImg

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


        otViewModel.mensajeError.observe(this, Observer {
            Util.toastMensaje(this, it)
        })

        otViewModel.mensajeSuccess.observe(this, Observer {
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
}