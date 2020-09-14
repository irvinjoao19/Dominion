package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dominion.data.local.model.OtReporte

@Dao
interface OtReporteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtReporteTask(c: OtReporte)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtReporteListTask(c: List<OtReporte>)

    @Update
    fun updateOtReporteTask(vararg c: OtReporte)

    @Delete
    fun deleteOtReporteTask(c: OtReporte)

    @Query("SELECT * FROM OtReporte")
    fun getOtReportes(): LiveData<List<OtReporte>>

    @Query("DELETE FROM OtReporte")
    fun deleteAll()

    @Query("SELECT * FROM OtReporte WHERE otId =:id")
    fun getOtReporteById(id: Int): LiveData<OtReporte>

}