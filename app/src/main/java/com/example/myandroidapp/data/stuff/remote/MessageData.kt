package com.example.myandroidapp.data.stuff.remote

import com.example.myandroidapp.data.movie.Movie

data class MessageData(var type: String, var payload: Movie) {
    //data class MovieJson(var item: Movie)
}