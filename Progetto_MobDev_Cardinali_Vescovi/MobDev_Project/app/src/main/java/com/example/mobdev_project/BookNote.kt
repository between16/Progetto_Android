package com.example.mobdev_project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
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

class BookNote : Fragment() {
    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        val pagesButton: Button = view.findViewById(R.id.AddPagesNoteButton)

        //defining action for the button
        pagesButton.setOnClickListener {
            //retrieving data from the page
            val note = view.findViewById<EditText>(R.id.text_note_id)
            val numericValue = view.findViewById<EditText>(R.id.numeric_value_id)

            //casting data into proper type
            val noteDb = note.text.toString()
            val numDb = numericValue.text.toString().toInt()


            // Create a JSON object
            val jsonObject = JSONObject()
            jsonObject.put("note", noteDb)

            // Send the JSON to the server
            sendDataToServer(jsonObject, noteDb, numDb)

            //when button is pressed it goes back to the main page
            val navController = view.findNavController()
            navController.navigate(R.id.action_bookNote_to_first)

        }

    }

    //Send the collected data to the server
    private fun sendDataToServer(jsonObject: JSONObject, note: String, value: Int) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://10.0.2.2:5000/api/sentiment_analysis"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                // Handle successful response
                Log.d("BookNote", "Data sent successfully: $response")
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
                    //Handle errors
                    Log.e("BookNote", "Error processing response: ${e.message}")
                    // Show a message to the user
                    Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()

                }
            },
            { error ->
                // Handle error in response
                Log.e("BookNote", "Error sending data: $error")
                Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest)

    }

    //Save values into Database
    private fun saveDb(note: String, value: Int, sentiment: Double, formattedDate: String) {
        val dbDAO = TrackingDatabase.getInstance(requireContext()).getDao()

        val newEvent = Tracking(
            trackingType = "Book",
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
                //Catch insertion errors
                Log.e("SleepNote", "Error inserting data into database: ${e.message}")
                Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()

            }
        }
    }
}