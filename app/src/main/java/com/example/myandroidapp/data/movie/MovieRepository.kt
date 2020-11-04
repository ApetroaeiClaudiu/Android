package com.example.myandroidapp.data.movie

import androidx.lifecycle.LiveData
import com.example.myandroidapp.data.stuff.local.MovieDao
import com.example.myandroidapp.data.stuff.remote.MovieApi

class MovieRepository(private val movieDao: MovieDao) {

    val items = movieDao.getAll();

    suspend fun refresh() {
        val items = MovieApi.service.find()
        for (item in items) {
            movieDao.insert(item)
        }
    }

    fun getById(itemId: String): LiveData<Movie> {
        return movieDao.getById(itemId)
    }

    suspend fun save(movie: Movie, local:Boolean = false): Movie {
        var savedMovie = movie
        println(movie)
        if(!local) {
            savedMovie= MovieApi.service.create(movie)
        }
        movieDao.insert(savedMovie)
        return savedMovie
    }

    suspend fun update(movie: Movie, local:Boolean = false): Movie {
        var updatedItem = movie
        if(!local) {
            println(movie.treiD)
            updatedItem = MovieApi.service.update(movie.id, movie)
        }
        movieDao.update(updatedItem)
        return updatedItem
    }

    suspend fun delete(movie: Movie, local:Boolean = false): Movie {
        if (!local){
            movieDao.delete(movie)
            MovieApi.service.delete(movie.id)
        }
        else{
            movieDao.delete(movie)
        }
        return movie
    }
}
