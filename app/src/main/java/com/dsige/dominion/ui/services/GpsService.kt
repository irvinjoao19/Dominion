package com.dsige.dominion.ui.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import com.dsige.dominion.ui.broadcasts.GpsReceiver

class GpsService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        Log.i("service", "Close MainService")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("service", "Open MainService")
    }

    override fun onDestroy() {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(this, GpsReceiver::class.java).putExtra("tipo", 0)
        val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        pi.cancel()
        am.cancel(pi)
        super.onDestroy()
        Log.i("service", "Close MainService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val gps = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intentGps = Intent(this, GpsReceiver::class.java).putExtra("tipo", 1)
        val pGps = PendingIntent.getBroadcast(this, 0, intentGps, PendingIntent.FLAG_UPDATE_CURRENT)
        val timeGps: Long = 300 * 1000
        gps.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timeGps, pGps)
        return START_STICKY
    }
}