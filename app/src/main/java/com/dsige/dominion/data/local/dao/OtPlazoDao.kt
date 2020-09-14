package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dominion.data.local.model.OtPlazo
import com.dsige.dominion.data.local.model.Proveedor

@Dao
interface OtPlazoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtPlazoTask(c: OtPlazo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtPlazoListTask(c: List<OtPlazo>)

    @Update
    fun updateOtPlazoTask(vararg c: OtPlazo)

    @Delete
    fun deleteOtPlazoTask(c: OtPlazo)

    @Query("DELETE FROM OtPlazo")
    fun deleteAll()

    @Query("SELECT * FROM OtPlazo WHERE empresaId =:id")
    fun getOtPlazoById(id: Int): LiveData<OtPlazo>

    @Query("SELECT * FROM OtPlazo")
    fun getOtPlazos(): DataSource.Factory<Int, OtPlazo>
}