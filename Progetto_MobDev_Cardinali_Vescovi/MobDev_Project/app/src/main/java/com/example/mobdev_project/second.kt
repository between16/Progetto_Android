package com.example.mobdev_project

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class second : Fragment() {

    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //To show the Steps variable
        (requireActivity() as MainActivity).show()

        val buttonStepsToCharts: TextView = view.findViewById(R.id.stepCharts)
        val navControllerSteps = view.findNavController()
        buttonStepsToCharts.setOnClickListener { navControllerSteps.navigate(R.id.action_second_to_steps_chart) }


        //recycle view initialization
        val rW: RecyclerView = view.findViewById(R.id.rWSteps)
        rW.layoutManager = LinearLayoutManager(context)
        rW.setHasFixedSize(true)

        // Variable to hold the adapter
        var adapter: MyAdapter? = null


        // Launch a coroutine to get data from DB
        myCoroutineScope.launch {
            try {
                val values = getData("Steps")

                // Create the adapter with the fetched data
                adapter = MyAdapter(values)

                // Update the RecyclerView adapter on the main thread
                withContext(Dispatchers.Main) {
                    rW.adapter = adapter
                }
            } catch (e: Exception) {
                // Catch error and log them
                Log.e("StepsNote", "Error retrieving data: ${e.message}")
                //show error to user as well
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    //Method to retrieve data from Database
    private suspend fun getData(type: String): Array<Tracking> {
        val dbDAO = TrackingDatabase.getInstance(requireContext()).getDao()

        return withContext(Dispatchers.IO) {
            try {
                // Attempt to retrieve the list from the database
                dbDAO.getList(type)
            } catch (e: Exception) {
                //Catch errors
                Log.e("SleepNote", "Error retrieving data from database: ${e.message}")
                // Return an empty array to indicate failure
                emptyArray()
            }
        }
    }

}

