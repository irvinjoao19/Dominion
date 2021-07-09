package com.dsige.dominion.data.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dsige.dominion.data.local.AppDataBase
import com.dsige.dominion.data.local.model.*
import com.dsige.dominion.helper.Mensaje
import com.dsige.dominion.helper.MensajeDetalle
import com.dsige.dominion.helper.Util
import com.github.nkzawa.socketio.client.IO
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_general.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class AppRepoImp(private val apiService: ApiService, private val dataBase: AppDataBase) :
    AppRepository {

    override fun getUsuarioIdTask(): Int {
        return dataBase.usuarioDao().getUsuarioIdTask()
    }

    override fun getUsuarioId(): Observable<Int> {
        return Observable.create {
            val id = dataBase.usuarioDao().getUsuarioIdTask()
            it.onNext(id)
            it.onComplete()
        }
    }

    override fun getEmpresaIdTask(): Int {
        return dataBase.usuarioDao().getEmpresaIdTask()
    }

    override fun getUsuario(): LiveData<Usuario> {
        return dataBase.usuarioDao().getUsuario()
    }

    override fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String
    ): Observable<Usuario> {
        val u = Filtro(usuario, password, imei, version)
        val json = Gson().toJson(u)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getLogin(body)
    }

    override fun getLogout(login: String): Observable<Mensaje> {
        val u = Filtro(login)
        val json = Gson().toJson(u)
//        Log.i("TAG", json)
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

    override fun deleteSesion(): Completable {
        return Completable.fromAction {
            dataBase.usuarioDao().deleteAll()
            dataBase.accesosDao().deleteAll()
            dataBase.grupoDao().deleteAll()
            dataBase.estadoDao().deleteAll()
            dataBase.otDao().deleteAll()
            dataBase.otDetalleDao().deleteAll()
            dataBase.otPhotoDao().deleteAll()
            dataBase.materialDao().deleteAll()
            dataBase.distritoDao().deleteAll()
            dataBase.servicioDao().deleteAll()
            dataBase.codigOtsDao().deleteAll()
        }
    }

    override fun deleteSync(): Completable {
        return Completable.fromAction {
            dataBase.grupoDao().deleteAll()
            dataBase.estadoDao().deleteAll()
            dataBase.otDao().deleteAll()
            dataBase.otDetalleDao().deleteAll()
            dataBase.otPhotoDao().deleteAll()
            dataBase.materialDao().deleteAll()
            dataBase.distritoDao().deleteAll()
            dataBase.servicioDao().deleteAll()
            dataBase.codigOtsDao().deleteAll()
        }
    }

    override fun getSync(u: Int, e: Int, p: Int, v: String): Observable<Sync> {
        val f = Filtro(u, e, p, v)
        val json = Gson().toJson(f)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getSync(body)
    }

    override fun saveSync(s: Sync): Completable {
        return Completable.fromAction {
            val o: List<Ot>? = s.ots
            if (o != null) {
                dataBase.otDao().insertOtListTask(o)
                for (ot: Ot in o) {
                    val d: List<OtDetalle>? = ot.detalles
                    if (d != null) {
                        dataBase.otDetalleDao().insertOtDetalleListTask(d)
                        for (p: OtDetalle in d) {
                            val f: List<OtPhoto> = p.photos
                            if (f.isNotEmpty()) {
                                dataBase.otPhotoDao().insertOtPhotoListTask(f)
                            }
                        }
                    }
                }
            }
            val g: List<Grupo>? = s.groups
            if (g != null) {
                dataBase.grupoDao().insertGrupoListTask(g)

                val se: List<Servicio>? = s.servicios
                if (se != null) {
                    dataBase.servicioDao().insertServicioListTask(se)

                    se.forEach {
                        if (it.servicioId == g[0].servicioId) {
                            dataBase.usuarioDao().updateServicio(
                                it.nombreServicio,
                                g[0].servicioId,
                                g[0].grupoId,
                                g[0].descripcion
                            )
                            return@forEach
                        }
                    }
                }
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
            val s7: List<Sed>? = s.seds
            if (s7 != null) {
                dataBase.sedDao().insertSedListTask(s7)
            }
            val s8: List<CodigOts>? = s.codigos
            if (s8 != null) {
                dataBase.codigOtsDao().insertCodigOtsListTask(s8)
            }
        }
    }

    override fun getAccesos(usuarioId: Int): LiveData<List<Accesos>> {
        return dataBase.accesosDao().getAccesosById(usuarioId)
    }

    override fun getGrupos(): LiveData<List<Grupo>> {
        return dataBase.grupoDao().getGrupos()
    }

    override fun getGrupoByServicioId(id: Int): LiveData<List<Grupo>> {
        return dataBase.grupoDao().getGrupoByServicioId(id)
    }

    override fun getEstados(): LiveData<List<Estado>> {
        return dataBase.estadoDao().getEstados()
    }

    override fun getOts(): LiveData<PagingData<Ot>> {
        return Pager(
            PagingConfig(
                pageSize = 20, enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            dataBase.otDao().getOts()
        }.liveData
    }

    override fun getOts(t: Int, e: Int): LiveData<PagingData<Ot>> {
        return Pager(
            PagingConfig(
                pageSize = 20, enablePlaceholders = true,
                maxSize = 200
            )
        ) { dataBase.otDao().getOts(t, e) }.liveData
    }

    override fun getOts(t: Int, e: Int, s: String): LiveData<PagingData<Ot>> {
        return Pager(
            PagingConfig(
                pageSize = 20, enablePlaceholders = true,
                maxSize = 200
            )
        ) { dataBase.otDao().getOts(t, e, s) }.liveData
    }

    override fun getOts(t: Int, e: Int, s: Int): LiveData<PagingData<Ot>> {
        return Pager(
            PagingConfig(
                pageSize = 20, enablePlaceholders = true,
                maxSize = 200
            )
        ) { dataBase.otDao().getOts(t, e, s) }.liveData
    }

    override fun getOts(t: Int, e: Int, sId: Int, s: String): LiveData<PagingData<Ot>> {
        return Pager(
            PagingConfig(
                pageSize = 20, enablePlaceholders = true,
                maxSize = 200
            )
        ) { dataBase.otDao().getOts(t, e, sId, s) }.liveData
    }

    override fun insertOrUpdateOt(t: Ot): Completable {
        return Completable.fromAction {

            val otr: Boolean = dataBase.codigOtsDao().getCodigOts(t.nroObra)
            if (otr) {
                error("OT ya registrada")
            }

            if (t.servicioId != 2) {
                if (t.distritoId == 0) {
                    t.distritoId = dataBase.distritoDao()
                        .searchDistritoId(
                            String.format(
                                "%s%s%s",
                                "%",
                                t.nombreDistritoId.uppercase(),
                                "%"
                            )
                        )
                }
            } else {
                val d: Sed? = dataBase.sedDao().getSedById(t.nroSed)
                if (d == null) {
                    if (t.distritoId == 0) {
                        error("Sed no encontrado")
                    }
                } else {
                    t.nroSed = d.codigo
                    t.distritoId = d.distritoId
                    t.distritoIdGps = d.distritoId
                    t.nombreDistritoId = d.distrito
                }
            }

            val o: Ot? = dataBase.otDao().getOtExistIdTask(t.otId)
            if (o == null) {
                val ot: Boolean = dataBase.otDao().getNroOt(t.nroObra, t.fechaXOt)
                if (ot) {
                    error("OT ya registrada")
                }
                dataBase.otDao().insertOtTask(t)
            } else {
                val a = dataBase.otDetalleDao().getDetalleOts(t.otId)
                if (a > 0) {
                    t.estado = 1
                }
                dataBase.otDao().updateOtTask(t)
            }
        }
    }

    override fun getOtById(otId: Int): LiveData<Ot> {
        return dataBase.otDao().getOtById(otId)
    }

    override fun getOtDetalleById(otId: Int, tipo: Int): LiveData<PagingData<OtDetalle>> {
        return Pager(
            PagingConfig(
                pageSize = 20, enablePlaceholders = true,
                maxSize = 200
            )
        ) { dataBase.otDetalleDao().getOtDetalleById(otId, tipo) }.liveData
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

    override fun getServicios(): LiveData<List<Servicio>> {
        return dataBase.servicioDao().getServicio()
    }

    override fun insertOrUpdateOtDetalle(d: OtDetalle): Completable {
        return Completable.fromAction {
            val t: OtDetalle? = dataBase.otDetalleDao().getOtDetalleIdTask(d.otDetalleId)
            if (t == null)
                dataBase.otDetalleDao().insertOtDetalleTask(d)
            else
                dataBase.otDetalleDao().updateOtDetalleTask(d)

            if (d.estado == 1) {
                dataBase.otDao().closeOt(d.otId)
            }
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

    override fun insertPhoto(f: OtPhoto): Completable {
        return Completable.fromAction {
            val p: OtPhoto? = dataBase.otPhotoDao().getOtPhotoName(f.urlPhoto)
            if (p == null)
                dataBase.otPhotoDao().insertOtPhotoTask(f)
        }
    }

    override fun insertMultiPhoto(f: ArrayList<OtPhoto>): Completable {
        return Completable.fromAction {
            for (photos: OtPhoto in f) {
                val p: OtPhoto? = dataBase.otPhotoDao().getOtPhotoName(photos.urlPhoto)
                if (p == null)
                    dataBase.otPhotoDao().insertOtPhotoTask(photos)
            }
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

    override fun updateOt(t: Mensaje): Completable {
        return Completable.fromAction {
            dataBase.otDao().updateEnabledOt(t.codigoBase, t.codigoRetorno)
            dataBase.otDetalleDao().updateEnabledDetalle(t.codigoBase)
            val detalle: List<MensajeDetalle>? = t.detalle
            if (detalle != null) {
                for (d: MensajeDetalle in detalle) {
                    dataBase.otPhotoDao().updateEnabledPhoto(t.codigoBase)
                }
            }
        }
    }

    override fun saveGps(body: RequestBody): Call<Mensaje> {
        return apiService.saveGps(body)
    }

    override fun saveMovil(body: RequestBody): Call<Mensaje> {
        return apiService.saveMovil(body)
    }

    override fun deleteOtDetalle(o: OtDetalle, context: Context): Completable {
        return Completable.fromAction {
            val photos: List<OtPhoto> = dataBase.otPhotoDao().getOtPhotoIdTask(o.otDetalleId)
            if (photos.isNotEmpty()) {
                for (p: OtPhoto in photos) {
                    Util.deletePhoto(p.urlPhoto, context)
                    dataBase.otPhotoDao().deleteOtPhotoTask(p)
                }
            }
            if (o.viajeIndebido == 1) {
                dataBase.otDao().updateViajeIndebido(o.otId,0)
            }
            dataBase.otDetalleDao().deleteOtDetalleTask(o)
        }
    }

    override fun getProveedor(f: Filtro): Observable<List<Proveedor>> {
        val json = Gson().toJson(f)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getProveedor(body)
    }

    override fun getProveedores(): LiveData<PagingData<Proveedor>> {
        return Pager(
            PagingConfig(
                pageSize = 20, enablePlaceholders = true,
                maxSize = 200
            )
        ) { dataBase.proveedorDao().getProveedores() }.liveData
    }

    override fun clearProveedores(): Completable {
        return Completable.fromAction {
            dataBase.proveedorDao().deleteAll()
        }
    }

    override fun insertProveedor(t: List<Proveedor>): Completable {
        return Completable.fromAction {
            dataBase.proveedorDao().insertProveedorListTask(t)
        }
    }

    override fun getEmpresa(f: Filtro): Observable<List<OtReporte>> {
        val json = Gson().toJson(f)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getEmpresa(body)
    }

    override fun insertEmpresa(t: List<OtReporte>): Completable {
        return Completable.fromAction {
            dataBase.otReporteDao().insertOtReporteListTask(t)
        }
    }

    override fun getOtReporte(): LiveData<List<OtReporte>> {
        return dataBase.otReporteDao().getOtReportes()
    }

    override fun getEmpresaById(id: Int): LiveData<OtReporte> {
        return dataBase.otReporteDao().getOtReporteById(id)
    }

    override fun getJefeCuadrilla(f: Filtro): Observable<List<JefeCuadrilla>> {
        val json = Gson().toJson(f)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getJefeCuadrilla(body)
    }

    override fun insertJefeCuadrilla(t: List<JefeCuadrilla>): Completable {
        return Completable.fromAction {
            dataBase.jefeCuadrillaDao().insertJefeCuadrillaListTask(t)
        }
    }

    override fun getJefeCuadrillas(): LiveData<List<JefeCuadrilla>> {
        return dataBase.jefeCuadrillaDao().getJefeCuadrillas()
    }

    override fun getJefeCuadrillaById(id: Int): LiveData<JefeCuadrilla> {
        return dataBase.jefeCuadrillaDao().getJefeCuadrillaById(id)
    }

    override fun getOtPlazos(): LiveData<PagingData<OtPlazo>> {
        return Pager(
            PagingConfig(
                pageSize = 20, enablePlaceholders = true, maxSize = 200
            )
        ) { dataBase.otPlazoDao().getOtPlazos() }.liveData
    }

    override fun getOtPlazo(f: Filtro): Observable<List<OtPlazo>> {
        val json = Gson().toJson(f)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getOtPlazo(body)
    }

    override fun insertOtPlazo(t: List<OtPlazo>): Completable {
        return Completable.fromAction {
            dataBase.otPlazoDao().insertOtPlazoListTask(t)
        }
    }

    override fun getOtPlazoDetalle(f: Filtro): Observable<List<OtPlazoDetalle>> {
        val json = Gson().toJson(f)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getOtPlazoDetalle(body)
    }

    override fun insertOtPlazoDetalle(t: List<OtPlazoDetalle>): Completable {
        return Completable.fromAction {
            dataBase.otPlazoDetalleDao().insertOtPlazoDetalleListTask(t)
        }
    }

    override fun clearOtPlazo(): Completable {
        return Completable.fromAction {
            dataBase.otPlazoDao().deleteAll()
        }
    }

    override fun getOtPlazoDetalles(): LiveData<PagingData<OtPlazoDetalle>> {
        return Pager(
            PagingConfig(
                pageSize = 20, enablePlaceholders = true, maxSize = 200
            )
        ) { dataBase.otPlazoDetalleDao().getOtPlazoDetalles() }.liveData
    }

    override fun clearOtPlazoDetalle(): Completable {
        return Completable.fromAction {
            dataBase.otPlazoDetalleDao().deleteAll()
        }
    }

    override fun sendSocket(): Completable {
        return Completable.fromAction {
            val v: List<OtNotify> = dataBase.otDao().getAllRegistroSocket()

            val user = dataBase.usuarioDao().getUser()
            val list: ArrayList<Notificacion> = ArrayList()
            if (v.isNotEmpty()) {
                for (r: OtNotify in v) {
                    val n = Notificacion()
                    n.idEmpresa = r.empresaId.toString()
                    n.cantidadOT = r.cantidad
                    n.idServicio = r.servicioId.toString()
                    n.idTipoOT = r.tipoOrdenId.toString()
                    n.idCuadrilla = r.usuarioId.toString()

                    val tipo = when (r.tipoOrdenId) {
                        3 -> "ROTURA"
                        4 -> "REPARACION"
                        else -> "RECOJO"
                    }
                    n.mensaje = String.format(
                        "Se ejecutó %s Ot de %s de la Empresa %s y por el Jefe de Cuadrilla %s %s",
                        r.cantidad, tipo, user.nombreEmpresa, user.nombres, user.apellidos
                    )

                    list.add(n)
                }
                dataBase.otDao().updateSocket()
            }

            val web = Gson().toJson(list)
//            Log.i("socket", web)
            try {
                val socket = IO.socket(Util.UrlSocket)
                socket.connect()
                socket.emit("Notificacion_movil_web_OT", web)
            } catch (e: URISyntaxException) {
            }
        }
    }

    override fun getOtPhotoTask(): Observable<List<String>> {
        return Observable.create { e ->
            val data: ArrayList<String> = ArrayList()
            val v: List<Ot> = dataBase.otDao().getAllRegistroTask(1)
            if (v.isNotEmpty()) {
                for (r: Ot in v) {
                    if (r.tipoOrdenId == 3 || r.tipoOrdenId == 4) {
                        if (r.conDesmonte) {
                            val d: List<OtDetalle> =
                                dataBase.otDetalleDao().getAllRegistroDetalleDesmonte(r.otId)
                            if (d.isNotEmpty()) {
                                if (d.isEmpty()) {
                                    e.onError(Throwable("Es obligatorio registrar un desmonte para cada ot"))
                                    e.onComplete()
                                    return@create
                                }
                            }
                        }
                    }

                    if (r.servicioId == 2 || r.servicioId == 4) {
                        if (r.urlPdf.isEmpty()) {
                            e.onError(Throwable("Nro OT ${r.nroObra} necesita archivo pdf."))
                            e.onComplete()
                            return@create
                        } else {
                            data.add(r.urlPdf)
                        }
//                        val d: OtDetalle? =
//                            dataBase.otDetalleDao().getOtDetalleViajeIndebido(r.otId)
//                        if (d == null) {
//                            e.onError(Throwable("Nro OT ${r.nroObra} necesita minimo 1 foto del viaje indebido."))
//                            e.onComplete()
//                            return@create
//                        }
                    }
                }
            }
            val ot = dataBase.otDetalleDao().getAllRegistroDetalleActiveTask(1)
            for (p: OtDetalle in ot) {
                val photos: List<OtPhoto> =
                    dataBase.otPhotoDao().getOtPhotoIdTask(p.otDetalleId)
                if (photos.isNotEmpty()) {
                    for (f: OtPhoto in photos) {
                        data.add(f.urlPhoto)
                    }
                }
            }
            e.onNext(data)
            e.onComplete()
        }
    }

    override fun sendOtPhotos(body: RequestBody): Observable<String> {
        return apiService.sendOtPhotos(body)
    }

    override fun sendOt(body: RequestBody): Observable<Mensaje> {
        return apiService.sendOt(body)
    }

    override fun getSed(sed: String): Observable<Sed> {
        return Observable.create {
            val d: Sed? = dataBase.sedDao().getSedById(sed)
            if (d == null) {
                it.onError(Throwable("Sed no encontrado"))
                it.onComplete()
                return@create
            }
            it.onNext(d)
            it.onComplete()
        }
    }

    override fun insertOtPhotoCabecera(t: OtDetalle): Observable<Int> {
        return Observable.create {
            dataBase.otDao().updateViajeIndebido(t.otId, 1)
            val s = dataBase.otDetalleDao().getMaxIdOtDetalleTask()
            val otDetalleId = if (s != 0) {
                s + 1
            } else 1

            val o: OtDetalle? =
                dataBase.otDetalleDao().getOtDetalleBajaTension(t.otId, t.tipoMaterialId)
            if (o == null) {
                t.otDetalleId = otDetalleId
                dataBase.otDetalleDao().insertOtDetalleTask(t)
                it.onNext(otDetalleId)
            } else {
                it.onNext(o.otDetalleId)
            }
            it.onComplete()
        }
    }

    override fun insertOtPhoto(id: Int, t: List<OtPhoto>): Completable {
        return Completable.fromAction {
            val f: List<OtPhoto> = t
            if (f.isNotEmpty()) {
                for (d: OtPhoto in f) {
                    val count = dataBase.otPhotoDao().getCountPhoto(id)
                    if (count < 3) {
                        d.otDetalleId = id
                        dataBase.otPhotoDao().insertOtPhotoTask(d)
                    }
                }
            }
        }
    }

    override fun getCountOtPhotoBajaTension(otId: Int): LiveData<Int> {
        return dataBase.otPhotoDao().getCountOtPhotoBajaTension(otId)
    }

    override fun getOtPhotoBajaTension(otId: Int): LiveData<List<OtPhoto>> {
        return dataBase.otPhotoDao().getOtPhotoBajaTension(otId)
    }

    override fun deleteOtPhotoBajaTension(otId: Int, context: Context): Completable {
        return Completable.fromAction {
            dataBase.otDetalleDao().deleteOtDetalleBajaTension(otId, 24)
            val f = dataBase.otPhotoDao().getOtPhotoBajaTensionTask(otId)
            for (p: OtPhoto in f) {
                Util.deletePhoto(p.urlPhoto, context)
            }
            dataBase.otDao().updateViajeIndebido(otId,0)
            dataBase.otPhotoDao().deletePhotoBajaTension(otId)
        }
    }

    override fun cerrarTrabajo(otId: Int): Completable {
        return Completable.fromAction {
            val r = dataBase.otDao().getOtIdTask(otId)
            r.fechaFinTrabajo = Util.getFechaActual()
            r.estado = 1
            r.conDesmonte = false
            dataBase.otDao().updateOtTask(r)
        }
    }

    override fun insertGps(e: OperarioGps): Completable {
        return Completable.fromAction {
            dataBase.operarioGpsDao().insertOperarioGpsTask(e)
        }
    }

    override fun getSendGps(): Observable<List<OperarioGps>> {
        return Observable.create {
            val gps: List<OperarioGps> = dataBase.operarioGpsDao().getOperarioGpsTask()
            it.onNext(gps)
            it.onComplete()
        }
    }

    override fun saveOperarioGps(e: OperarioGps): Observable<Mensaje> {
        val json = Gson().toJson(e)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.saveOperarioGps(body)
    }


    override fun updateEnabledGps(t: Mensaje): Completable {
        return Completable.fromAction {
            dataBase.operarioGpsDao().updateEnabledGps(t.codigoBase)
        }
    }

    override fun insertBattery(e: OperarioBattery): Completable {
        return Completable.fromAction {
            dataBase.operarioBatteryDao().insertOperarioBatteryTask(e)
        }
    }

    override fun getSendBattery(): Observable<List<OperarioBattery>> {
        return Observable.create {
            val gps: List<OperarioBattery> =
                dataBase.operarioBatteryDao().getOperarioBatteryTask()
            it.onNext(gps)
            it.onComplete()
        }
    }

    override fun saveOperarioBattery(e: OperarioBattery): Observable<Mensaje> {
        val json = Gson().toJson(e)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.saveOperarioBattery(body)
    }

    override fun updateEnabledBattery(t: Mensaje): Completable {
        return Completable.fromAction {
            dataBase.operarioBatteryDao().updateEnabledBattery(t.codigoBase)
        }
    }

    override fun updateOtPdf(id: Int, path: String): Completable {
        return Completable.fromAction {
            val urlPdf = "${path.substring(0, path.length - 4)}.pdf"
            dataBase.otDao().updateOtPdf(id, urlPdf)
        }
    }
}