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


class third : Fragment() {

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
        return inflater.inflate(R.layout.fragment_third, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)
        val buttonToNote: TextView = view.findViewById(R.id.move_to_add_note)
        val buttonToChart: TextView = view.findViewById(R.id.move_to_charts)
        val navControllerSteps = view.findNavController()
        buttonToNote.setOnClickListener { navControllerSteps.navigate(R.id.action_third_to_sleepNote) }


        buttonToChart.setOnClickListener { navControllerSteps.navigate(R.id.action_third_to_charts) }

        //recycle view initialization
        val rW: RecyclerView = view.findViewById(R.id.rW_sleep)
        rW.layoutManager = LinearLayoutManager(context)
        rW.setHasFixedSize(true)

        // Variable to hold the adapter
        var adapter: MyAdapter? = null

        // Launch a coroutine to get data from DB
        myCoroutineScope.launch {
            try {
                val values = getData("Sleep")

                // Create the adapter with the fetched data
                adapter = MyAdapter(values)

                // Update the RecyclerView adapter on the main thread
                withContext(Dispatchers.Main) {
                    rW.adapter = adapter
                }
            } catch (e: Exception) {
                // Log the error and sends it to the user as well
                Log.e("SleepNote", "Error retrieving data: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Get data from database
    private suspend fun getData(type: String): Array<Tracking> {
        val dbDAO = TrackingDatabase.getInstance(requireContext()).getDao()

        return withContext(Dispatchers.IO) {
            try {
                dbDAO.getList(type)
            } catch (e: Exception) {
                //Catch errors
                Log.e("SleepNote", "Error retrieving data from database: ${e.message}")
                // Return an empty array or handle the error as needed
                emptyArray()
            }
        }
    }
}