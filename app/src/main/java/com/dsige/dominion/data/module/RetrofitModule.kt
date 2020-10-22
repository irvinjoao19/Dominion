package com.dsige.dominion.data.module

import com.dsige.dominion.data.local.AppDataBase
import com.dsige.dominion.data.local.repository.*
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module(includes = [ViewModelModule::class])
class RetrofitModule {

    @Provides
    internal fun providesRetrofit(
        gsonFactory: GsonConverterFactory,
        rxJava: RxJava2CallAdapterFactory,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addCallAdapterFactory(rxJava)
            .addConverterFactory(gsonFactory)
            .client(client)
            .build()
    }

    @Provides
    internal fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    internal fun providesGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    internal fun providesRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    internal fun provideService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    internal fun provideRepository(apiService: ApiService, database: AppDataBase): AppRepository {
        return AppRepoImp(apiService, database)
    }

    @Provides
    internal fun provideError(retrofit: Retrofit): ApiError {
        return ApiError(retrofit)
    }

    companion object {
        private val BASE_URL = "http://190.223.38.245/webApiDominion/api/Dominion/"
    }
}