package com.dsige.dominion.data.local.repository

import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.helper.Mensaje
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers("Cache-Control: no-cache")
    @POST("Login")
    fun getLogin(@Body body: RequestBody): Observable<Usuario>

    @Headers("Cache-Control: no-cache")
    @POST("Logout")
    fun getLogout(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("Sync")
    fun getSync(@Body body: RequestBody): Observable<Sync>

    @Headers("Cache-Control: no-cache")
    @POST("SaveGps")
    fun saveGps(@Body body: RequestBody): Call<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveMovil")
    fun saveMovil(@Body body: RequestBody): Call<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveRegistroNew")
    fun sendRegistroOt(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("Proveedores")
    fun getProveedor(@Body body: RequestBody): Observable<List<Proveedor>>

    @Headers("Cache-Control: no-cache")
    @POST("Empresas")
    fun getEmpresa(@Body body: RequestBody): Observable<List<OtReporte>>

    @Headers("Cache-Control: no-cache")
    @POST("JefeCuadrillas")
    fun getJefeCuadrilla(@Body body: RequestBody): Observable<List<JefeCuadrilla>>

    @Headers("Cache-Control: no-cache")
    @POST("OtPlazo")
    fun getOtPlazo(@Body body: RequestBody): Observable<List<OtPlazo>>

    @Headers("Cache-Control: no-cache")
    @POST("OtPlazoDetalle")
    fun getOtPlazoDetalle(@Body body: RequestBody): Observable<List<OtPlazoDetalle>>


    // nuevo
    @Headers("Cache-Control: no-cache")
    @POST("SaveOtPhotos")
    fun sendOtPhotos(@Body body: RequestBody): Observable<String>

    @Headers("Cache-Control: no-cache")
    @POST("SaveOtNew3")
    fun sendOt(@Body body: RequestBody): Observable<Mensaje>

}