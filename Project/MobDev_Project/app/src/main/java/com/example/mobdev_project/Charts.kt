package com.example.mobdev_project

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class Charts : Fragment() {

    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)

    //wait to load the image
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_charts, container, false)
        imageView = view.findViewById(R.id.chartImageView) // Find the ImageView in the layout
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myCoroutineScope.launch {
            try {
                // Retrieve the data from the database
                val values = getData("Sleep")

                // Create a JSON object
                val jsonObject = JSONObject()
                jsonObject.put("numeric_value", JSONArray(values)) // Use the retrieved values

                // Send the JSON to the server asynchronously
                sendDataToServerAsync(jsonObject)
            } catch (e: Exception) {
                // Handle errors
                Log.e("SleepNote", "Error: ${e.message}")

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to load or send data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Send values to the server to get Chart back
    private fun sendDataToServerAsync(jsonObject: JSONObject) {
        val url = "http://10.0.2.2:5000/api/receive_charts"// Correct// Use this for Android emulator

        //Unpacks the data that is received
        val jsonObjectRequest = object : Request<ByteArray>(Request.Method.POST, url, null) {
            //JSON data is converted to an Array
            override fun getBody(): ByteArray {
                return jsonObject.toString().toByteArray()
            }

            //Specifies the content type to the server
            override fun getBodyContentType(): String {
                return "application/json"
            }
            //Processes the server response
            override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray> {
                return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
            }
            //A log message is written if data has successfully arrived from the server
            override fun deliverResponse(response: ByteArray) {
                // Handle successful response
                Log.d("Sleep", "Data sent successfully")

                //Load image
                val imageBitmap = BitmapFactory.decodeByteArray(response, 0, response.size)
                imageView.setImageBitmap(imageBitmap) // Display the image in ImageView
            }
        }

        // Add the request to the Volley request queue
        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
    }

    //This function is used to interact with the DAO
    private suspend fun getData(actType: String): Array<Int> {
        val dbDAO = TrackingDatabase.getInstance(requireContext()).getDao()

        return withContext(Dispatchers.IO) {
            try {
                // Attempt to retrieve the values from the database
                dbDAO.getValues(actType)
            } catch (e: Exception) {
                //Catch Errors and send massage to the user
                Log.e("Sleep", "Error retrieving data from database: ${e.message}")
                Toast.makeText(context, "Error detected", Toast.LENGTH_SHORT).show()
                // Return an empty array to indicate failure
                emptyArray()
            }
        }
    }

}