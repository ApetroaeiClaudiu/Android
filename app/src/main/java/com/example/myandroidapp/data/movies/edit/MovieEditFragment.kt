package com.example.myandroidapp.data.movies.edit

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.myandroidapp.R
import com.example.myandroidapp.data.movie.Movie
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_item.*
import java.time.LocalDateTime
import java.time.ZoneOffset

class MovieEditFragment : Fragment() {

    companion object {
        const val ITEM_ID = "ITEM_ID"
    }

    private lateinit var viewModel: MovieEditViewModel
    private var itemId: String? = null
    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(javaClass.name, "onCreate")
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                itemId = it.getString(ITEM_ID).toString()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(javaClass.name, "onCreateView")
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(javaClass.name, "onActivityCreated")
        setupViewModel()
        fab.setOnClickListener {
            Log.v(javaClass.name, "save movie")
            val i = movie
            if (i != null) {
                i.title = title_input.text.toString()
                i.director = director_input.text.toString()
                i.year = date_input.text.toString()
                i.treiD = treiD_checkbox.isChecked
                i.price = price_input.text.toString().toInt()
                viewModel.saveOrUpdate(i)
            }
        }
        delete_button.setOnClickListener {
            Log.v(javaClass.name, "delete movie")
            val i = movie
            if (i != null) {
                viewModel.delete(i)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MovieEditViewModel::class.java)
        viewModel.fetching.observe(viewLifecycleOwner) { fetching ->
            Log.v(javaClass.name, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        }
        viewModel.fetchingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.v(javaClass.name, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.completed.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                Log.v(javaClass.name, "completed, navigate back")
                findNavController().popBackStack()
            }
        }
        val id = itemId
        if (id == null) {
            movie = Movie("", "", "", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(), false,0)
        } else {
            viewModel.getItemById(id).observe(viewLifecycleOwner) {
                Log.v(javaClass.name, "update movie")
                if (it != null) {
                    movie = it
                    title_input.setText(it.title)
                    director_input.setText(it.director)
                    price_input.setText(it.price.toString())
                    date_input.setText(it.year)
                    treiD_checkbox.isChecked = it.treiD
                }
            }
        }

//    private fun setupViewModel() {
//        viewModel = ViewModelProvider(this).get(GuitarEditViewModel::class.java)
//        viewModel.fetching.observe(viewLifecycleOwner, { fetching ->
//            Log.v(javaClass.name, "update fetching")
//            progress.visibility = if (fetching) View.VISIBLE else View.GONE
//        })
//        viewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
//            if (exception != null) {
//                Log.v(javaClass.name, "update fetching error")
//                val message = "Fetching exception ${exception.message}"
//                val parentActivity = activity?.parent
//                if (parentActivity != null) {
//                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//        viewModel.completed.observe(viewLifecycleOwner, { completed ->
//            if (completed) {
//                Log.v(javaClass.name, "completed, navigate back")
//                findNavController().popBackStack()
//            }
//        })
//        val id = itemId
//        if (id == null) {
//            guitar = Guitar("", "", 0.0, LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(), false)
//        } else {
//            viewModel.getItemById(id).observe(viewLifecycleOwner, {
//                Log.v(javaClass.name, "update guitar")
//                if(it != null) {
//                    guitar = it
//                    model_input.setText(it.model)
//                    price_input.setText(it.price.toString())
//                    date_input.setText(it.producedOn)
//                    available_checkbox.isChecked = it.available
//                }
//            })
//        }
    }
}
