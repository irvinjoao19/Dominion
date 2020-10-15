package com.dsige.dominion.data.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.helper.Mensaje
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call

interface AppRepository {

    fun getUsuarioIdTask(): Int

    fun getEmpresaIdTask(): Int

    fun getUsuario(): LiveData<Usuario>

    fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String
    ): Observable<Usuario>

    fun getLogout(login: String): Observable<Mensaje>
    fun insertUsuario(u: Usuario): Completable
    fun deleteSesion(): Completable
    fun deleteSync(): Completable
    fun getSync(u: Int, e: Int, p: Int): Observable<Sync>
    fun saveSync(s: Sync): Completable

    fun getAccesos(usuarioId: Int): LiveData<List<Accesos>>
    fun getGrupos(): LiveData<List<Grupo>>
    fun getEstados(): LiveData<List<Estado>>

    fun getOts(): LiveData<PagedList<Ot>>
    fun getOts(t: Int, e: Int): LiveData<PagedList<Ot>>
    fun getOts(t: Int, e: Int, s: String): LiveData<PagedList<Ot>>
    fun getOts(t: Int, e: Int, s: Int): LiveData<PagedList<Ot>>
    fun getOts(t: Int, e: Int, sId: Int, s: String): LiveData<PagedList<Ot>>
    fun insertOrUpdateOt(t: Ot): Completable
    fun getOtById(otId: Int): LiveData<Ot>
    fun getOtDetalleById(otId: Int, tipo: Int): LiveData<PagedList<OtDetalle>>
    fun getOtPhotoById(id: Int): LiveData<List<OtPhoto>>

    fun getDistritos(): LiveData<List<Distrito>>
    fun getMateriales(): LiveData<List<Material>>
    fun getServicios(): LiveData<List<Servicio>>

    fun insertOrUpdateOtDetalle(d: OtDetalle): Completable

    fun getMaxIdOt(): LiveData<Int>
    fun getMaxIdOtDetalle(): LiveData<Int>
    fun getOtDetalleId(id: Int): LiveData<OtDetalle>

    fun insertPhoto(f: OtPhoto): Completable
    fun insertMultiPhoto(f: ArrayList<OtPhoto>): Completable
    fun deletePhoto(o: OtPhoto, context: Context): Completable

    fun getSendOt(i: Int): Observable<List<Ot>>
    fun sendRegistroOt(body: RequestBody): Observable<Mensaje>
    fun updateOt(t: Mensaje): Completable

    fun saveGps(body: RequestBody): Call<Mensaje>
    fun saveMovil(body: RequestBody): Call<Mensaje>

    fun deleteOtDetalle(o: OtDetalle, context: Context): Completable

    fun getProveedor(f: Filtro): Observable<List<Proveedor>>
    fun getProveedores(): LiveData<PagedList<Proveedor>>
    fun clearProveedores(): Completable
    fun insertProveedor(t: List<Proveedor>): Completable
    fun getEmpresa(f: Filtro): Observable<List<OtReporte>>
    fun insertEmpresa(t: List<OtReporte>): Completable
    fun getOtReporte(): LiveData<List<OtReporte>>
    fun getEmpresaById(id: Int): LiveData<OtReporte>
    fun getJefeCuadrilla(f: Filtro): Observable<List<JefeCuadrilla>>
    fun insertJefeCuadrilla(t: List<JefeCuadrilla>): Completable
    fun getJefeCuadrillas(): LiveData<List<JefeCuadrilla>>
    fun getJefeCuadrillaById(id: Int): LiveData<JefeCuadrilla>

    fun getOtPlazos(): LiveData<PagedList<OtPlazo>>
    fun getOtPlazo(f: Filtro): Observable<List<OtPlazo>>
    fun insertOtPlazo(t: List<OtPlazo>): Completable
    fun getOtPlazoDetalle(f: Filtro): Observable<List<OtPlazoDetalle>>
    fun insertOtPlazoDetalle(t: List<OtPlazoDetalle>): Completable
    fun clearOtPlazo(): Completable
    fun getOtPlazoDetalles(): LiveData<PagedList<OtPlazoDetalle>>
    fun clearOtPlazoDetalle(): Completable

    fun sendSocket(): Completable

}