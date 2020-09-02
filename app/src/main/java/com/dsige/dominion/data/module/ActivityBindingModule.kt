package com.dsige.dominion.data.module

import com.dsige.dominion.ui.activities.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Main::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Form::class])
    internal abstract fun bindFormActivity(): FormActivity

    @ContributesAndroidInjector
    internal abstract fun bindFormDetailActivity(): FormDetailActivity

    @ContributesAndroidInjector
    internal abstract fun bindPreviewCameraActivity() : PreviewCameraActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Reporte::class])
    internal abstract fun bindMapActivity(): MapActivity
}