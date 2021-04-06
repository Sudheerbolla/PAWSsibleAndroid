package com.pawssibleandroid.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pawssibleandroid.R
import com.pawssibleandroid.interfaces.ITextListener
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

/**
 * Class where all required methods used globally are there
 */
object StaticUtils {
    private const val DISPLAY_DATE_TIME_FORMAT = "dd-MM-yyyy hh:mm a"
    var SCREEN_HEIGHT = 0
    var SCREEN_WIDTH = 0
    private var clipboard: ClipboardManager? = null
    const val CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
    const val CONTENT_TYPE_TEXT_PLAIN = "text/plain"

    /**
     * Checks if there is internet connection
     */
    fun checkInternetConnection(context: Context): Boolean {
        val _activeNetwork =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return _activeNetwork != null && _activeNetwork.isConnectedOrConnecting
    }

    /**
     * Shows toast message
     */
    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows toast message
     */
    fun showToast(context: Context?, message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Hides keyboard
     */
    fun hideSoftKeyboard(act: Activity) {
        try {
            if (act.currentFocus != null) {
                val inputMethodManager =
                    act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(act.currentFocus!!.windowToken, 0)
            }
        } catch (ignored: Exception) {
        }
    }

    fun getRequestBody(jsonObject: JSONObject): RequestBody {
        val MEDIA_TYPE_TEXT: MediaType = CONTENT_TYPE_TEXT_PLAIN.toMediaTypeOrNull()!!
        return RequestBody.create(MEDIA_TYPE_TEXT, jsonObject.toString())
    }

    fun getRequestBodyJson(jsonObject: JSONObject): RequestBody {
        val MEDIA_TYPE_TEXT: MediaType = CONTENT_TYPE_JSON.toMediaTypeOrNull()!!
        return RequestBody.create(MEDIA_TYPE_TEXT, jsonObject.toString())
    }

    fun copyTextToClipBoard(context: Context, textToCopy: String?) {
        try {
            if (clipboard == null) clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("selectedFilePath", textToCopy)
            clipboard!!.setPrimaryClip(clip)
            //            showToast(context, context.getString(R.string.copied_to_clipboard));
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun forgotPWDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        iTextListener: ITextListener
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Forgot Passsword")
        val dialogLayout = layoutInflater.inflate(R.layout.layout_forgot_password, null)
        val editText: EditText = dialogLayout.findViewById(R.id.edtEmail)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Send Verification Link") { dialogInterface, i ->

            if (editText.text.toString().trim().isValidEmail()) {
                showToast(
                    context,
                    "Verification link has been sent to your email Address. please reset password using the link."
                )
                iTextListener.onClick(dialogInterface, i, editText.text.toString().trim())
                dialogInterface.dismiss()
            } else {
                showToast(
                    context,
                    "Please enter valid Email Address."
                )
                editText.setText("")
            }
        }
        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun checkWifiOnAndConnected(context: Context): Boolean {
        val wifiMgr = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (wifiMgr.isWifiEnabled) {
            val wifiInfo = wifiMgr.connectionInfo
            wifiInfo.networkId != -1
        } else {
            false
        }
    }

    fun isWifiOn(context: Context): Boolean {
        val connMgr =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connMgr.activeNetwork ?: return false
            val capabilities = connMgr.getNetworkCapabilities(network)
            capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val networkInfo = connMgr.activeNetworkInfo
            networkInfo!!.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI
        }
    }

    fun isPrimaryTextAvailableInClipboard(context: Context): Boolean {
        if (clipboard == null) clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboard!!.hasPrimaryClip() && clipboard!!.primaryClip != null && clipboard!!.primaryClip!!
            .getItemAt(0) != null &&
                !TextUtils.isEmpty(clipboard!!.primaryClip!!.getItemAt(0).text)
    }

    fun pasteTextToClipBoard(context: Context, textToCopy: String?) {
        try {
            if (clipboard == null) clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("selectedFilePath", textToCopy)
            clipboard!!.setPrimaryClip(clip)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val isExternalStorageReadOnly: Boolean
        get() = Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()
    val isExternalStorageAvailable: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}