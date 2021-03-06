package com.dsige.dominion.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dsige.dominion.ui.fragments.*

abstract class TabLayoutAdapter {

    class TabLayoutForm(
        fm: FragmentManager,
        var numberOfTabs: Int, var otId: Int, var usuarioId: Int,
        var tipo: Int, var empresaId: Int, var servicioId: Int,var personalId:Int
    ) :
        FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GeneralFragment.newInstance(otId, usuarioId, tipo, empresaId, servicioId,personalId)
                1 -> if (tipo == 5) DesmonteFragment.newInstance(
                    otId, usuarioId, tipo,servicioId
                ) else MedidasFragment.newInstance(otId, usuarioId, tipo,servicioId)
                2 -> DesmonteFragment.newInstance(otId, usuarioId, tipo,servicioId)
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }

}