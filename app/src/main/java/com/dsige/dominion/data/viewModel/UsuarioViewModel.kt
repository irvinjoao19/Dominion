package com.dsige.dominion.data.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.data.local.repository.ApiError
import com.dsige.dominion.data.local.repository.AppRepository
import com.dsige.dominion.helper.Mensaje
import com.dsige.dominion.helper.Util
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UsuarioViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError = MutableLiveData<String>()
    val mensajeSuccess = MutableLiveData<String>()

    val user: LiveData<Usuario>
        get() = roomRepository.getUsuario()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getLogin(usuario: String, pass: String, imei: String, version: String) {
        roomRepository.getUsuarioService(usuario, pass, imei, version)
            .delay(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Usuario> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(usuario: Usuario) {
                    insertUsuario(usuario, version)
                }

                override fun onError(t: Throwable) {
                    if (t is HttpException) {
                        val body = t.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    } else {
                        mensajeError.postValue(t.message)
                    }
                }

                override fun onComplete() {
                }
            })
    }

    fun insertUsuario(u: Usuario, v: String) {
        roomRepository.insertUsuario(u)
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    getSync(u, v)
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun logout(login: String) {
        deleteUser(login)
//        var mensaje = ""
//        roomRepository.getLogout(login)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : Observer<Mensaje> {
//                override fun onSubscribe(d: Disposable) {
//
//                }
//
//                override fun onNext(m: Mensaje) {
//                    mensaje = m.mensaje
//                }
//
//                override fun onError(t: Throwable) {
//                    if (t is HttpException) {
//                        val body = t.response().errorBody()
//                        try {
//                            val error = retrofit.errorConverter.convert(body!!)
//                            mensajeError.postValue(error.Message)
//                        } catch (e1: IOException) {
//                            e1.printStackTrace()
//                        }
//                    } else {
//                        mensajeError.postValue(t.message)
//                    }
//                }
//
//                override fun onComplete() {
//                    deleteUser(mensaje)
//                }
//            })
    }

    private fun deleteUser(mensaje: String) {
        roomRepository.deleteUsuario()
            .delay(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    mensajeSuccess.value = "Close"
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun getSync(u: Usuario, version: String) {
        roomRepository.deleteTotal()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    sync(u.empresaId, u.personalId)
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    private fun sync(e: Int, p: Int) {
        roomRepository.getSync(e, p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Sync> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Sync) {
                    insertSync(t)
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val body = e.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                            Log.i("TAG", e1.toString())
                        }
                    } else {
                        mensajeError.postValue(e.toString())
                    }
                }
            })
    }

    fun insertSync(s: Sync) {
        roomRepository.saveSync(s)
            .delay(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    mensajeSuccess.value = "Sincronización Completa"
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun getAccesos(usuarioId: Int): LiveData<List<Accesos>> {
        return roomRepository.getAccesos(usuarioId)
    }

    fun sendData(context: Context) {
        val ots: Observable<List<Ot>> = roomRepository.getSendOt(1)
        ots.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val b = MultipartBody.Builder()
                val detalles: List<OtDetalle>? = a.detalles
                if (detalles != null) {
                    for (d: OtDetalle in detalles) {
                        for (p: OtPhoto in d.photos) {
                            if (p.nombrePhoto.isNotEmpty()) {
                                val file = File(Util.getFolder(context), p.nombrePhoto)
                                if (file.exists()) {
                                    b.addFormDataPart(
                                        "files", file.name,
                                        RequestBody.create(
                                            MediaType.parse("multipart/form-data"),
                                            file
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                val json = Gson().toJson(a)
                Log.i("TAG", json)
                b.setType(MultipartBody.FORM)
                b.addFormDataPart("data", json)

                val body = b.build()
                Observable.zip(
                    Observable.just(a), roomRepository.sendRegistroOt(body),
                    BiFunction<Ot, Mensaje, Mensaje> { _, mensaje ->
                        mensaje
                    })
            }
        }.subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onComplete() {
                    mensajeSuccess.value = "Enviado"
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Mensaje) {
                    updateOt(t)
                }

                override fun onError(t: Throwable) {
                    if (t is HttpException) {
                        val body = t.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    } else {
                        mensajeError.postValue(t.message)
                    }
                }
            })

    }

    private fun updateOt(t: Mensaje) {
        roomRepository.updateOt(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
//                    mensajeError.value = e.message
                }
            })
    }
}