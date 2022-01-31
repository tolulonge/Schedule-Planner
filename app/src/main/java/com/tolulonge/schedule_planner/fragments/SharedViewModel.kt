package com.tolulonge.schedule_planner.fragments

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tolulonge.schedule_planner.R
import com.tolulonge.schedule_planner.data.model.Priority
import com.tolulonge.schedule_planner.data.model.ToDoData

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkIfDatabaseEmpty(toDoData: List<ToDoData>){
        emptyDatabase.value = toDoData.isEmpty()
    }



    val listener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener{
        override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when(position){
                0 -> (p0?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.red))
                1 -> (p0?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.yellow))
                2 -> (p0?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.green))

            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

    }

     fun verifyDataFromUser(title: String, desc: String) : Boolean{
        return if (title.isEmpty() || desc.isEmpty()) false else !(title.isEmpty() || desc.isEmpty())
    }

     fun parsePriority(priority: String) : Priority {
        return when(priority){
            "High Priority" -> Priority.HIGH
            "Medium Priority" -> Priority.MEDIUM
            "Low Priority" -> Priority.LOW
            else -> Priority.LOW
        }
    }

}