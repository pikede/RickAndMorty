package com.example.rickandmorty.views

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

fun View.hideView() {
    this.isVisible = false
}

fun View.showView() {
    this.isVisible = true
}

fun Any.logError(message: String?) {
    message?.let {
        Log.d(this::class.java.name, it)
    }
}

fun Fragment.showToast(message: String?) {
    Toast.makeText(
        requireContext(),
        message,
        Toast.LENGTH_SHORT
    ).show()
}