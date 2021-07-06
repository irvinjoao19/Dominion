package com.dsige.dominion.helper

import android.content.Context
import android.graphics.Color
import com.dsige.dominion.data.local.model.MapPrincipal
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class FetchUrl(private val url: String, private val directionMode: String, context: Context) {

    private var taskCallback: TaskLoadedCallback = (context as TaskLoadedCallback)

    init {
        val data: Observable<String> = Observable.create {
            val result = downloadUrl(url)
            it.onNext(result)
            it.onComplete()
        }
        data.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: String) {
                    pointsRouter(t)
                    pointsPaser(t)
                }
            })
    }

    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuilder()
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
//            Log.d("mylog", "Exception downloading URL: $e")
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }

    private fun pointsRouter(json:String){
        val map: MapPrincipal = Gson().fromJson(json, MapPrincipal::class.java)
        taskCallback.onTaskRoutes(map)
    }

    private fun pointsPaser(json: String) {
        val points: Observable<List<List<HashMap<String, String>>>> = Observable.create {
            val routes: List<List<HashMap<String, String>>>
            val jObject = JSONObject(json)
            routes = DataParser().parse(jObject)
            it.onNext(routes)
            it.onComplete()
        }
        points.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<List<HashMap<String, String>>>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: List<List<HashMap<String, String>>>) {
                    onPostExecute(t)
                }
            })

    }

    private fun onPostExecute(result: List<List<HashMap<String, String>>>) {
        var points: ArrayList<LatLng>
        var lineOptions: PolylineOptions? = null
        for (i in result.indices) {
            points = ArrayList()
            lineOptions = PolylineOptions()

            val path: List<HashMap<String, String>> = result[i]
            for (j in path.indices) {
                val point: HashMap<String, String> = path[j]
                val lat = point["lat"]!!.toDouble()
                val lng = point["lng"]!!.toDouble()
                val position = LatLng(lat, lng)
                points.add(position)
            }
            lineOptions.addAll(points)
            if (directionMode.equals("walking", ignoreCase = true)) {
                lineOptions.width(10f)
                lineOptions.color(Color.MAGENTA)
            } else {
                lineOptions.width(13f)
                lineOptions.color(Color.RED)
            }
        }
        if (lineOptions != null) {
            taskCallback.onTaskDone(lineOptions)
        }
    }
}