package com.example.myandroidapp.data.movie

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDateTime

@Entity(tableName = "movies")
data class Movie(
        @PrimaryKey @ColumnInfo(name = "_id") var _id: String,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "director") var director: String,
        @ColumnInfo(name = "year") var year: String,
        @ColumnInfo(name = "treiD") var treiD: Boolean,
        @ColumnInfo(name = "price") var price:Int,
        @ColumnInfo(name = "userId") var userId:String
){
    override fun toString(): String {
        return "Movie(_id='$_id', title='$title', director='$director',year='$year', price='$price',treiD='$treiD')"
    }

    fun toDisplay(): String {
    //        2008-10-31T20:08:13+02:00
        return "$title by $director in ${this.year.split("T")[0]} , costs $price , treiD: $treiD"
    }
}

class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(value) }
    }

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date.toString()
    }
}