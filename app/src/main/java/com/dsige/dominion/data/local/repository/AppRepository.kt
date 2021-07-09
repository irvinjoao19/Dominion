package com.dsige.dominion.data.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.helper.Mensaje
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call

interface AppRepository {

    fun getUsuarioIdTask(): Int
    fun getUsuarioId(): Observable<Int>

    fun getEmpresaIdTask(): Int

    fun getUsuario(): LiveData<Usuario>

    fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String
    ): Observable<Usuario>

    fun getLogout(login: String): Observable<Mensaje>
    fun insertUsuario(u: Usuario): Completable
    fun deleteSesion(): Completable
    fun deleteSync(): Completable
    fun getSync(u: Int, e: Int, p: Int,v:String): Observable<Sync>
    fun saveSync(s: Sync): Completable

    fun getAccesos(usuarioId: Int): LiveData<List<Accesos>>
    fun getGrupos(): LiveData<List<Grupo>>
    fun getGrupoByServicioId(id:Int): LiveData<List<Grupo>>
    fun getEstados(): LiveData<List<Estado>>

    fun getOts(): LiveData<PagingData<Ot>>
    fun getOts(t: Int, e: Int): LiveData<PagingData<Ot>>
    fun getOts(t: Int, e: Int, s: String): LiveData<PagingData<Ot>>
    fun getOts(t: Int, e: Int, s: Int): LiveData<PagingData<Ot>>
    fun getOts(t: Int, e: Int, sId: Int, s: String): LiveData<PagingData<Ot>>

    fun insertOrUpdateOt(t: Ot): Completable
    fun getOtById(otId: Int): LiveData<Ot>
    fun getOtDetalleById(otId: Int, tipo: Int): LiveData<PagingData<OtDetalle>>
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
    fun updateOt(t: Mensaje): Completable

    fun saveGps(body: RequestBody): Call<Mensaje>
    fun saveMovil(body: RequestBody): Call<Mensaje>

    fun deleteOtDetalle(o: OtDetalle, context: Context): Completable

    fun getProveedor(f: Filtro): Observable<List<Proveedor>>
    fun getProveedores(): LiveData<PagingData<Proveedor>>
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

    fun getOtPlazos(): LiveData<PagingData<OtPlazo>>
    fun getOtPlazo(f: Filtro): Observable<List<OtPlazo>>
    fun insertOtPlazo(t: List<OtPlazo>): Completable
    fun getOtPlazoDetalle(f: Filtro): Observable<List<OtPlazoDetalle>>
    fun insertOtPlazoDetalle(t: List<OtPlazoDetalle>): Completable
    fun clearOtPlazo(): Completable
    fun getOtPlazoDetalles(): LiveData<PagingData<OtPlazoDetalle>>
    fun clearOtPlazoDetalle(): Completable

    fun sendSocket(): Completable

    // nuevo
    fun getOtPhotoTask(): Observable<List<String>>
    fun sendOtPhotos(body: RequestBody): Observable<String>
    fun sendOt(body: RequestBody): Observable<Mensaje>
    fun getSed(sed: String): Observable<Sed>
    fun insertOtPhotoCabecera(t: OtDetalle): Observable<Int>
    fun insertOtPhoto(id:Int,t: List<OtPhoto>): Completable
    fun getCountOtPhotoBajaTension(otId: Int): LiveData<Int>
    fun getOtPhotoBajaTension(otId: Int): LiveData<List<OtPhoto>>
    fun deleteOtPhotoBajaTension(otId: Int,context:Context): Completable

    // nuevo
    fun cerrarTrabajo(otId: Int): Completable

    //gps
    fun insertGps(e: OperarioGps): Completable
    fun getSendGps(): Observable<List<OperarioGps>>
    fun saveOperarioGps(e: OperarioGps): Observable<Mensaje>
    fun updateEnabledGps(t: Mensaje): Completable

    //battery
    fun insertBattery(e: OperarioBattery): Completable
    fun getSendBattery(): Observable<List<OperarioBattery>>
    fun saveOperarioBattery(e: OperarioBattery): Observable<Mensaje>
    fun updateEnabledBattery(t: Mensaje): Completable

    //pdf
    fun updateOtPdf(id: Int, path: String): Completable
}