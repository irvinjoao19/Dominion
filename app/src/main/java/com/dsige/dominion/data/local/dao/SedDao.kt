package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dominion.data.local.model.Sed

@Dao
interface SedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSedTask(c: Sed)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSedListTask(c: List<Sed>)

    @Update
    fun updateSedTask(vararg c: Sed)

    @Delete
    fun deleteSedTask(c: Sed)

    @Query("SELECT * FROM Sed")
    fun getSed(): LiveData<Sed>

    @Query("SELECT * FROM Sed WHERE codigo =:id")
    fun getSedById(id: String): Sed?

    @Query("DELETE FROM Sed")
    fun deleteAll()
}