package com.example.mobdev_project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class fourth : Fragment() {

    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fourth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)
        //initiate the buttons and the navControllers to move to the proper page when button is pressed
        val buttonToNote: TextView = view.findViewById(R.id.move_to_add_note)
        val buttonToChart: TextView = view.findViewById(R.id.move_to_charts)
        val navControllerSteps = view.findNavController()
        buttonToNote.setOnClickListener { navControllerSteps.navigate(R.id.action_fourth_to_bookNote) }
        buttonToChart.setOnClickListener { navControllerSteps.navigate(R.id.action_fourth_to_book_charts) }

        //recycle view initialization
        val rW: RecyclerView = view.findViewById(R.id.rW_book)
        rW.layoutManager = LinearLayoutManager(context)
        rW.setHasFixedSize(true)

        // Variable to hold the adapter
        var adapter: MyAdapter? = null


        // Launch a coroutine to get data from DB
        myCoroutineScope.launch {
            try {
                val values = getData("Book")

                // Create the adapter with the fetched data
                adapter = MyAdapter(values)

                // Update the RecyclerView adapter on the main thread
                withContext(Dispatchers.Main) {
                    rW.adapter = adapter
                }
            } catch (e: Exception) {
                // Log the error and show it to the user
                Log.e("BookNote", "Error retrieving data: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    //use to get data from Database
    private suspend fun getData(type: String): Array<Tracking> {
        val dbDAO = TrackingDatabase.getInstance(requireContext()).getDao()

        return withContext(Dispatchers.IO) {
            try {
                // Attempt to retrieve the list from the database
                dbDAO.getList(type)
            } catch (e: Exception) {
                // Log the error
                Log.e("SleepNote", "Error retrieving data from database: ${e.message}")
                // Return an empty array to indicate failure
                emptyArray()
            }
        }
    }
}