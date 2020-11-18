package com.example.myandroidapp.core

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.channels.Channel
import java.text.DateFormat

object Api {
    private const val URL = "http://192.168.0.194:3001/"
    const val WS_URL = "ws://192.168.0.194:3001/"
    //private const val URL = "http://192.168.0.194:8080/"
    //const val WS_URL = "ws://192.168.0.194:8080/"

    val tokenInterceptor = TokenInterceptor()

    private val client: OkHttpClient = OkHttpClient.Builder()
            .apply {
                this.addInterceptor(tokenInterceptor)
            }.build()

    private var gson = GsonBuilder()
            .setLenient()
            .create()

    val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

    val eventChannel = Channel<String>()

    init {
        val request = Request.Builder().url(WS_URL).build()
        OkHttpClient().newWebSocket(request, MyWebSocketListener())
    }

    private class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            val token = Constants.instance()?.fetchValueString("token")
            webSocket.send("{\"type\":\"authorization\",\"payload\":{\"token\":\"${token}\"}}")
            Log.d("WebSocket", "onOpen")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocket", "onMessage$text")
            runBlocking { eventChannel.send(text) }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
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
