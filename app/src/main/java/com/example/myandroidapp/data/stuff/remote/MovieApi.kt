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
        @GET("/movieManagement/api/movies")
        suspend fun find(): List<Movie>

        @GET("/movieManagement/api/movies/{id}")
        suspend fun read(@Path("id") itemId: String): Movie;

        @Headers("Content-Type: application/json")
        @POST("/movieManagement/api/movies")
        suspend fun create(@Body item: Movie): Movie

        @Headers("Content-Type: application/json")
        @PUT("/movieManagement/api/movies/{id}")
        suspend fun update(@Path("id") itemId: String, @Body item: Movie):Movie

        @Headers("Content-Type: application/json")
        @DELETE("/movieManagement/api/movies/{id}")
        suspend fun delete(@Path("id") itemId: String): Movie
    }

    val service: Service = Api.retrofit.create(Service::class.java)

    val eventChannel = Channel<String>()

    init {
        val request = Request.Builder().url(WS_URL).build()
        val webSocket = OkHttpClient().newWebSocket(request, MyWebSocketListener())
    }

    private class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocket", "onOpen")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocket", "onMessage$text")
            runBlocking { eventChannel.send(text) }
        }

//        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//            Log.d("WebSocket", "onMessage bytes")
//            output("Receiving bytes : " + bytes!!.hex())
//        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "onClosing")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocket", "onFailure", t)
            t.printStackTrace()
        }

        private fun output(txt: String) {
            Log.d("WebSocket", txt)
        }
    }
}