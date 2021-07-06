package com.dsige.dominion.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.dsige.dominion.R
import com.dsige.dominion.helper.Util
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import kotlinx.android.synthetic.main.activity_preview_pdf.*
import java.io.File

class PreviewPdfActivity : AppCompatActivity(), OnErrorListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_pdf)
        val b = intent.extras
        if (b != null){
            bindUI(b.getString("pdfName","prueba.pdf"))
        }
    }

    private fun bindUI(pdfName:String){
        setSupportActionBar(toolbar)
        supportActionBar!!.title = pdfName
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                val file = File(Util.getFolder(this), pdfName)
                pdfView.fromFile(file)
                    .onError(this)
                    .load()
                progressBar.visibility = View.GONE
            }, 1000)
        }
    }

    override fun onError(t: Throwable?) {
        textViewMensaje.visibility = View.VISIBLE
    }

}