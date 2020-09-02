package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dominion.data.local.model.Proveedor

@Dao
interface ProveedorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProveedorTask(c: Proveedor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProveedorListTask(c: List<Proveedor>)

    @Update
    fun updateProveedorTask(vararg c: Proveedor)

    @Delete
    fun deleteProveedorTask(c: Proveedor)

    @Query("SELECT * FROM Proveedor")
    fun getProveedores(): DataSource.Factory<Int, Proveedor>

    @Query("DELETE FROM Proveedor")
    fun deleteAll()

}