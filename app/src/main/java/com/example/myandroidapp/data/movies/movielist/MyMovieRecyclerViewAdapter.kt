package com.example.myandroidapp.data.movies.movielist

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myandroidapp.R
import com.example.myandroidapp.data.movie.Movie
import com.example.myandroidapp.data.movies.edit.MovieEditFragment
import kotlinx.android.synthetic.main.movie_view.view.*


class MyMovieRecyclerViewAdapter(
    private val fragment: Fragment
): RecyclerView.Adapter<MyMovieRecyclerViewAdapter.ViewHolder>() {

    var items = emptyList<Movie>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    private var onItemClick: View.OnClickListener;

    init {
        onItemClick = View.OnClickListener { view ->
            val item = view.tag as Movie
            fragment.findNavController().navigate(R.id.fragment_item, Bundle().apply {
                putString(MovieEditFragment.ITEM_ID, item.id)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_view, parent, false)
        Log.v(javaClass.name, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.v(javaClass.name, "onBindViewHolder $position")
        val item = items[position]
        holder.itemView.tag = item
        holder.textView.text = item.toDisplay()
        holder.itemView.setOnClickListener(onItemClick)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.text
    }
}