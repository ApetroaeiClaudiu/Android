package com.example.myandroidapp.data.movies.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myandroidapp.data.movie.Movie
import com.example.myandroidapp.data.movie.MovieRepository
import com.example.myandroidapp.data.stuff.local.MovieDatabase
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
        Log.v(javaClass.name, "getItemById...")
        return movieRepository.getById(itemId)
    }

    fun saveOrUpdate(item: Movie) {
        viewModelScope.launch {
            Log.v(javaClass.name, "saveOrUpdateItem...");
            mutableFetching.value = true
            mutableException.value = null
            try {
                if (item.id.isNotEmpty()) {
                    movieRepository.update(item)
                } else {
                    movieRepository.save(item)
                }
            }
            catch (e: Exception) {
                Log.w(javaClass.name, "saveOrUpdateItem failed", e);
                mutableException.value = e
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

    fun delete(item: Movie) {
        viewModelScope.launch {
            Log.v(javaClass.name, "delete...");
            mutableFetching.value = true
            mutableException.value = null
            try {
                movieRepository.delete(item);
            }
            catch (e: Exception) {
                Log.w(javaClass.name, "delete failed", e);
                mutableException.value = e
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}