package com.tolulonge.schedule_planner.data

import android.content.Context
import androidx.room.*
import com.tolulonge.schedule_planner.data.model.ToDoData

@Database(entities = [ToDoData::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun toDoDao() : ToDoDao


    companion object{

        // Volatile: Writes to this field are immediately made visible to other threads
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getDatabase(context: Context) : ToDoDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }

            /*
             When a thread calls synchronized, it acquires the lock of that synchronized block. Other
             threads don't have permission to call that same synchronized block as long as previous
             thread which had acquired the lock does not release the lock
             */
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}