package com.dsige.dominion.ui.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dsige.dominion.R
import com.dsige.dominion.ui.fragments.EmpresaMapFragment
import com.dsige.dominion.ui.fragments.PersonalMapFragment
import dagger.android.support.DaggerAppCompatActivity

/**
 * @tipo
 * 1 -> empresas
 * 2 -> personal
 */
class MapActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val b = intent.extras
        if (b != null) {
            when (b.getInt("tipo")) {
                1 -> replaceFragment(
                    savedInstanceState, EmpresaMapFragment.newInstance(
                        b.getInt("empresaId"), b.getInt("personalId")
                    )
                )
                2 -> replaceFragment(
                    savedInstanceState, PersonalMapFragment.newInstance(
                        b.getInt("servicioId"), b.getInt("tipoId"), b.getInt("proveedorId")
                    )
                )
            }
        }
    }

    private fun replaceFragment(savedInstanceState: Bundle?, f: Fragment) {
        savedInstanceState ?: supportFragmentManager.beginTransaction()
            .replace(R.id.container, f).commit()
    }
}