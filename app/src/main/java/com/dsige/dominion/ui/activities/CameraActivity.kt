package com.dsige.dominion.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dsige.dominion.R
import com.dsige.dominion.ui.fragments.CameraFragment

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val b = intent.extras
        if (b != null) {
            savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    CameraFragment.newInstance(
                        b.getInt("usuarioId"), b.getInt("id"),b.getInt("tipo")
                    )
                ).commit()
        }
    }
}