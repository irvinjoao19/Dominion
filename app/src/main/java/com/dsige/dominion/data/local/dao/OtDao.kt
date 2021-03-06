package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.dsige.dominion.data.local.model.Ot
import com.dsige.dominion.data.local.model.OtNotify

@Dao
interface OtDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtTask(c: Ot)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtListTask(c: List<Ot>)

    @Update
    fun updateOtTask(vararg c: Ot)

    @Delete
    fun deleteOtTask(c: Ot)

    @Query("SELECT * FROM Ot GROUP BY nroObra")
    fun getOts(): PagingSource<Int, Ot>

    @Query("SELECT * FROM Ot WHERE tipoOrdenId=:t AND estadoId =:e AND direccion LIKE :s")
    fun getOts(t: Int, e: Int, s: String): PagingSource<Int, Ot>

    @Query("SELECT * FROM Ot WHERE tipoOrdenId=:t AND estadoId =:e AND servicioId =:sId AND direccion LIKE :s")
    fun getOts(t: Int, e: Int, sId: Int, s: String): PagingSource<Int, Ot>

    @Query("SELECT * FROM Ot WHERE tipoOrdenId=:t AND estadoId =:e AND servicioId =:s")
    fun getOts(t: Int, e: Int, s: Int): PagingSource<Int, Ot>

    @Query("SELECT * FROM Ot WHERE tipoOrdenId=:t AND estadoId =:e")
    fun getOts(t: Int, e: Int): PagingSource<Int, Ot>

    @Query("DELETE FROM Ot")
    fun deleteAll()

    @Query("SELECT * FROM Ot WHERE otId =:id")
    fun getOtById(id: Int): LiveData<Ot>

    @Query("SELECT * FROM Ot WHERE otId =:id")
    fun getOtIdTask(id: Int): Ot

    @Query("SELECT * FROM Ot WHERE otId =:id")
    fun getOtExistIdTask(id: Int): Ot?

    @Query("SELECT otId FROM Ot ORDER BY otId DESC LIMIT 1")
    fun getMaxIdOt(): LiveData<Int>

    @Query("UPDATE Ot SET estado = 1 WHERE otId =:id")
    fun closeOt(id: Int)

    @Query("SELECT * FROM Ot WHERE estado =:i")
    fun getAllRegistroTask(i: Int): List<Ot>

    @Query("UPDATE Ot SET identity =:codigoRetorno , estado = 0 , estadoId = 5 WHERE otId=:codigoBase")
    fun updateEnabledOt(codigoBase: Int, codigoRetorno: Int)

    @Query("SELECT * FROM OtNotify ")
    fun getAllRegistroSocket(): List<OtNotify>

    @Query("UPDATE Ot SET activeNotificacion = 0")
    fun updateSocket()

    @Query("SELECT * FROM Ot WHERE nroObra=:n AND fechaXOt=:f")
    fun getNroOt(n: String, f: String): Boolean

    @Query("UPDATE Ot SET viajeIndebido =:e WHERE otId=:id")
    fun updateViajeIndebido(id: Int,e:Int)

    @Query("UPDATE Ot SET urlPdf =:path WHERE otId=:id")
    fun updateOtPdf(id: Int, path: String)

}