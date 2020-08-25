package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dominion.data.local.model.Material

@Dao
interface MaterialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMaterialTask(c: Material)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMaterialListTask(c: List<Material>)

    @Update
    fun updateMaterialTask(vararg c: Material)

    @Delete
    fun deleteMaterialTask(c: Material)

    @Query("SELECT * FROM Material")
    fun getMateriales(): LiveData<List<Material>>

    @Query("DELETE FROM Material")
    fun deleteAll()
}