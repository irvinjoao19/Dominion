package com.dsige.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dominion.data.local.model.OtPhoto

@Dao
interface OtPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtPhotoTask(c: OtPhoto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOtPhotoListTask(c: List<OtPhoto>)

    @Update
    fun updateOtPhotoTask(vararg c: OtPhoto)

    @Delete
    fun deleteOtPhotoTask(c: OtPhoto)

    @Query("SELECT * FROM OtPhoto")
    fun getOtPhotos(): DataSource.Factory<Int, OtPhoto>

    @Query("DELETE FROM OtPhoto")
    fun deleteAll()

    @Query("SELECT * FROM OtPhoto WHERE otDetalleId =:id")
    fun getOtPhotoById(id: Int): LiveData<List<OtPhoto>>

    @Query("SELECT * FROM OtPhoto WHERE urlPhoto =:img")
    fun getOtPhotoName(img: String): OtPhoto?

    @Query("SELECT * FROM OtPhoto WHERE otDetalleId =:id")
    fun getOtPhotoIdTask(id: Int): List<OtPhoto>

    @Query("UPDATE OtPhoto SET  estado = 0 WHERE otDetalleId=:codigoBase")
    fun updateEnabledPhoto(codigoBase: Int)

    @Query("SELECT COUNT(*) FROM OtPhoto WHERE otDetalleId =:id")
    fun getCountPhoto(id: Int): Int

    @Query("SELECT COUNT(*) FROM OtPhoto WHERE otId =:id")
    fun getCountOtPhotoBajaTension(id: Int): LiveData<Int>

    @Query("SELECT * FROM OtPhoto WHERE otId =:id")
    fun getOtPhotoBajaTension(id: Int): LiveData<List<OtPhoto>>

    @Query("DELETE FROM OtPhoto WHERE otId =:id")
    fun deletePhotoBajaTension(id: Int)

    @Query("SELECT * FROM OtPhoto WHERE otId =:id")
    fun getOtPhotoBajaTensionTask(id: Int): List<OtPhoto>
}