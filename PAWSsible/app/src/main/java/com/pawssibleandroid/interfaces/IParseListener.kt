package com.pawssibleandroid.interfaces

interface IParseListener<T> {
    fun onSuccess(code: Int, response: T)
    fun onError(code: Int, error: String?)
    fun onNoNetwork(code: Int)
}