package com.example.myandroidapp.data.movies.movielist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.myandroidapp.R
import com.example.myandroidapp.data.movie.WebSocketEvent
import kotlinx.android.synthetic.main.fragment_item_list.*
import com.example.myandroidapp.data.stuff.remote.MovieApi
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class MovieFragment : Fragment() {

    private lateinit var movieModel: MovieListViewModel
    private lateinit var movieListAdapter: MyMovieRecyclerViewAdapter
    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(javaClass.name, "onCreate")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(javaClass.name, "onActivityCreated")
//        if (!AuthRepository.isLoggedIn) {
//            findNavController().navigate(R.id.fragment_login)
//            return;
//        }
        setupMovieList()
        fab.setOnClickListener {
            Log.v(javaClass.name, "navigate to add new movie")
            findNavController().navigate(R.id.fragment_item)
        }
    }

    override fun onStart() {
        super.onStart()
        isListening = true
        CoroutineScope(Dispatchers.Main).launch { collectWebSocketEvents() }
    }

    override fun onStop() {
        super.onStop()
        isListening = false
    }

    private suspend fun collectWebSocketEvents() {
        while (isListening) {
            val event = Gson().fromJson<WebSocketEvent>(MovieApi.eventChannel.receive(), WebSocketEvent::class.java)
            Log.i("MainActivity", "received $event")
            if (event.type.equals("created")) {
                movieModel.movieRepository.save(event.payload,true)
            } else if(event.type.equals("updated")) {
                movieModel.movieRepository.update(event.payload,true)
            } else if(event.type.equals("deleted")) {
                movieModel.movieRepository.delete(event.payload,true)
            }
        }
    }

    private fun setupMovieList() {
        movieListAdapter = MyMovieRecyclerViewAdapter(this)
        movie_list.adapter = movieListAdapter
        movieModel = ViewModelProvider(this).get(MovieListViewModel::class.java)
        movieModel.items.observe(viewLifecycleOwner) { items ->
            Log.v(javaClass.name, "update items")
            movieListAdapter.items = items
        }
        movieModel.loading.observe(viewLifecycleOwner) { loading ->
            Log.i(javaClass.name, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        }
        movieModel.loadingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.i(javaClass.name, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }
        movieModel.refresh()
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.v(javaClass.name, "onDestroy")
    }
}