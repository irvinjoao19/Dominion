package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dominion.data.local.model.OtDetalle

@Dao
interface OtDetalleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtDetalleTask(c: OtDetalle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtDetalleListTask(c: List<OtDetalle>)

    @Update
    fun updateOtDetalleTask(vararg c: OtDetalle)

    @Delete
    fun deleteOtDetalleTask(c: OtDetalle)

    @Query("SELECT * FROM OtDetalle")
    fun getOtDetalles(): DataSource.Factory<Int, OtDetalle>

    @Query("DELETE FROM OtDetalle")
    fun deleteAll()

    @Query("SELECT * FROM OtDetalle WHERE otId =:id AND tipoTrabajoId =:tipo")
    fun getOtDetalleById(id: Int,tipo:Int): DataSource.Factory<Int, OtDetalle>

    @Query("SELECT * FROM OtDetalle WHERE otDetalleId =:id")
    fun getOtDetalleIdTask(id: Int): OtDetalle

    @Query("SELECT otDetalleId FROM OtDetalle ORDER BY otDetalleId DESC LIMIT 1")
    fun getMaxIdOtDetalle(): LiveData<Int>

    @Query("SELECT * FROM OtDetalle WHERE otDetalleId =:id")
    fun getOtDetalleId(id: Int): LiveData<OtDetalle>

    @Query("UPDATE OtDetalle SET estado = 1 WHERE otDetalleId =:id")
    fun closeDetalle(id: Int)

    @Query("SELECT * FROM OtDetalle WHERE otId =:id AND estado = 1")
    fun getAllRegistroDetalleTask(id: Int): List<OtDetalle>

    @Query("UPDATE OtDetalle SET  estado = 3 WHERE otId=:codigoBase")
    fun updateEnabledDetalle(codigoBase: Int)

    @Query("SELECT COUNT(*) FROM OtDetalle WHERE otId =:id")
    fun getDetalleOts(id: Int): Int

    @Query("SELECT * FROM OtDetalle WHERE estado =:e")
    fun getAllRegistroDetalleActiveTask(e:Int): List<OtDetalle>
}