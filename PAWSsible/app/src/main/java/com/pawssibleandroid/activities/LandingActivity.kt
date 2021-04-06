package com.pawssibleandroid.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pawssibleandroid.R
import com.pawssibleandroid.interfaces.ITextListener
import com.pawssibleandroid.utils.StaticUtils

/**
 * This is landing screen if user not logged in previously or after logout
 */
class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
    }

    /**
     * Navigate to Login screen
     */
    fun loginPage(view: View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * Navigate to Registration screen
     */
    fun reg(view: View?) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    /**
     * Shows forgot password dialog and navigates to reset password screen
     */
    fun forgotPW(view: View) {
        StaticUtils.forgotPWDialog(this, layoutInflater, object : ITextListener {
            override fun onClick(dialog: DialogInterface?, which: Int, text: String?) {
                navigateToResetPasswordScreen(text)
            }
        })
    }

    /**
     * Navigate to Reset Password screen
     */
    private fun navigateToResetPasswordScreen(text: String?) {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        intent.putExtra("email", text)
        startActivity(intent)
    }

}