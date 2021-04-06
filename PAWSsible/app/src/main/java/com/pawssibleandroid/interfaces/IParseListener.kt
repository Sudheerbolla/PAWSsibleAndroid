package com.pawssibleandroid.interfaces

/**
 * Used to deal with retrofi api call's callbacks
 */
interface IParseListener<T> {
    /**
     * If response is successfully captured
     */
    fun onSuccess(code: Int, response: T)

    /**
     * If there is any error
     */
    fun onError(code: Int, error: String?)

    /**
     * Check before calling, if network is available
     */
    fun onNoNetwork(code: Int)
}