package com.example.myandroidapp.data.stuff.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myandroidapp.data.movie.Movie
import retrofit2.http.DELETE

@Dao
interface MovieDao {

    @Query("SELECT * from movies ORDER BY price ASC")
    fun getAll(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE id=:id ")
    fun getById(id: String): LiveData<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(guitar: Movie)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(guitar: Movie)

    @Delete
    suspend fun delete(guitar: Movie)

    @Query("DELETE FROM movies")
    suspend fun deleteAll()
}