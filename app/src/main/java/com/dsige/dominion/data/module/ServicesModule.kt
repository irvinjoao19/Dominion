package com.dsige.dominion.data.module

import com.dsige.dominion.ui.broadcasts.*
import com.dsige.dominion.ui.services.SocketServices
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServicesModule {

    @ContributesAndroidInjector
    internal abstract fun provideMainReceiver(): MovilReceiver

    @ContributesAndroidInjector
    internal abstract fun provideGpsReceiver(): GpsReceiver

    @ContributesAndroidInjector
    internal abstract fun provideSocketReceiver(): SocketServices
}