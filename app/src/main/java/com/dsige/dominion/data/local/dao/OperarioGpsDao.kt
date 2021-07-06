package com.dsige.dominion.data.local.dao

import androidx.room.*
import com.dsige.dominion.data.local.model.OperarioGps

@Dao
interface OperarioGpsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperarioGpsTask(c: OperarioGps)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperarioGpsListTask(c: List<OperarioGps>)

    @Update
    fun updateOperarioGpsTask(vararg c: OperarioGps)

    @Delete
    fun deleteOperarioGpsTask(c: OperarioGps)

    @Query("SELECT * FROM OperarioGps WHERE estado= 1 ")
    fun getOperarioGpsTask(): List<OperarioGps>

    @Query("DELETE FROM OperarioGps")
    fun deleteAll()

    @Query("UPDATE OperarioGps SET estado = 0 WHERE id=:i ")
    fun updateEnabledGps(i: Int)
}