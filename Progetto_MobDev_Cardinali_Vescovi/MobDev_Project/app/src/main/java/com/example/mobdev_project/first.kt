package com.example.mobdev_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController

class first : Fragment() {


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
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)
        //initiate the buttons and the navControllers to move to the proper page when button is pressed
        val buttonSteps: TextView = view.findViewById(R.id.Steps_id)
        val navControllerSteps = view.findNavController()
        buttonSteps.setOnClickListener { navControllerSteps.navigate(R.id.action_first_to_second) }


        val buttonSleep: TextView = view.findViewById(R.id.Sleep_id)
        val navControllerSleep = view.findNavController()
        buttonSleep.setOnClickListener { navControllerSleep.navigate(R.id.action_first_to_third) }


        val buttonBook: TextView = view.findViewById(R.id.Book_id)
        val navControllerBook = view.findNavController()
        buttonBook.setOnClickListener { navControllerBook.navigate(R.id.action_first_to_fourth) }


    }

    //Remove the totalSteps TextView when coming back from Second
    override fun onResume() {
       super.onResume()
        (requireActivity() as MainActivity).hide()
    }

}