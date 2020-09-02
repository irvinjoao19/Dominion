package com.dsige.dominion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dsige.dominion.data.local.dao.*
import com.dsige.dominion.data.local.model.*

@Database(
    entities = [
        Usuario::class,
        Accesos::class,
        Estado::class,
        Grupo::class,
        Distrito::class,
        Material::class,
        Ot::class,
        OtDetalle::class,
        OtPhoto::class,
        Servicio::class,
        Proveedor::class,
        JefeCuadrilla::class,
        OtReporte::class
    ],
    version = 5, // version 1 en play store
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun accesosDao(): AccesosDao
    abstract fun grupoDao(): GrupoDao
    abstract fun estadoDao(): EstadoDao
    abstract fun otDao(): OtDao
    abstract fun otDetalleDao(): OtDetalleDao
    abstract fun otPhotoDao(): OtPhotoDao
    abstract fun materialDao(): MaterialDao
    abstract fun distritoDao(): DistritoDao
    abstract fun servicioDao(): ServicioDao
    abstract fun proveedorDao(): ProveedorDao
    abstract fun jefeCuadrillaDao(): JefeCuadrillaDao
    abstract fun otReporteDao(): OtReporteDao

    companion object {
        @Volatile
        var INSTANCE: AppDataBase? = null
        val DB_NAME = "dominion_db"
    }

    fun getDatabase(context: Context): AppDataBase {
        if (INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java, "dominion_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return INSTANCE!!
    }
}