package com.dsige.dominion.ui.broadcasts

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.EstadoOperario
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

class GpsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var roomRepository: AppRepository

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        when (intent.getIntExtra("tipo", 0)) {
            1 -> {
                val gps = Gps(context)
                if (gps.isLocationEnabled()) {
                    if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                        val latitud = gps.latitude.toString()
                        val longitud = gps.longitude.toString()
                        SendGpsTask(roomRepository).execute(latitud,longitud)
//                        executeGps(latitud, longitud)
                    }
                } else {
                    notificationGps(context)
                }
            }
        }
    }

    class SendGpsTask(private val roomRepository: AppRepository): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg strings: String): String? {
            val latitud = strings[0]
            val longitud = strings[1]
            val usuarioId = roomRepository.getUsuarioIdTask()

            val estadoOperario =
                EstadoOperario(usuarioId, latitud, longitud, "", Util.getFechaActual())
            val gps = Gson().toJson(estadoOperario)
            Log.i("GPS", gps)
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gps)
            val mensajeCall = roomRepository.saveGps(body)
            mensajeCall.enqueue(object : Callback<Mensaje> {
                override fun onFailure(call: Call<Mensaje>?, t: Throwable?) {
                }

                override fun onResponse(call: Call<Mensaje>?, response: Response<Mensaje>?) {
                    Log.i("TAG", "GPS ENVIADO")
                }
            })
            publishProgress()
            return "Ok"
        }
    }

    private fun getBasicNotificationBuilder(context: Context)
            : NotificationCompat.Builder {
//        val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        //        if (false) nBuilder.setSound(notificationSound)
        return NotificationCompat.Builder(context, CHANNEL_ID_TIMER)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setDefaults(0)
    }

    private fun notificationGps(context: Context) {
        val manager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val nBuilder = getBasicNotificationBuilder(context)
            nBuilder.setContentTitle("Mensaje Gps")
                .setContentText("Gps desactivado, necesitas activarlo para poder continuar \n con el servicio")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            val mMediaPlayer = MediaPlayer.create(context, R.raw.ic_error)
            mMediaPlayer.start()

            val nManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            nManager.notify(TIMER_ID, nBuilder.build())
        }
    }

    @TargetApi(26)
    private fun NotificationManager.createNotificationChannel(
        channelID: String,
        channelName: String,
        playSound: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
            else NotificationManager.IMPORTANCE_LOW
            val nChannel = NotificationChannel(channelID, channelName, channelImportance)
            nChannel.enableLights(true)
            nChannel.lightColor = Color.BLUE
            this.createNotificationChannel(nChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID_TIMER = "enable_gps"
        private const val CHANNEL_NAME_TIMER = "Dsige_Enable_Gps"
        private const val TIMER_ID = 0
    }
}