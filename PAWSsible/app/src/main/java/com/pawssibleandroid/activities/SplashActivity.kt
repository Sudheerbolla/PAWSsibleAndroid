package com.pawssibleandroid.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.utils.PawssibleStorage

/**
 * This is launcher activity, where app launches initially.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        showing splash for 2.5 seconds and going to login or home screen based on user id availability
        Handler(Looper.getMainLooper()).postDelayed({
            val homeIntent: Intent
            if (!TextUtils.isEmpty(
                    BaseApplication.pawssibleStorage?.getValue(PawssibleStorage.SP_USER_EMAIL, "")
                )
            ) {
                homeIntent = Intent(this, MainActivity::class.java)
            } else {
                homeIntent = Intent(this, LandingActivity::class.java)
            }
            startActivity(homeIntent)
            finishAffinity()
        }, 2500)
    }

}