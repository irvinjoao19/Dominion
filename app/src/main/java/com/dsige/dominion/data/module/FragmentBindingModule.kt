package com.dsige.dominion.data.module

import com.dsige.dominion.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

abstract class FragmentBindingModule {

    @Module
    abstract class Main {
        @ContributesAndroidInjector
        internal abstract fun providMainFragment(): MainFragment

        @ContributesAndroidInjector
        internal abstract fun providGeneralMapFragment(): GeneralMapFragment

        @ContributesAndroidInjector
        internal abstract fun providResumenFragment(): ResumenFragment
    }

    @Module
    abstract class Form {
        @ContributesAndroidInjector
        internal abstract fun providGeneralFragment(): GeneralFragment

        @ContributesAndroidInjector
        internal abstract fun providMedidasFragment(): MedidasFragment

        @ContributesAndroidInjector
        internal abstract fun providDesmonteFragment(): DesmonteFragment

        @ContributesAndroidInjector
        internal abstract fun providPhotoFragment(): PhotoFragment
    }

    @Module
    abstract class Reporte {
        @ContributesAndroidInjector
        internal abstract fun providPersonalMapFragment(): PersonalMapFragment

        @ContributesAndroidInjector
        internal abstract fun providEmpresaMapFragment(): EmpresaMapFragment
    }
}