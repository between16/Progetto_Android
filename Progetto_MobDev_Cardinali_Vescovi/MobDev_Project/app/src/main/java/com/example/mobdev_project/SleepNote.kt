package com.example.mobdev_project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SleepNote : Fragment() {

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
        return inflater.inflate(R.layout.fragment_sleep_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sleepButton: Button = view.findViewById(R.id.AddSleepNoteButton)

        //Set actions for button
        sleepButton.setOnClickListener {
            //Retrieve information from user insert
            val note = view.findViewById<EditText>(R.id.text_note_id)
            val numericValue = view.findViewById<EditText>(R.id.numeric_value_id)

           //cast value to proper type
            val noteDb = note.text.toString()
            val numDb = numericValue.text.toString().toInt()


            // Create a JSON object
            val jsonObject = JSONObject()
            jsonObject.put("note", noteDb)

            // Send the JSON to the server
            sendDataToServer(jsonObject, noteDb, numDb)

            //when button is pressed it goes back to the display page
            val navController = view.findNavController()
            navController.navigate(R.id.action_sleepNote_to_first)




        }
    }

    private fun sendDataToServer(jsonObject: JSONObject, note: String, value: Int) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://10.0.2.2:5000/api/sentiment_analysis"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                // Handle successful response
                Log.d("SleepNote", "Data sent successfully: $response")
                try {
                    // Extract the sentiment value from the response
                    val sentiment = response.optDouble("sentiment")

                    // Get the date
                    val currentDate = Calendar.getInstance().time
                    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = sdf.format(currentDate)

                    // Save to database
                    saveDb(note, value, sentiment, formattedDate)
                } catch (e: Exception) {
                    //Handle error withing database communication
                    Log.e("SleepNote", "Error processing response: ${e.message}")
                    Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Handle error
                Log.e("SleepNote", "Error sending data: $error")
                Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest)

    }

    //Send all data to database
    private fun saveDb(note: String, value: Int, sentiment: Double, formattedDate: String) {
        val dbDAO = TrackingDatabase.getInstance(requireContext()).getDao()

        val newEvent = Tracking(
            trackingType = "Sleep",
            note = note,
            number = value,
            sentimentResult = sentiment,
            date = formattedDate
        )

        myCoroutineScope.launch {
            try {
                dbDAO.insert(newEvent)
                Log.d("SleepNote", "Data inserted successfully: $newEvent")
            } catch (e: Exception) {
                //Catch error
                Log.e("SleepNote", "Error inserting data into database: ${e.message}")
                Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()
            }
        }
    }


}