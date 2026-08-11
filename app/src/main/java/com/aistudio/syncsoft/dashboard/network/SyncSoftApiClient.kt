package com.aistudio.syncsoft.dashboard.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SyncSoftApiClient {
    // Replace with your actual local network IP or production URL
    // e.g., "http://192.168.1.XX:3000/" for    // Using the local network IP instead of localhost/10.0.2.2 since you will test on a physical device
    private const val BASE_URL = "http://192.168.1.80:3000/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // Add Auth Interceptor here later to pass NextAuth/Supabase tokens
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val apiService: SyncSoftApiService by lazy {
        retrofit.create(SyncSoftApiService::class.java)
    }
}
