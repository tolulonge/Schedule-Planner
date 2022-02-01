package com.tolulonge.schedule_planner.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/*
  Binding Adapters are essentially just a regular function annotated with a binding adapter, used as
  a custom view action in layout file
 */

/*
  When a thread calls synchronized, it acquires the lock of that synchronized block. Other
  threads don't have permission to call that same synchronized block as long as previous
  thread which had acquired the lock does not release the lock
*/

// Volatile: Writes to this field are immediately made visible to other threads

fun hideKeyboard(activity: Activity){
    val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    val currentFocusedView = activity.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
    observe(lifecycleOwner, object : Observer<T>{
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}