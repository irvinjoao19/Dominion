package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.dsige.dominion.data.local.model.OtPlazoDetalle

@Dao
interface OtPlazoDetalleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtPlazoDetalleTask(c: OtPlazoDetalle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtPlazoDetalleListTask(c: List<OtPlazoDetalle>)

    @Update
    fun updateOtPlazoDetalleTask(vararg c: OtPlazoDetalle)

    @Delete
    fun deleteOtPlazoDetalleTask(c: OtPlazoDetalle)

    @Query("SELECT * FROM OtPlazoDetalle")
    fun getOtPlazoDetalles(): PagingSource<Int, OtPlazoDetalle>

    @Query("DELETE FROM OtPlazoDetalle")
    fun deleteAll()

    @Query("SELECT * FROM OtPlazoDetalle WHERE otId =:id")
    fun getOtPlazoDetalleById(id: Int): LiveData<OtPlazoDetalle>

}