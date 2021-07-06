package com.dsige.dominion.ui.activities

import android.os.Bundle
import com.dsige.dominion.R
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.adapters.TabLayoutAdapter
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_form.*

class FormActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        val b = intent.extras
        if (b != null) {
            bind(
                b.getInt("otId"), b.getInt("usuarioId"), b.getInt("tipo"),
                b.getInt("empresaId"), b.getInt("servicioId"), b.getInt("personalId")
            )
        }
    }

    /**
     * s => Servicio
     * 3 -> MT
     * 1 ->	Obras distribucion
     * 2 ->	Emergencia Baja Tension
     * 3 ->	Emergencia MT
     * 4 ->	Alumbrado Publico
     * 5 ->	MTTO Preventivo
     * 8 ->	Lineas Energizadas
     */
    private fun bind(otId: Int, u: Int, t: Int, e: Int, s: Int, p: Int) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = when (t) {
            3 -> "ROTURA"
            4 -> "REPARACION"
            else -> "RECOJO"
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        when (t) {
            5 -> {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1))
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3))
            }
            else -> {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1))
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab2))
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3))
            }
        }

        val tabLayoutAdapter =
            TabLayoutAdapter.TabLayoutForm(
                supportFragmentManager, tabLayout.tabCount, otId, u, t, e, s, p
            )
        viewPager.adapter = tabLayoutAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                Util.hideKeyboard(this@FormActivity)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}
