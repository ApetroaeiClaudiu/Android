package com.example.myandroidapp.data.movie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.myandroidapp.core.Api
import com.example.myandroidapp.core.Constants
import com.example.myandroidapp.core.TAG
import com.example.myandroidapp.core.Result
import com.example.myandroidapp.data.stuff.local.MovieDao
import com.example.myandroidapp.data.stuff.remote.MovieApi
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.myandroidapp.data.stuff.remote.MessageData

class MovieRepository(private val movieDao: MovieDao) {

    val movies = MediatorLiveData<List<Movie>>().apply { postValue(emptyList()) }

    init {
        CoroutineScope(Dispatchers.Main).launch { collectEvents() }
    }

    suspend fun refresh(): Result<Boolean> {
        try {
            val items = MovieApi.service.find()
            movies.value = items;
            for (item in items) {
                movieDao.insert(item)
            }
            return Result.Success(true)
        } catch(e: Exception) {
            Log.v(TAG, "Suntem in offline!");
            val userId = Constants.instance()?.fetchValueString("_id")
            Log.v(TAG, "Avem ID $userId");
            movies.addSource(movieDao.getAll(userId!!)){
                movies.value = it;
            }
            return Result.Error(e)
        }
    }

    fun getById(itemId: String): LiveData<Movie> {
        return movieDao.getById(itemId)
    }

    suspend fun save(item: Movie): Result<Movie> {
        try {
            val createdItem = MovieApi.service.create(item)
            movieDao.insert(createdItem)
            return Result.Success(createdItem)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(item: Movie): Result<Movie> {
        try {
            val updatedItem = MovieApi.service.update(item._id, item)
            movieDao.update(updatedItem)
            return Result.Success(updatedItem)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(itemId: String): Result<Boolean> {
        try {
            Log.d("DELETE-----------------",itemId);
            MovieApi.service.delete(itemId)
            movieDao.delete(itemId)
            return Result.Success(true)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    private suspend fun collectEvents() {
        while (true) {
            val messageData = Gson().fromJson(Api.eventChannel.receive(), MessageData::class.java)
            Log.d("GLF: collectEvents", "received $messageData")
            handleMessage(messageData)
        }
    }

    private suspend fun handleMessage(messageData: MessageData) {
        val game = messageData.payload
        when (messageData.type) {
            "created" -> movieDao.insert(game)
            "updated" -> movieDao.update(game)
            "deleted" -> movieDao.delete(game._id)
            else -> {
                Log.d("GLF: handleMessage", "received $messageData")
            }
        }
    }
}