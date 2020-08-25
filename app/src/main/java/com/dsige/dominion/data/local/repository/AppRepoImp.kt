package com.dsige.dominion.data.local.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.dsige.dominion.data.local.AppDataBase
import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.helper.Mensaje
import com.dsige.dominion.helper.Util
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody

class AppRepoImp(private val apiService: ApiService, private val dataBase: AppDataBase) :
    AppRepository {

    override fun getUsuarioIdTask(): Int {
        return dataBase.usuarioDao().getUsuarioIdTask()
    }

    override fun getUsuario(): LiveData<Usuario> {
        return dataBase.usuarioDao().getUsuario()
    }

    override fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String
    ): Observable<Usuario> {
        val u = Filtro(usuario, password, imei, version)
        val json = Gson().toJson(u)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getLogin(body)
    }

    override fun getLogout(login: String): Observable<Mensaje> {
        val u = Filtro(login)
        val json = Gson().toJson(u)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getLogout(body)
    }

    override fun insertUsuario(u: Usuario): Completable {
        return Completable.fromAction {
            dataBase.usuarioDao().insertUsuarioTask(u)
            val a: List<Accesos>? = u.accesos
            if (a != null) {
                dataBase.accesosDao().insertAccesosListTask(a)
            }
        }
    }

    override fun deleteUsuario(): Completable {
        return Completable.fromAction {
            dataBase.usuarioDao().deleteAll()
            dataBase.accesosDao().deleteAll()
        }
    }

    override fun deleteTotal(): Completable {
        return Completable.fromAction {
            dataBase.estadoDao().deleteAll()
            dataBase.grupoDao().deleteAll()
            dataBase.otDao().deleteAll()
        }
    }

    override fun getSync(e: Int, p: Int): Observable<Sync> {
        val f = Filtro(e, p)
        val json = Gson().toJson(f)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getSync(body)
    }

    override fun saveSync(s: Sync): Completable {
        return Completable.fromAction {
            val o: List<Ot>? = s.ots
            if (o != null) {
                dataBase.otDao().insertOtListTask(o)
            }
            val g: List<Grupo>? = s.groups
            if (g != null) {
                dataBase.grupoDao().insertGrupoListTask(g)
            }
            val e: List<Estado>? = s.estados
            if (e != null) {
                dataBase.estadoDao().insertEstadoListTask(e)
            }
            val d: List<Distrito>? = s.distritos
            if (d != null) {
                dataBase.distritoDao().insertDistritoListTask(d)
            }
            val m: List<Material>? = s.materials
            if (m != null) {
                dataBase.materialDao().insertMaterialListTask(m)
            }
        }
    }

    override fun getAccesos(usuarioId: Int): LiveData<List<Accesos>> {
        return dataBase.accesosDao().getAccesosById(usuarioId)
    }

    override fun getGrupos(): LiveData<List<Grupo>> {
        return dataBase.grupoDao().getGrupos()
    }

    override fun getEstados(): LiveData<List<Estado>> {
        return dataBase.estadoDao().getEstados()
    }

    override fun getOts(): LiveData<PagedList<Ot>> {
        return dataBase.otDao().getOts().toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun insertOrUpdateOt(t: Ot): Completable {
        return Completable.fromAction {
            val o: Ot? = dataBase.otDao().getOtIdTask(t.otId)
            if (o == null)
                dataBase.otDao().insertOtTask(t)
            else
                dataBase.otDao().updateOtTask(t)
        }
    }

    override fun getOtById(otId: Int): LiveData<Ot> {
        return dataBase.otDao().getOtById(otId)
    }

    override fun getOtDetalleById(otId: Int): LiveData<PagedList<OtDetalle>> {
        return dataBase.otDetalleDao().getOtDetalleById(otId).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getOtPhotoById(id: Int): LiveData<List<OtPhoto>> {
        return dataBase.otPhotoDao().getOtPhotoById(id)
    }

    override fun getDistritos(): LiveData<List<Distrito>> {
        return dataBase.distritoDao().getDistritos()
    }

    override fun getMateriales(): LiveData<List<Material>> {
        return dataBase.materialDao().getMateriales()
    }

    override fun insertOrUpdateOtDetalle(d: OtDetalle): Completable {
        return Completable.fromAction {
            val t: OtDetalle? = dataBase.otDetalleDao().getOtDetalleIdTask(d.otDetalleId)
            if (t == null)
                dataBase.otDetalleDao().insertOtDetalleTask(d)
            else
                dataBase.otDetalleDao().updateOtDetalleTask(d)
        }
    }

    override fun getMaxIdOt(): LiveData<Int> {
        return dataBase.otDao().getMaxIdOt()
    }

    override fun getMaxIdOtDetalle(): LiveData<Int> {
        return dataBase.otDetalleDao().getMaxIdOtDetalle()
    }

    override fun getOtDetalleId(id: Int): LiveData<OtDetalle> {
        return dataBase.otDetalleDao().getOtDetalleId(id)
    }

    override fun closeDetalle(o: OtDetalle): Completable {
        return Completable.fromAction {
            dataBase.otDao().closeOt(o.otId)
            dataBase.otDetalleDao().closeDetalle(o.otDetalleId)
        }
    }

    override fun insertPhoto(f: OtPhoto): Completable {
        return Completable.fromAction {
            val p: OtPhoto? = dataBase.otPhotoDao().getOtPhotoName(f.urlPhoto)
            if (p == null)
                dataBase.otPhotoDao().insertOtPhotoTask(f)
        }
    }

    override fun deletePhoto(o: OtPhoto, context: Context): Completable {
        return Completable.fromAction {
            Util.deletePhoto(o.urlPhoto, context)
            dataBase.otPhotoDao().deleteOtPhotoTask(o)
        }
    }

    override fun getSendOt(i: Int): Observable<List<Ot>> {
        return Observable.create { e ->
            val v: List<Ot> = dataBase.otDao().getAllRegistroTask(i)
            if (v.isNotEmpty()) {
                val list: ArrayList<Ot> = ArrayList()
                for (r: Ot in v) {
                    val details: ArrayList<OtDetalle> = ArrayList()
                    val detalle =
                        dataBase.otDetalleDao().getAllRegistroDetalleTask(r.otId)
                    for (p: OtDetalle in detalle) {
                        p.photos = dataBase.otPhotoDao().getOtPhotoIdTask(p.otDetalleId)
                        details.add(p)
                    }
                    r.detalles = details
                    list.add(r)
                }
                e.onNext(list)
            } else
                e.onError(Throwable("No hay datos disponibles por enviar"))

            e.onComplete()
        }
    }

    override fun sendRegistroOt(body: RequestBody): Observable<Mensaje> {
        return apiService.sendRegistroOt(body)
    }

    override fun updateOt(t: Mensaje): Completable {
        return Completable.fromAction {
            dataBase.otDao().updateEnabledOt(t.codigoBase, t.codigoRetorno)
        }
    }
}