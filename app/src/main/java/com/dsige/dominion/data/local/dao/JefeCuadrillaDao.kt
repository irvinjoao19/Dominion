package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dominion.data.local.model.JefeCuadrilla

@Dao
interface JefeCuadrillaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJefeCuadrillaTask(c: JefeCuadrilla)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJefeCuadrillaListTask(c: List<JefeCuadrilla>)

    @Update
    fun updateJefeCuadrillaTask(vararg c: JefeCuadrilla)

    @Delete
    fun deleteJefeCuadrillaTask(c: JefeCuadrilla)

    @Query("SELECT * FROM JefeCuadrilla")
    fun getJefeCuadrillas(): LiveData<List<JefeCuadrilla>>

    @Query("DELETE FROM JefeCuadrilla")
    fun deleteAll()

}