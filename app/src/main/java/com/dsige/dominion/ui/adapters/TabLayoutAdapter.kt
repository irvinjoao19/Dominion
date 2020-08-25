package com.dsige.dominion.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dsige.dominion.ui.fragments.*

abstract class TabLayoutAdapter {

    class TabLayoutForm(
        fm: FragmentManager,
        var numberOfTabs: Int,
        var otId: Int,
        var usuarioId: Int,
        var tipo: Int
    ) :
        FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GeneralFragment.newInstance(otId, usuarioId, tipo)
                1 -> MedidasFragment.newInstance(otId, usuarioId, tipo)
                2 -> DesmonteFragment.newInstance("", "")
                3 -> PhotoFragment.newInstance("", "")
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }

}