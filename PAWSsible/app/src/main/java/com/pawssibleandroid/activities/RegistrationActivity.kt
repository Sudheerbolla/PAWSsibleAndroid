package com.pawssibleandroid.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.databinding.ActivityRegistrationBinding
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
 * Activity to create new user
 */
class RegistrationActivity : AppCompatActivity(), IParseListener<JsonElement?> {

    lateinit var dataBinding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        initComponents()
    }

    /**
     * Initialises the components
     */
    private fun initComponents() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.user_type_array, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dataBinding.spinner.setAdapter(adapter)
    }

    /**
     * Check validations for data and create user
     */
    fun verify(view: View?) {
        if (TextUtils.isEmpty(dataBinding.edtName.getText().toString())) {
            dataBinding.edtName.setError("Enter Full Name")
        } else if (TextUtils.isEmpty(dataBinding.emailR.getText().toString())) {
            dataBinding.emailR.setError("Enter email address")
        } else if (!dataBinding.emailR.text.toString().trim().isValidEmail()) {
            dataBinding.emailR.setError("Enter valid email address")
        } else if (TextUtils.isEmpty(dataBinding.phone.getText().toString())) {
            dataBinding.phone.setError("Enter phone")
        } else if (TextUtils.isEmpty(dataBinding.address.getText().toString())) {
            dataBinding.address.setError("Enter address")
        } else if (TextUtils.isEmpty(dataBinding.spinner.selectedItem.toString().trim())) {
            StaticUtils.showToast(this, "please select user type")
        } else if (TextUtils.isEmpty(dataBinding.password.getText().toString())) {
            dataBinding.password.setError("Enter Password")
        } else if (TextUtils.isEmpty(dataBinding.cpassword.getText().toString())) {
            dataBinding.cpassword.setError("Confirm Password")
        } else if (dataBinding.password.getText().toString() !== dataBinding.cpassword.getText()
                .toString()
        ) {
            dataBinding.cpassword.setError("Password Does'nt Match!")
        } else {
            requestForRegistrationWS()
        }
    }

    /**
     * API for creating user
     */
    private fun requestForRegistrationWS() {
        val call: Call<JsonElement> =
            BaseApplication.instance?.wsClientListener!!.performRegistration(requestBodyObject)
        WSCallBacksListener().requestForJsonObject(
            this,
            WSUtils.REQ_FOR_USER_REGISTRATION,
            call,
            this
        )
    }

    /**
     * forming jsonobject for creating user api
     */
    private val requestBodyObject: RequestBody
        get() {
            val jsonObjectReq = JSONObject()
            try {
                jsonObjectReq.put("name", dataBinding.edtName.text.toString().trim())
                jsonObjectReq.put("email", dataBinding.emailR.text.toString().trim())
                jsonObjectReq.put(
                    "userType",
                    if (dataBinding.spinner.selectedItem.toString().trim()
                            .equals("owner", true)
                    ) "O" else "C"
                )
                jsonObjectReq.put("phone", dataBinding.phone.text.toString().trim())
                jsonObjectReq.put("address", dataBinding.address.text.toString().trim())
                jsonObjectReq.put("password", dataBinding.password.text.toString().trim())
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
     * Registration ws data parsing
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
                    StaticUtils.showToast(this, "Registered Successfully!")
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