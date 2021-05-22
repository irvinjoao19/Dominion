package com.dsige.dominion.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.dsige.dominion.BuildConfig
import com.dsige.dominion.R
import com.dsige.dominion.data.viewModel.OtViewModel
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.dsige.dominion.helper.Permission
import com.dsige.dominion.helper.Util
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_pdf.*
import java.io.File
import javax.inject.Inject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PdfFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var otViewModel: OtViewModel

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pdf, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        otViewModel =
            ViewModelProvider(this, viewModelFactory).get(OtViewModel::class.java)


        buttonCamara.setOnClickListener {
            goGalery()
        }

        otViewModel.mensajeSuccess.observe(viewLifecycleOwner, {
            val pdfName = "${it.substring(0, it.length - 4)}.pdf"
            buttonPdf.isEnabled = true
            buttonPdf.setOnClickListener {
                val file = File(Util.getFolder(requireContext()), pdfName)
                val uri = FileProvider.getUriForFile(
                    requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file
                )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = uri
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
            }
        })
    }

    private fun goGalery() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "image/*"
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(i, Permission.GALERY_REQUEST)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PdfFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Permission.GALERY_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                otViewModel.generarArchivoPdf(
                    1, requireContext(), data,
                    "direccion",
                    "distrito"
                )
            }
        }
    }


}