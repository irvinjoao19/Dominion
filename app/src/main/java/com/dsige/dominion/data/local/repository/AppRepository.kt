package com.dsige.dominion.data.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.helper.Mensaje
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.RequestBody

interface AppRepository {

    fun getUsuarioIdTask(): Int

    fun getUsuario(): LiveData<Usuario>

    fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String
    ): Observable<Usuario>

    fun getLogout(login: String): Observable<Mensaje>

    fun insertUsuario(u: Usuario): Completable

    fun deleteUsuario(): Completable

    fun deleteTotal(): Completable

    fun getSync(e: Int, p: Int): Observable<Sync>

    fun saveSync(s: Sync): Completable

    fun getAccesos(usuarioId: Int): LiveData<List<Accesos>>

    fun getGrupos(): LiveData<List<Grupo>>

    fun getEstados(): LiveData<List<Estado>>

    fun getOts(): LiveData<PagedList<Ot>>

    fun insertOrUpdateOt(t: Ot): Completable

    fun getOtById(otId: Int): LiveData<Ot>

    fun getOtDetalleById(otId: Int): LiveData<PagedList<OtDetalle>>

    fun getOtPhotoById(id: Int): LiveData<List<OtPhoto>>

    fun getDistritos(): LiveData<List<Distrito>>

    fun getMateriales(): LiveData<List<Material>>

    fun insertOrUpdateOtDetalle(d: OtDetalle): Completable

    fun getMaxIdOt(): LiveData<Int>

    fun getMaxIdOtDetalle(): LiveData<Int>

    fun getOtDetalleId(id: Int): LiveData<OtDetalle>

    fun closeDetalle(o: OtDetalle): Completable

    fun insertPhoto(f: OtPhoto): Completable

    fun deletePhoto(o: OtPhoto, context: Context): Completable

    fun getSendOt(i: Int): Observable<List<Ot>>

    fun sendRegistroOt(body: RequestBody): Observable<Mensaje>

    fun updateOt(t: Mensaje): Completable
}