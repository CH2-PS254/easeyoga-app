package com.dicoding.capstone_ch2ps254.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.dicoding.capstone_ch2ps254.HomeActivity
import com.dicoding.capstone_ch2ps254.R.string
import com.dicoding.capstone_ch2ps254.data.remote.ApiResponse
import com.dicoding.capstone_ch2ps254.data.remote.authpack.Body
import com.dicoding.capstone_ch2ps254.databinding.ActivityLoginBinding
import com.dicoding.capstone_ch2ps254.register.RegistrationActivity
import com.dicoding.capstone_ch2ps254.utils.Session
import com.dicoding.capstone_ch2ps254.utils.ValManager.KEY_ISLOGIN
import com.dicoding.capstone_ch2ps254.utils.ValManager.KEY_TOKEN
import com.dicoding.capstone_ch2ps254.utils.ValManager.KEY_USER_ID
import com.dicoding.capstone_ch2ps254.utils.ValManager.KEY_USER_NAME
import com.dicoding.capstone_ch2ps254.utils.extension.showOKDialog
import com.dicoding.capstone_ch2ps254.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private var _activityLoginBinding: ActivityLoginBinding? = null
    private val binding get() = _activityLoginBinding!!
    private lateinit var pref: Session

    companion object {
        fun begin(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_activityLoginBinding?.root)

        pref = Session(this)
        initAct()
    }

    private fun initAct() {
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextName.text.toString()
            val password = binding.editTextPassword.text.toString()

            when {
                username.isBlank() -> {
                    binding.editTextName.requestFocus()
                    binding.editTextName.error = getString(string.email_empty)
                }
                password.isBlank() -> {
                    binding.editTextPassword.requestFocus()
                    binding.editTextPassword.error = getString(string.pass_empty)
                }
                else -> {
                    val request = Body(username, password)
                    loginUser(request, username)
                }
            }
        }
        binding.textViewSignUpBold.setOnClickListener {
            RegistrationActivity.begin(this)
        }
    }

    private fun loginUser(loginBody: Body, username: String) {
        loginViewModel.userLogin(loginBody).observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    isLoading(true)
                }
                is ApiResponse.Success -> {
                    try {
                        isLoading(false)
                        val userRespon = response.data.data
                        pref.apply {
//                            setStringPreference(KEY_USER_ID, userRespon.id)
                            setStringPreference(KEY_TOKEN, userRespon.token)
                            setStringPreference(KEY_USER_NAME, username)
//                            setStringPreference(KEY_USER_ID, id)
//                            setStringPreference(KEY_USER_NAME, username)
//                            setStringPreference(KEY_EMAIL, email)
                            setBooleanPreference(KEY_ISLOGIN, true)
                        }
                    } finally {
                        HomeActivity.begin(this)
                    }
                }
                is ApiResponse.Error -> {
                    isLoading(false)
                    showOKDialog(getString(string.dialog_error), getString(string.userpass_error))
                }
                else -> {
                    showToast(getString(string.statement_error))
                }
            }
        }
    }

    private fun isLoading(isLoading: Boolean) {
        binding.editTextName.isClickable = !isLoading
        binding.editTextName.isEnabled = !isLoading
        binding.editTextPassword.isClickable = !isLoading
        binding.editTextPassword.isEnabled = !isLoading
        binding.buttonLogin.isClickable = !isLoading
    }
}