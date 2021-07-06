package com.dsige.dominion.data.module

import com.dsige.dominion.data.workManager.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(GpsWork::class)
    internal abstract fun bindGpsWork(gpsWork: GpsWork.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(BatteryWork::class)
    internal abstract fun bindBatteryWork(batteryWork: BatteryWork.Factory): ChildWorkerFactory

}