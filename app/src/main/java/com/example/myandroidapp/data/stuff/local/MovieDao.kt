package com.example.myandroidapp.data.stuff.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myandroidapp.data.movie.Movie

@Dao
interface MovieDao {

    @Query("SELECT * from movies WHERE userId=:userId ORDER BY price ASC")
    fun getAll(userId: String): LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE _id=:id ")
    fun getById(id: String): LiveData<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(movie: Movie)

    @Query("DELETE FROM movies WHERE _id=:id")
    suspend fun delete(id: String)

    @Query("DELETE FROM movies")
    suspend fun deleteAll()
}