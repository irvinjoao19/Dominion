package com.dsige.dominion.data.module

import android.app.Application
import androidx.room.Room
import com.dsige.dominion.data.local.AppDataBase
import com.dsige.dominion.data.local.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(application: Application): AppDataBase {
        if (AppDataBase.INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (AppDataBase.INSTANCE == null) {
                    AppDataBase.INSTANCE = Room.databaseBuilder(
                        application.applicationContext,
                        AppDataBase::class.java, AppDataBase.DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return AppDataBase.INSTANCE!!
    }

    @Provides
    internal fun provideUsuarioDao(appDataBase: AppDataBase): UsuarioDao {
        return appDataBase.usuarioDao()
    }

    @Provides
    internal fun provideAccesosDao(appDataBase: AppDataBase): AccesosDao {
        return appDataBase.accesosDao()
    }

    @Provides
    internal fun provideEstadoDao(appDataBase: AppDataBase): EstadoDao {
        return appDataBase.estadoDao()
    }

    @Provides
    internal fun provideGrupoDao(appDataBase: AppDataBase): GrupoDao {
        return appDataBase.grupoDao()
    }

    @Provides
    internal fun provideOtDao(appDataBase: AppDataBase): OtDao {
        return appDataBase.otDao()
    }

    @Provides
    internal fun provideOtDetalleDao(appDataBase: AppDataBase): OtDetalleDao {
        return appDataBase.otDetalleDao()
    }

    @Provides
    internal fun provideOtPhotoDao(appDataBase: AppDataBase): OtPhotoDao {
        return appDataBase.otPhotoDao()
    }
    @Provides
    internal fun provideMaterialDao(appDataBase: AppDataBase): MaterialDao {
        return appDataBase.materialDao()
    }
    @Provides
    internal fun provideDistritoDao(appDataBase: AppDataBase): DistritoDao {
        return appDataBase.distritoDao()
    }
}