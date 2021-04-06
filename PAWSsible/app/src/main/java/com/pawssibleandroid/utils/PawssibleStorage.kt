package com.pawssibleandroid.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.multidex.BuildConfig

/**
 * This is local storage or shared preference helper class for easy access to store data
 */
class PawssibleStorage {

    private var appStorage: PawssibleStorage? = null
    protected var mContext: Context? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mSharedPreferencesEditor: SharedPreferences.Editor? = null

    companion object {
        //    O (owner) and C(customer)
        val SP_USER_TYPE = "SP_USER_TYPE"
        val SP_USER_ID = "SP_USER_ID"
        val SP_USER_NAME = "SP_USER_NAME"
        val SP_USER_EMAIL = "SP_USER_EMAIL"
        val SP_USER_PASSWORD = "SP_USER_PASSWORD"
        val SP_USER_PHONE = "SP_USER_PHONE"
        val SP_USER_ADDRESS = "SP_USER_ADDRESS"
    }

    private fun PawssibleStorage(context: Context): PawssibleStorage {
        mContext = context
        mSharedPreferences =
            context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        mSharedPreferencesEditor = mSharedPreferences?.edit()
        return this
    }

    /**
     * Creates single instance of SharedPreferenceUtils
     *
     * @param context context of Activity or Service
     * @return Returns instance of SharedPreferenceUtils
     */
    @Synchronized
    fun getInstance(context: Context): PawssibleStorage? {
        if (appStorage == null) {
            appStorage = PawssibleStorage(context)
        }
        return appStorage
    }

    /**
     * Stores String value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: String?) {
        mSharedPreferencesEditor!!.putString(key, value)
        mSharedPreferencesEditor!!.commit()
    }

    /**
     * Stores int value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: Int) {
        mSharedPreferencesEditor!!.putInt(key, value)
        mSharedPreferencesEditor!!.commit()
    }

    /**
     * Stores Double value in String format in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: Double) {
        setValue(key, java.lang.Double.toString(value))
    }

    /**
     * Stores long value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: Long) {
        mSharedPreferencesEditor!!.putLong(key, value)
        mSharedPreferencesEditor!!.commit()
    }

    /**
     * Stores boolean value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: Boolean) {
        mSharedPreferencesEditor!!.putBoolean(key, value)
        mSharedPreferencesEditor!!.commit()
    }

    /**
     * Retrieves String value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    fun getValue(key: String?, defaultValue: String?): String? {
        return mSharedPreferences!!.getString(key, defaultValue)
    }

    /**
     * Retrieves int value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    fun getValue(key: String?, defaultValue: Int): Int {
        return mSharedPreferences!!.getInt(key, defaultValue)
    }

    /**
     * Retrieves long value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    fun getValue(key: String?, defaultValue: Long): Long {
        return mSharedPreferences!!.getLong(key, defaultValue)
    }

    /**
     * Retrieves boolean value from preference
     *
     * @param keyFlag      key of preference
     * @param defaultValue default value if no key found
     */
    fun getValue(keyFlag: String?, defaultValue: Boolean): Boolean {
        return mSharedPreferences!!.getBoolean(keyFlag, defaultValue)
    }

    /**
     * Removes key from preference
     *
     * @param key key of preference that is to be deleted
     */
    fun removeKey(key: String?) {
        if (mSharedPreferencesEditor != null) {
            mSharedPreferencesEditor!!.remove(key)
            mSharedPreferencesEditor!!.commit()
        }
    }

    /**
     * Clears all the preferences stored
     */
    fun clear() {
        mSharedPreferencesEditor!!.clear().commit()
    }

}