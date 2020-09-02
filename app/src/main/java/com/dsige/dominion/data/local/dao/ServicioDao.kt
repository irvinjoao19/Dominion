package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dominion.data.local.model.Servicio

@Dao
interface ServicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServicioTask(c: Servicio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServicioListTask(c: List<Servicio>)

    @Update
    fun updateServicioTask(vararg c: Servicio)

    @Delete
    fun deleteServicioTask(c: Servicio)

    @Query("SELECT * FROM Servicio")
    fun getServicio(): LiveData<List<Servicio>>

    @Query("SELECT usuarioId FROM Servicio")
    fun getServicioId(): Int

    @Query("SELECT * FROM Servicio WHERE usuarioId =:id")
    fun getServicioById(id: Int): LiveData<List<Servicio>>

    @Query("DELETE FROM Servicio")
    fun deleteAll()

    @Query("SELECT usuarioId FROM Servicio")
    fun getServicioIdTask(): Int
}