package com.pawssibleandroid.interfaces

import android.content.DialogInterface

/**
 * Interface to pass data between dialog and holder screen for reset password
 */
interface ITextListener {
    fun onClick(dialog: DialogInterface?, which: Int, text: String?)
}