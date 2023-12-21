package com.dicoding.capstone_ch2ps254.splashscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.dicoding.capstone_ch2ps254.HomeActivity
import com.dicoding.capstone_ch2ps254.R
import com.dicoding.capstone_ch2ps254.utils.Session
import com.dicoding.capstone_ch2ps254.utils.UiValue

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var pref: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        pref = Session(this)
        val isLogin = pref.isLogin
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                isLogin -> {
                    HomeActivity.begin(this)
                    finish()
                }
                else -> {
                    HomeActivity.begin(this)
                    finish()
                }
            }
        }, UiValue.LOADING)
    }
}