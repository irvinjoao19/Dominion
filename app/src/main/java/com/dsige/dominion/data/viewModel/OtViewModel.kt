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
                val f = Gson().fromJson(search.value, Filtro::class.java)
                if (f.servicioId == 0) {
                    if (f.search.isNotEmpty()) {
                        roomRepository.getOts(
                            f.tipo, f.estadoId, String.format("%s%s%s", "%", f.search, "%")
                        )
                    } else {
                        roomRepository.getOts(f.tipo, f.estadoId)
                    }
                } else {
                    if (f.search.isEmpty()) {
                        roomRepository.getOts(f.tipo, f.estadoId, f.servicioId)
                    } else {
                        roomRepository.getOts(
                            f.tipo, f.estadoId, f.servicioId,
                            String.format("%s%s%s", "%", f.search, "%")
                        )
                    }
                }
            }
        }
    }

    fun validateOt(t: Ot) {
        if (t.nroObra.isEmpty()) {
            mensajeError.value = "Ingresar Nro OT/TD"
            return
        }
        if (t.direccion.isEmpty()) {
            mensajeError.value = "Ingresar Dirección"
            return
        }
        if (t.nombreDistritoId.isEmpty()) {
            mensajeError.value = "Seleccione Distrito"
            return
        }
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

    fun getOtDetalleById(otId: Int, tipo: Int): LiveData<PagedList<OtDetalle>> {
        return roomRepository.getOtDetalleById(otId, tipo)
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

    fun getServicios(): LiveData<List<Servicio>> {
        return roomRepository.getServicios()
    }

    fun validateOtDetalle(d: OtDetalle, tipo: String) {
        if (d.tipoTrabajoId == 6) {
            if (d.tipoMaterialId == 0) {
                mensajeError.value = "Seleccione Tipo de Material"
                return
            }
        } else {
            if (d.tipoDesmonteId == 14) {
                if (d.nroPlaca.isEmpty()) {
                    mensajeError.value = "Nro de Placa"
                    return
                }
            }
        }

        if (d.largo == 0.0) {
            mensajeError.value = "Ingrese Largo"
            return
        }

        if (d.ancho == 0.0) {
            mensajeError.value = "Ingrese Ancho"
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
                    mensajeSuccess.value = if (tipo == "3") "Ok" else tipo
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

    fun generarArchivo(nameImg: String, context: Context, data: Intent,direccion:String,distrito:String) {
        Util.getFolderAdjunto(nameImg, context, data,direccion,distrito)
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

    fun deleteOtDetalle(o: OtDetalle, context: Context) {
        roomRepository.deleteOtDetalle(o, context)
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

    fun getProveedor(f: Filtro) {
        roomRepository.clearProveedores()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = "load"
                    syncProveedor(f)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    private fun syncProveedor(f: Filtro) {
        roomRepository.getProveedor(f)
            .delay(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Proveedor>> {
                override fun onComplete() {
                    mensajeSuccess.value = "finish"
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Proveedor>) {
                    insertProveedor(t)
                }

                override fun onError(e: Throwable) {
                    mensajeSuccess.value = "finish"
                }
            })
    }

    private fun insertProveedor(t: List<Proveedor>) {
        roomRepository.insertProveedor(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun getProveedores(): LiveData<PagedList<Proveedor>> {
        return roomRepository.getProveedores()
    }


    // TODO Empresas Ot Reporte

    fun syncEmpresa(f: Filtro) {
        roomRepository.getEmpresa(f)
            .delay(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<OtReporte>> {
                override fun onComplete() {
                    mensajeSuccess.value = "finish"
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<OtReporte>) {
                    insertEmpresa(t)
                }

                override fun onError(e: Throwable) {
                    mensajeSuccess.value = "finish"
                }
            })
    }

    private fun insertEmpresa(t: List<OtReporte>) {
        roomRepository.insertEmpresa(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun getEmpresas(): LiveData<List<OtReporte>> {
        return roomRepository.getOtReporte()
    }

    fun getEmpresasById(id: Int): LiveData<OtReporte> {
        return roomRepository.getEmpresaById(id)
    }

    // TODO Personal Jefe Cuadrilla

    fun syncJefeCuadrilla(f: Filtro) {
        roomRepository.getJefeCuadrilla(f)
            .delay(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<JefeCuadrilla>> {
                override fun onComplete() {
                    mensajeSuccess.value = "finish"
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<JefeCuadrilla>) {
                    insertJefeCuadrilla(t)
                }

                override fun onError(e: Throwable) {
                    mensajeSuccess.value = "finish"
                }
            })
    }

    private fun insertJefeCuadrilla(t: List<JefeCuadrilla>) {
        roomRepository.insertJefeCuadrilla(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun getJefeCuadrillas(): LiveData<List<JefeCuadrilla>> {
        return roomRepository.getJefeCuadrillas()
    }

    fun getJefeCuadrillaById(id: Int): LiveData<JefeCuadrilla> {
        return roomRepository.getJefeCuadrillaById(id)
    }

    // TODO Ot Plazo

    fun getOtPlazos(): LiveData<PagedList<OtPlazo>> {
        return roomRepository.getOtPlazos()
    }

    fun syncOtPlazo(f: Filtro) {
        roomRepository.clearOtPlazo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = "load"
                    roomRepository.getOtPlazo(f)
                        .delay(2000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<List<OtPlazo>> {
                            override fun onComplete() {
                                mensajeSuccess.value = "finish"
                            }

                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onNext(t: List<OtPlazo>) {
                                insertOtPlazo(t)
                            }

                            override fun onError(t: Throwable) {
                                mensajeSuccess.value = "finish"
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

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    private fun insertOtPlazo(t: List<OtPlazo>) {
        roomRepository.insertOtPlazo(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }

            })
    }

    // TODO Ot Plazo Detalle


    fun syncOtPlazoDetalle(f: Filtro) {
        roomRepository.clearOtPlazoDetalle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    roomRepository.getOtPlazoDetalle(f)
                        .delay(2000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<List<OtPlazoDetalle>> {
                            override fun onComplete() {
                                mensajeSuccess.value = "finish"
                            }

                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onNext(t: List<OtPlazoDetalle>) {
                                insertOtPlazoDetalle(t)
                            }

                            override fun onError(t: Throwable) {
                                mensajeSuccess.value = "finish"
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

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }

            })
    }

    private fun insertOtPlazoDetalle(t: List<OtPlazoDetalle>) {
        roomRepository.insertOtPlazoDetalle(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun getOtPlazoDetalles(): LiveData<PagedList<OtPlazoDetalle>> {
        return roomRepository.getOtPlazoDetalles()
    }
}