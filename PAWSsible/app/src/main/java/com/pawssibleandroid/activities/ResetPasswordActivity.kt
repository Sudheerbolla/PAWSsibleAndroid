package com.pawssibleandroid.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.databinding.ActivityResetPasswordBinding
import com.pawssibleandroid.interfaces.IParseListener
import com.pawssibleandroid.models.UserModel
import com.pawssibleandroid.utils.StaticUtils
import com.pawssibleandroid.utils.StaticUtils.isValidEmail
import com.pawssibleandroid.wsutils.WSCallBacksListener
import com.pawssibleandroid.wsutils.WSUtils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call

/**
 * Reset Password screen
 */
class ResetPasswordActivity : AppCompatActivity(), IParseListener<JsonElement?> {
    private lateinit var resetPasswordBinding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetPasswordBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
//        if we don't get any data(email) from previous screen, go back
        intent.extras.apply {
            if (this!!.containsKey("email")) {
                resetPasswordBinding.emailR.setText(this.getString("email"))
                resetPasswordBinding.emailR.isEnabled = false
            } else onBackPressed()
        }
    }

    /**
     * check validations and call reset password api
     */
    fun resetPassword(view: View) {
        if (!resetPasswordBinding.emailR.text.toString().trim().isValidEmail()) {
            resetPasswordBinding.emailR.setError("Enter valid email address")
        } else if (TextUtils.isEmpty(resetPasswordBinding.password.getText().toString())) {
            resetPasswordBinding.password.setError("Enter Password")
        } else if (TextUtils.isEmpty(resetPasswordBinding.cpassword.getText().toString())) {
            resetPasswordBinding.cpassword.setError("Confirm Password")
        } else if (resetPasswordBinding.password.getText()
                .toString() !== resetPasswordBinding.cpassword.getText().toString()
        ) {
            resetPasswordBinding.cpassword.setError("Password Does'nt Match!")
        } else {
            requestForResetPasswordWS()
        }
    }

    /**
     * API for reset password
     */
    private fun requestForResetPasswordWS() {
        val call: Call<JsonElement> =
            BaseApplication.instance?.wsClientListener!!.resetPassword(requestBodyObject)
        WSCallBacksListener().requestForJsonObject(
            this,
            WSUtils.REQ_FOR_RESET_PASSWORD,
            call,
            this
        )
    }

    /**
     * Helper to create request body as JSON
     */
    private val requestBodyObject: RequestBody
        get() {
            val jsonObjectReq = JSONObject()
            try {
                jsonObjectReq.put("email", resetPasswordBinding.emailR.text.toString().trim())
                jsonObjectReq.put("password", resetPasswordBinding.password.text.toString().trim())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return StaticUtils.getRequestBodyJson(jsonObjectReq)
        }

    /**
     * If API returns success, we will go into this callback method
     */
    override fun onSuccess(code: Int, response: JsonElement?) {
        when (code) {
            WSUtils.REQ_FOR_USER_REGISTRATION -> parseRegisterResponse(response as JsonObject)
            else -> {
            }
        }
    }

    /**
     * Registration response
     */
    private fun parseRegisterResponse(response: JsonObject) {
        try {
            if (response.has("message")) {
                StaticUtils.showToast(this, response["message"].asString)
            } else {
                val gson = Gson()
                val userModel =
                    gson.fromJson(response.get("user").asJsonObject, UserModel::class.java)

                if (userModel != null) {
                    StaticUtils.showToast(this, "Reset Password Successfully!")
                    val homeIntent = Intent(this, LoginActivity::class.java)
                    startActivity(homeIntent)
                    finishAffinity()
                } else StaticUtils.showToast(this, "User not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * If API returns error, we will go into this callback method
     */
    override fun onError(code: Int, error: String?) {
        StaticUtils.showToast(this, error)
    }

    /**
     * If there is no internet, we will go into this callback method
     */
    override fun onNoNetwork(code: Int) {
        StaticUtils.showToast(this, "Please check your internet connection")
    }

}