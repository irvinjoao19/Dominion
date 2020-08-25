package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dominion.data.local.model.Grupo

@Dao
interface GrupoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrupoTask(c: Grupo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrupoListTask(c: List<Grupo>)

    @Update
    fun updateGrupoTask(vararg c: Grupo)

    @Delete
    fun deleteGrupoTask(c: Grupo)

    @Query("SELECT * FROM Grupo")
    fun getGrupos(): LiveData<List<Grupo>>

    @Query("DELETE FROM Grupo")
    fun deleteAll()
}