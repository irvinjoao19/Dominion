package com.dsige.dominion.ui.services

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Notificacion
import com.dsige.dominion.data.local.repository.AppRepository
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.activities.MainActivity
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_preview_camera.*
import java.io.File
import java.net.URISyntaxException
import java.util.*
import javax.inject.Inject

class SocketServices : Service() {

    @Inject
    lateinit var roomRepository: AppRepository
    lateinit var mSocket: Socket

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val handler = Handler()
        handler.post {
            try {
                mSocket =
                    IO.socket("http://192.168.20.249:5001/")
                mSocket.on("Alertas_web_OT") { s ->
                    Log.i("TAG", s[0].toString())
                    val myType = object : TypeToken<List<Notificacion>>() {}.type
                    val n = Gson().fromJson<List<Notificacion>>(s[0].toString(), myType)
                    for ((id, m: Notificacion) in n.withIndex()) {
                        if (m.idCuadrilla == "0") {
                            val e = roomRepository.getEmpresaIdTask()
                            if (m.idEmpresa == e.toString()) {
                                notificationSocket(id, this, m.titulo, m.mensaje)
                            }
                        } else {
                            val u = roomRepository.getUsuarioIdTask()
                            if (u.toString() == m.idCuadrilla) {
                                notificationSocket(id, this, m.titulo, m.mensaje)
                            }
                        }
                    }
                }
            } catch (ignored: URISyntaxException) {
            }
            mSocket.connect()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun notificationSocket(id: Int, context: Context, title: String, s: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, false)
        nBuilder.setContentTitle(title)
            .setContentText(s)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
//            val mMediaPlayer = MediaPlayer.create(context, R.raw.ic_error)
//            mMediaPlayer.start()

        val nManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
        nManager.notify(id, nBuilder.build())
    }

    private fun getBasicNotificationBuilder(context: Context, channelId: String, playSound: Boolean)
            : NotificationCompat.Builder {
        val notificationSound: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val nBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources, R.mipmap.ic_launcher_round
                )
            )
            .setAutoCancel(true)
            .setDefaults(0)
        if (playSound) nBuilder.setSound(notificationSound)
        return nBuilder
    }


    @TargetApi(26)
    private fun NotificationManager.createNotificationChannel(
        channelID: String, channelName: String, playSound: Boolean
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
        private const val CHANNEL_ID_TIMER = "enable_socket"
        private const val CHANNEL_NAME_TIMER = "Dsige_Enable_Socket"
        private const val TIMER_ID = 0
    }
}