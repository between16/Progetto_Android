package com.example.mobdev_project

import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName= "Tracker")
data class Tracking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
            @ColumnInfo(name = "trackingType") val trackingType: String,
            @ColumnInfo(name = "note") val note: String?,
            @ColumnInfo(name = "number") val number: Int,
            @ColumnInfo(name = "sentimentResult") val sentimentResult: Double?,
            @ColumnInfo(name = "date") val date: String
    )