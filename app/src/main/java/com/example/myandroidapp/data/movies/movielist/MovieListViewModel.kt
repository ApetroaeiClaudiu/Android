package com.example.myandroidapp.data.movies.movielist

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
import java.util.*

class MovieListViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val items: LiveData<List<Movie>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    val movieRepository : MovieRepository

    init {
        val movieDao = MovieDatabase.getDatabase(application, viewModelScope).movieDao()
        movieRepository = MovieRepository(movieDao)
        items = movieRepository.items
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(javaClass.name, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            try {
                movieRepository.refresh()
            } catch(e: Exception) {
                Log.e(javaClass.name,e.toString())
                mutableException.value = e
            }
            mutableLoading.value = false
        }
    }
}