package com.example.mobdev_project

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

//Define the DAO interface with the operations we need
@Dao
interface TrackingDAO {

    @Query("SELECT * FROM Tracker")
    fun getAll(): Array<Tracking>

    @Insert
    fun insert(vararg t: Tracking)

    @Update
    fun update(t: Tracking)

    @Delete
    fun delete(t: Tracking)

    //To get only numeric values
    @Query("SELECT number FROM Tracker WHERE trackingType = :activity")
    fun getValues(activity: String): Array<Int>

    //To get only the text note
    @Query("SELECT note FROM Tracker WHERE trackingType = :activity")
    fun getNote(activity: String): Array<String>

    //To get All data based on a single activity
    @Query("SELECT * FROM Tracker WHERE trackingType = :activity")
    fun getList(activity: String): Array<Tracking>

}
