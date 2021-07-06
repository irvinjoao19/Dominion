package com.dsige.dominion.data.local.dao

import androidx.room.*
import com.dsige.dominion.data.local.model.OperarioBattery

@Dao
interface OperarioBatteryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperarioBatteryTask(c: OperarioBattery)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperarioBatteryListTask(c: List<OperarioBattery>)

    @Update
    fun updateOperarioBatteryTask(vararg c: OperarioBattery)

    @Delete
    fun deleteOperarioBatteryTask(c: OperarioBattery)

    @Query("SELECT * FROM OperarioBattery WHERE estado= 1 ")
    fun getOperarioBatteryTask(): List<OperarioBattery>

    @Query("DELETE FROM OperarioBattery")
    fun deleteAll()

    @Query("UPDATE OperarioBattery SET estado = 0 WHERE id=:i ")
    fun updateEnabledBattery(i: Int)
}