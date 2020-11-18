package com.example.myandroidapp.data.movies.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myandroidapp.core.TAG
import com.example.myandroidapp.data.movie.Movie
import com.example.myandroidapp.data.movie.MovieRepository
import com.example.myandroidapp.data.stuff.local.MovieDatabase
import com.example.myandroidapp.core.Result
import kotlinx.coroutines.launch

class MovieEditViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    val movieRepository: MovieRepository


    init {
        val movieDao = MovieDatabase.getDatabase(application, viewModelScope).movieDao();
        movieRepository = MovieRepository(movieDao);
    }

    fun getItemById(itemId: String): LiveData<Movie> {
        Log.v(TAG, "getMovieById...")
        return movieRepository.getById(itemId)
    }

    fun saveOrUpdate(item: Movie) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateItem...");
            mutableFetching.value = true
            mutableException.value = null
            try {
                if (item._id.isNotEmpty()) {
                    movieRepository.update(item)
                } else {
                    item._id = (System.currentTimeMillis() / 1000L).toString();
                    movieRepository.save(item)
                }
            }
            catch (e: Exception) {
                Log.w(TAG, "saveOrUpdateItem failed", e);
                mutableException.value = e
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

    fun delete(itemId: String){
        viewModelScope.launch {
            Log.v(TAG, "deleteItem...");
            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Boolean>
            result = movieRepository.delete(itemId)
            when(result) {
                is Result.Success -> {
                    Log.d(TAG, "deleteItem succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "deleteItem failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}