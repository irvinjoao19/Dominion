package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dominion.data.local.model.Accesos

@Dao
interface AccesosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccesosTask(c: Accesos)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccesosListTask(c: List<Accesos>)

    @Update
    fun updateAccesosTask(vararg c: Accesos)

    @Delete
    fun deleteAccesosTask(c: Accesos)

    @Query("SELECT * FROM Accesos")
    fun getAccesos(): LiveData<Accesos>

    @Query("SELECT usuarioId FROM Accesos")
    fun getAccesosId(): Int

    @Query("SELECT * FROM Accesos WHERE usuarioId =:id")
    fun getAccesosById(id: Int): LiveData<List<Accesos>>

    @Query("DELETE FROM Accesos")
    fun deleteAll()

    @Query("SELECT usuarioId FROM Accesos")
    fun getAccesosIdTask(): Int
}