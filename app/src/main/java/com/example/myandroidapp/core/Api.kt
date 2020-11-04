package com.example.myandroidapp.core

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat

object Api {
    private const val URL = "http://192.168.0.194:8080/"
    const val WS_URL = "ws://192.168.0.194:8080/"
//private const val URL = "http://188.24.53.206:8080/"
//    const val WS_URL = "ws://188.24.53.206:8080/"

    private val client: OkHttpClient = OkHttpClient.Builder()
//        .apply {
//        this.addInterceptor(tokenInterceptor)
//    }
        .build()

    private var gson = GsonBuilder()
        .setLenient()
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
}
