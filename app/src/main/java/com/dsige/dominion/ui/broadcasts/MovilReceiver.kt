package com.dsige.dominion.ui.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log
import com.dsige.dominion.data.local.model.EstadoMovil
import com.dsige.dominion.data.local.repository.AppRepository
import com.dsige.dominion.helper.Gps
import com.dsige.dominion.helper.Mensaje
import com.dsige.dominion.helper.Util
import com.google.gson.Gson
import dagger.android.AndroidInjection
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MovilReceiver : BroadcastReceiver() {

    @Inject
    lateinit var roomRepository: AppRepository

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        when (intent.getIntExtra("tipo", 0)) {
            1 -> {
                val gps = Gps(context)
                val gpsActivo = if (gps.isLocationEnabled()) 1 else 0
                SendMovilTask(roomRepository,context).execute(gpsActivo.toString())
            }
        }
    }

    class SendMovilTask(private val roomRepository: AppRepository, private val context: Context) :
        AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg strings: String): String? {
            val gpsActivo = strings[0].toInt()
            val operarioId = roomRepository.getUsuarioIdTask()

            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent = context.registerReceiver(null, ifilter)!!
            val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct: Int = level

            val modoAvion = if (Settings.System.getInt(
                    context.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON,
                    0
                ) == 0
            ) 0 else 1

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val planDatos = if (activeNetwork != null) {
                if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE || activeNetwork.isConnected) 1 else 0
            } else {
                0
            }

            val operario = EstadoMovil(
                operarioId, gpsActivo, batteryPct, Util.getFechaActual(), modoAvion, planDatos
            )
            val gps = Gson().toJson(operario)
            Log.i("GPS", gps)
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gps)
            val mensajeCall = roomRepository.saveMovil(body)
            mensajeCall.enqueue(object : Callback<Mensaje> {
                override fun onFailure(call: Call<Mensaje>?, t: Throwable?) {
                }

                override fun onResponse(call: Call<Mensaje>?, response: Response<Mensaje>?) {
                    Log.i("TAG", "MOVIL ENVIADO")
                }
            })
            publishProgress()
            return "Ok"
        }
    }
}