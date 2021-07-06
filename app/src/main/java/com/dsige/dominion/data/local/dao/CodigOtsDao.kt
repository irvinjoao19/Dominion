package com.dsige.dominion.data.local.dao

import androidx.room.*
import com.dsige.dominion.data.local.model.CodigOts

@Dao
interface CodigOtsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCodigOtsTask(c: CodigOts)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCodigOtsListTask(c: List<CodigOts>)

    @Update
    fun updateCodigOtsTask(vararg c: CodigOts)

    @Delete
    fun deleteCodigOtsTask(c: CodigOts)

    @Query("SELECT * FROM CodigOts WHERE codigo=:s")
    fun getCodigOts(s:String): Boolean

    @Query("DELETE FROM CodigOts")
    fun deleteAll()

}