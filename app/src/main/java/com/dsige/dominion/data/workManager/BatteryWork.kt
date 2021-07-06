package com.dsige.dominion.data.workManager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.BatteryManager
import android.provider.Settings
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dsige.dominion.data.local.model.OperarioBattery
import com.dsige.dominion.data.local.repository.AppRepository
import com.dsige.dominion.helper.Mensaje
import com.dsige.dominion.helper.Util
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider

class BatteryWork @Inject
internal constructor(
    val context: Context, workerParams: WorkerParameters, private val roomRepository: AppRepository
) : Worker(context, workerParams) {

    class Factory @Inject constructor(private val repository: Provider<AppRepository>) :
        ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return BatteryWork(appContext, params, repository.get())
        }
    }

    private fun Context.isGPSEnabled() = (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
        LocationManager.GPS_PROVIDER)


    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        getUsuario()
        return Result.success()
    }


    private fun getUsuario() {
        roomRepository.getUsuarioId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
                override fun onNext(t: Int) {
                    saveBatery(t)
                }
            })
    }


    private fun saveBatery(operarioId: Int) {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent = context.registerReceiver(null, ifilter)!!
        val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct: Int = level

        val gpsActivo = if(context.isGPSEnabled()) 1 else 0
        val modoAvion = if (Settings.System.getInt(
                context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON,
                0
            ) == 0
        ) 0 else 1


        val planDatos = if (Util.getMobileDataState(context)) 1 else 0

        saveBattery(
            OperarioBattery(
                operarioId, gpsActivo, batteryPct, Util.getFechaActual(), modoAvion, planDatos, 1
            )
        )
    }

    private fun saveBattery(e: OperarioBattery) {
        roomRepository.insertBattery(e)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    sendBattery()
                }
            })
    }

    private fun sendBattery() {
        roomRepository.getSendBattery()
            .flatMap { observable ->
                Observable.fromIterable(observable).flatMap { a ->
                    Observable.zip(
                        Observable.just(a),
                        roomRepository.saveOperarioBattery(a), { _, mensaje -> mensaje })
                }
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(t: Throwable) { }
                override fun onNext(t: Mensaje) {
                    updateEnabledBattery(t)
                }
            })
    }

    private fun updateEnabledBattery(t: Mensaje) {
        roomRepository.updateEnabledBattery(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }
}