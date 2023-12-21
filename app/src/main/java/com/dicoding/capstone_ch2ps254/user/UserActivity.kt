package com.dicoding.capstone_ch2ps254.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.dicoding.capstone_ch2ps254.HomeActivity
import com.dicoding.capstone_ch2ps254.R
import com.dicoding.capstone_ch2ps254.databinding.ActivityUserBinding
import com.dicoding.capstone_ch2ps254.login.LoginActivity
import com.dicoding.capstone_ch2ps254.utils.Session

@Suppress("DEPRECATION")
class UserActivity : AppCompatActivity() {

    private var _activityUserBinding: ActivityUserBinding? = null
    private val binding get() = _activityUserBinding!!
    private lateinit var pref: Session

    companion object {
        fun begin(context: Context) {
            val intent = Intent(context, UserActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityUserBinding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(_activityUserBinding?.root)
        pref = Session(this)
        initUI()
        initAct()

        binding.btnback.setOnClickListener {
            navigateToHomeActivity()
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun initUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.profile)
        binding.tvName.text = pref.getUserName
    }

    private fun initAct() {
        binding.btnLogout.setOnClickListener {
            logoutDialog()
        }
    }

    private fun logoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.logout_confirm))
            ?.setPositiveButton(getString(R.string.logout_yes)) { _, _ ->
                pref.clearPreferences()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            ?.setNegativeButton(getString(R.string.logout_no), null)
        val alert = alertDialog.create()
        alert.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}