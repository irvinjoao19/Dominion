package com.dsige.dominion.data.viewModel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.data.local.repository.*
import com.dsige.dominion.helper.Mensaje
import com.dsige.dominion.helper.Util
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OtViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError = MutableLiveData<String>()
    val mensajeSuccess = MutableLiveData<String>()
    val search: MutableLiveData<String> = MutableLiveData()

    val user: LiveData<Usuario>
        get() = roomRepository.getUsuario()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getGrupos(): LiveData<List<Grupo>> {
        return roomRepository.getGrupos()
    }

    fun getEstados(): LiveData<List<Estado>> {
        return roomRepository.getEstados()
    }

    fun getOts(): LiveData<PagedList<Ot>> {
        return Transformations.switchMap(search) { input ->
            if (input == null || input.isEmpty()) {
                roomRepository.getOts()
            } else {
                roomRepository.getOts()
//                val f = Gson().fromJson(search.value, Filtro::class.java)
//                if (f.distritoId.isEmpty()) {
//                    if (f.search.isNotEmpty()) {
//                        roomRepository.getCliente(String.format("%s%s%s", "%", f.search, "%"))
//                    } else {
//                        roomRepository.getCliente()
//                    }
//                } else {
//                    if (f.search.isEmpty()) {
//                        roomRepository.getCliente(f.distritoId.toInt())
//                    } else {
//                        roomRepository.getCliente(
//                            f.distritoId.toInt(), String.format("%s%s%s", "%", f.search, "%")
//                        )
//                    }
//                }
            }
        }
    }

    fun validateOt(t: Ot) {
        insertOrUpdateOt(t)
    }

    private fun insertOrUpdateOt(t: Ot) {
        roomRepository.insertOrUpdateOt(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = "Ot Generado"
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.message
                }

            })
    }

    fun getOtById(otId: Int): LiveData<Ot> {
        return roomRepository.getOtById(otId)
    }

    fun getOtDetalleById(otId: Int): LiveData<PagedList<OtDetalle>> {
        return roomRepository.getOtDetalleById(otId)
    }

    fun getOtPhotoById(id: Int): LiveData<List<OtPhoto>> {
        return roomRepository.getOtPhotoById(id)
    }

    fun getDistritos(): LiveData<List<Distrito>> {
        return roomRepository.getDistritos()
    }

    fun getMateriales(): LiveData<List<Material>> {
        return roomRepository.getMateriales()
    }

    fun validateOtDetalle(d: OtDetalle, tipo: String) {
        if (d.tipoMaterialId == 0) {
            mensajeError.value = "Seleccione Tipo de Material"
            return
        }

        if (d.ancho == 0.0) {
            mensajeError.value = "Ingrese Ancho"
            return
        }

        if (d.largo == 0.0) {
            mensajeError.value = "Ingrese Largo"
            return
        }

        if (d.espesor == 0.0) {
            mensajeError.value = "Ingrese Espesor"
            return
        }
        insertOrUpdateOtDetalle(d, tipo)
    }

    private fun insertOrUpdateOtDetalle(d: OtDetalle, tipo: String) {
        roomRepository.insertOrUpdateOtDetalle(d)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = tipo
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.message
                }
            })
    }

    fun getMaxIdOt(): LiveData<Int> {
        return roomRepository.getMaxIdOt()
    }

    fun getMaxIdOtDetalle(): LiveData<Int> {
        return roomRepository.getMaxIdOtDetalle()
    }

    fun getOtDetalleId(detalleId: Int): LiveData<OtDetalle> {
        return roomRepository.getOtDetalleId(detalleId)
    }

    fun closeDetalle(o: OtDetalle) {
        roomRepository.closeDetalle(o)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = "Ok"
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.message
                }
            })
    }

    fun generarArchivo(nameImg: String, context: Context, data: Intent) {
        Util.getFolderAdjunto(nameImg, context, data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = nameImg
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    Log.i("TAG", e.toString())
                }
            })
    }

    fun insertPhoto(f: OtPhoto) {
        roomRepository.insertPhoto(f)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = "Ok"
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    Log.i("TAG", e.toString())
                }
            })
    }

    fun deletePhoto(o: OtPhoto, context: Context) {
        roomRepository.deletePhoto(o, context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeError.value = "Foto Eliminada"
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }
}