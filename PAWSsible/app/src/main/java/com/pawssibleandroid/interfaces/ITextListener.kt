package com.pawssibleandroid.interfaces

import android.content.DialogInterface

interface ITextListener {
    fun onClick(dialog: DialogInterface?, which: Int, text: String?)
}