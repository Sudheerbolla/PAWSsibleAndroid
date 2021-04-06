package com.pawssibleandroid.interfaces

import android.view.View

/**
 * Used to catch click events of recycler view adapter inside holding screen
 */
interface IClickListener {
    fun onClick(view: View?, position: Int)

    fun onLongClick(view: View?, position: Int)
}