package com.example.mobdev_project

import android.hardware.Sensor
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var shakeCount = 0
    private val handler = Handler()
    private val runnable = Runnable { checkAndResetSteps() }

    //So that the text can be initialized later on
    private lateinit var curStepsTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //associated the variable and makes it invisible
        curStepsTextView = findViewById<TextView>(R.id.curSteps)
        curStepsTextView.visibility = View.INVISIBLE

        // Initialize the sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Start the handler to check time every minute
        handler.postDelayed(runnable, 60000) // 60000 milliseconds = 1 minute

    }

    //to make visible the currentSteps variable
    fun show() {
        curStepsTextView.visibility = View.VISIBLE
    }
    //to hide currentSteps
    fun hide() {
        curStepsTextView.visibility = View.INVISIBLE
    }

    //Keep track of the sensor
    override fun onResume() {
        super.onResume()
        running = true
        //Associate the accelerometer sensor
        val accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        //If not found shows an error
        if (accelerometerSensor == null) {
            Toast.makeText(this, "No accelerometer detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
        }

    }

    //Method that handle whenever sensor's value changes
    override fun onSensorChanged(event: SensorEvent?) {
        if (running && event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Call the step detection method for the accelerometer
            var currentSteps = detectSteps(event.values)
            totalSteps += currentSteps
            var tStep = findViewById<TextView>(R.id.curSteps)
            //Update the value in real time on screen
            tStep.text = "Current steps: $totalSteps"


        }
    }

    //Necessary but not used
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    //Check if it midnight and reset steps counter
    private fun checkAndResetSteps() {
        //Get current time
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Check if it's midnight
        if (hour == 0 && minute == 0) {
            resetSteps() // Reset the steps
        }

        //The handler checks every minute
        handler.postDelayed(runnable, 60000)
    }

    //Insert value into database and reset the totalSteps
    fun resetSteps() {
        // Save the current step data to the database
        val dbDAO = TrackingDatabase.getInstance(this).getDao() // Use 'this' for context

        //Get date
        val currentDate = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = sdf.format(currentDate)

        // Create a new Tracking object
        val newEvent = Tracking(
            trackingType = "Steps",
            note = "",
            number = totalSteps.toInt(),
            sentimentResult = null,
            date = formattedDate
        )

        // Launch a coroutine to insert the data into the database
        myCoroutineScope.launch {
            dbDAO.insert(newEvent)
        }

        // Reset the step count
        totalSteps = 0f
    }

    //Interprets accelerometer values to understand if a step was made
    private fun detectSteps(values: FloatArray): Int {
        val x = values[0]
        val y = values[1]
        val z = values[2]

        //Math function to calculate the Magnitude of the movement
        val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        // Define a threshold for detecting steps
        val threshold = 10f

        if (magnitude > threshold) {
            shakeCount++
            if (shakeCount > 2) { // Steps is calculated as two shakes
                shakeCount = 0
                Log.d("StepDetector", "Step detected!")
                return 1 // Count a step
            }
        } else {
            shakeCount = 0 // Reset shake count if movement is below threshold
        }

        return 0 // No step detected

    }

    //Releases the handler
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable) // Stop the handler when the activity is destroyed
    }

}