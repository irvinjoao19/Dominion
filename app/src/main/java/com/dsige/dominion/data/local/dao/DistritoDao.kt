package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dominion.data.local.model.Distrito

@Dao
interface DistritoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistritoTask(c: Distrito)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistritoListTask(c: List<Distrito>)

    @Update
    fun updateDistritoTask(vararg c: Distrito)

    @Delete
    fun deleteDistritoTask(c: Distrito)

    @Query("SELECT * FROM Distrito")
    fun getDistritos(): LiveData<List<Distrito>>

    @Query("DELETE FROM Distrito")
    fun deleteAll()

    @Query("SELECT distritoId FROM Distrito WHERE nombreDistrito LIKE :d")
    fun searchDistritoId(d: String): Int
}