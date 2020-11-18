package com.example.myandroidapp.data.stuff.remote

import com.example.myandroidapp.data.movie.Movie
import android.util.Log
import com.example.myandroidapp.core.Api
import com.example.myandroidapp.core.Api.WS_URL
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okio.ByteString
import retrofit2.http.*
import retrofit2.http.Headers

object MovieApi {
    interface Service {
        @GET("/api/movie")
        suspend fun find(): List<Movie>

        @GET("/api/movie/{id}")
        suspend fun read(@Path("id") itemId: String): Movie;

        @Headers("Content-Type: application/json")
        @POST("/api/movie")
        suspend fun create(@Body item: Movie): Movie

        @Headers("Content-Type: application/json")
        @PUT("/api/movie/{id}")
        suspend fun update(@Path("id") itemId: String, @Body item: Movie):Movie

        @Headers("Content-Type: application/json")
        @DELETE("/api/movie/{id}")
        suspend fun delete(@Path("id") itemId: String): Movie
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}