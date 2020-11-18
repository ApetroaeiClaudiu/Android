package com.example.myandroidapp.data.movies.movielist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.myandroidapp.R
import com.example.myandroidapp.core.TAG
import kotlinx.android.synthetic.main.fragment_item_list.*
import com.example.myandroidapp.remote.AuthRepository

/**
 * A fragment representing a list of Items.
 */
class MovieFragment : Fragment() {

    private lateinit var movieModel: MovieListViewModel
    private lateinit var movieListAdapter: MyMovieRecyclerViewAdapter
    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
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
        Log.v(TAG, "onActivityCreated")
        if (!AuthRepository.isLoggedIn) {
            findNavController().navigate(R.id.login_fragment)
            return;
        }
        setupMovieList()
        fab.setOnClickListener {
            Log.v(TAG, "navigate to add new movie")
            findNavController().navigate(R.id.fragment_item)
        }
        logout.setOnClickListener{
            Log.v(TAG, "logging out")
            AuthRepository.logout();
            findNavController().navigate(R.id.login_fragment);
        }
    }

    private fun setupMovieList() {
        movieListAdapter = MyMovieRecyclerViewAdapter(this)
        movie_list.adapter = movieListAdapter
        movieModel = ViewModelProvider(this).get(MovieListViewModel::class.java)
        movieModel.items.observe(viewLifecycleOwner) { items ->
            Log.v(TAG, "update items")
            movieListAdapter.items = items
        }
        movieModel.loading.observe(viewLifecycleOwner) { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        }
        movieModel.loadingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }
        movieModel.refresh()
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }
}