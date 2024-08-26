package com.example.mobdev_project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*
Handles the Recycle view
 */
class MyAdapter(val data: Array<Tracking>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val r_date = row.findViewById<TextView>(R.id.date)
        val r_number = row.findViewById<TextView>(R.id.hours)
        val notes = row.findViewById<TextView>(R.id.note)
        val nlp = row.findViewById<TextView>(R.id.nlp)
        val numNote = row.findViewById<TextView>(R.id.count)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_view,
            parent, false)
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Get the current Tracking item
        val trackingItem = data[position]

        // Set the values to the TextViews
        holder.r_date.text = "Date: ${trackingItem.date}"
        holder.r_number.text = "Value: ${trackingItem.number.toString()}"
        holder.notes.text = "Note: ${trackingItem.note}"

        var rawValue = trackingItem.sentimentResult

        //Associate the sentient values with a short comment
        val sentiment = when {
            rawValue == null -> ""
            rawValue < -0.6 -> "VERY BAD"
            rawValue >= -0.6 && rawValue < -0.2 -> "BAD"
            rawValue >= -0.2 && rawValue < 0.2 -> "NORMAL"
            rawValue >= 0.2 && rawValue < 0.6 -> "GOOD"
            rawValue >= 0.6 -> "VERY GOOD"
            else -> ""
        }

        holder.nlp.text = "Sentiment result: $sentiment"
        holder.numNote.text = "#${position + 1}"



    }
    override fun getItemCount(): Int = data.size
}