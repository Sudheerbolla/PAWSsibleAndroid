package com.pawssibleandroid.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.interfaces.IParseListener
import com.pawssibleandroid.interfaces.ITextListener
import com.pawssibleandroid.models.UserModel
import com.pawssibleandroid.utils.PawssibleStorage
import com.pawssibleandroid.utils.StaticUtils
import com.pawssibleandroid.utils.StaticUtils.isValidEmail
import com.pawssibleandroid.wsutils.WSCallBacksListener
import com.pawssibleandroid.wsutils.WSUtils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call


class LoginActivity : AppCompatActivity(), IParseListener<JsonElement?> {
    var b1: Button? = null
    var email: EditText? = null
    var password: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        b1 = findViewById(R.id.enter)
        email = findViewById(R.id.emailid)
        password = findViewById(R.id.password)
        email?.setText("customer1@gmail.com")
        password?.setText("111111")
    }

    fun forgotPasswordPage(view: View?) {
        StaticUtils.forgotPWDialog(this, layoutInflater, object : ITextListener {
            override fun onClick(dialog: DialogInterface?, which: Int, text: String?) {
                navigateToResetPasswordScreen(text)
            }
        })
    }

    private fun navigateToResetPasswordScreen(text: String?) {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        intent.putExtra("email", text)
        startActivity(intent)
    }

    fun homePage(view: View?) {
        if (TextUtils.isEmpty(email!!.text.toString())) {
            email!!.error = "Enter Email"
        } else if (!email!!.text.toString().isValidEmail()) {
            email!!.error = "Enter valid Email"
        } else if (TextUtils.isEmpty(password!!.text.toString())) {
            password!!.error = "Enter Password"
        } else {
            requestForLoginWS()
        }
    }

    private fun requestForLoginWS() {
        val call: Call<JsonElement> =
            BaseApplication.instance?.wsClientListener!!.performLogin(requestBodyObject)
        WSCallBacksListener().requestForJsonObject(this, WSUtils.REQ_FOR_LOGIN, call, this)
    }

    private val requestBodyObject: RequestBody
        private get() {
            val jsonObjectReq = JSONObject()
            try {
                jsonObjectReq.put("email", email!!.text.toString().trim { it <= ' ' })
                jsonObjectReq.put("password", password!!.text.toString().trim { it <= ' ' })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return StaticUtils.getRequestBodyJson(jsonObjectReq)
        }

    fun registerPage(view: View?) {
        val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSuccess(code: Int, response: JsonElement?) {
        when (code) {
            WSUtils.REQ_FOR_LOGIN -> parseLoginResponse(response as JsonObject)
            else -> {
            }
        }
    }

    private fun parseLoginResponse(response: JsonObject) {
        try {
            if (response.has("message")) {
                StaticUtils.showToast(this, response["message"].asString)
            } else {
                val gson = Gson()
                val userModel =
                    gson.fromJson(response.get("user").asJsonObject, UserModel::class.java)

                if (userModel != null) {
                    BaseApplication.pawssibleStorage?.setValue(
                        PawssibleStorage.SP_USER_EMAIL,
                        userModel.email
                    )
                    BaseApplication.pawssibleStorage?.setValue(
                        PawssibleStorage.SP_USER_PASSWORD,
                        password!!.text.toString().trim { it <= ' ' })
                    BaseApplication.pawssibleStorage?.setValue(
                        PawssibleStorage.SP_USER_TYPE,
                        userModel.userType
                    )
                    BaseApplication.pawssibleStorage?.setValue(
                        PawssibleStorage.SP_USER_PHONE,
                        userModel.phone
                    )
                    BaseApplication.pawssibleStorage?.setValue(
                        PawssibleStorage.SP_USER_ADDRESS,
                        userModel.address
                    )
                    BaseApplication.pawssibleStorage?.setValue(
                        PawssibleStorage.SP_USER_NAME,
                        userModel.name
                    )
                    BaseApplication.pawssibleStorage?.setValue(
                        PawssibleStorage.SP_USER_ID,
                        userModel.userId
                    )
                    BaseApplication.userType =
                        BaseApplication.pawssibleStorage?.getValue(
                            PawssibleStorage.SP_USER_TYPE,
                            "C"
                        )
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else StaticUtils.showToast(this, "User not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onError(code: Int, error: String?) {
        StaticUtils.showToast(this, error)
    }

    override fun onNoNetwork(code: Int) {
        StaticUtils.showToast(this, "Please check your internet connection")
    }

}