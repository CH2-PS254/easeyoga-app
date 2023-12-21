package com.dicoding.capstone_ch2ps254.register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import com.dicoding.capstone_ch2ps254.R
import com.dicoding.capstone_ch2ps254.data.remote.ApiResponse
import com.dicoding.capstone_ch2ps254.data.remote.authpack.AuthBody
import com.dicoding.capstone_ch2ps254.databinding.ActivityRegistrationBinding
import com.dicoding.capstone_ch2ps254.login.LoginActivity
import com.dicoding.capstone_ch2ps254.utils.UiValue
import com.dicoding.capstone_ch2ps254.utils.extension.isEmailValid
import com.dicoding.capstone_ch2ps254.utils.extension.showOKDialog
import com.dicoding.capstone_ch2ps254.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private val registViewModel: RegistrationViewModel by viewModels()
    private var _activityRegistBinding: ActivityRegistrationBinding? = null
    private val binding get() = _activityRegistBinding!!

    companion object {
        fun begin(context: Context) {
            val intent = Intent(context, RegistrationActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityRegistBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(_activityRegistBinding?.root)

        initAct()
    }

    private fun initAct() {
        binding.buttonSignUp.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            Handler(Looper.getMainLooper()).postDelayed({
                when {
                    name.isBlank() -> binding.editTextName.error = getString(R.string.name_empty)
                    email.isBlank() -> binding.editTextEmail.error = getString(R.string.email_empty)
                    !email.isEmailValid() -> binding.editTextEmail.error = getString(R.string.email_error)
                    password.isBlank() -> binding.editTextPassword.error = getString(R.string.pass_empty)
                    else -> {
                        val request = AuthBody(
                            name, email, password
                        )
                        registUser(request)
                    }
                }
            }, UiValue.DELAYED)
        }
        binding.textViewSignInBold.setOnClickListener {
            LoginActivity.begin(this)
        }
    }

    private fun registUser(newUser: AuthBody) {
        registViewModel.registUser(newUser).observe(this) { response ->
            when(response) {
                is ApiResponse.Loading -> {
                    showLoading(true)
                }
                is ApiResponse.Success -> {
                    try {
                        showLoading(false)
                        showToast(getString(R.string.regist_succsess))
                        Timber.d("RegistrationActivity", "Registration success.")

                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        Timber.d("RegistrationActivity", "Starting LoginActivity.")
                        startActivity(intent)
                        Timber.d("RegistrationActivity", "Finishing RegistrationActivity.")
                        finish()
                    } finally {
                        Timber.d("RegistrationActivity", "Registration success.")
                    }
                }



                is ApiResponse.Error -> {
                    showLoading(false)
                    showOKDialog(getString(R.string.dialog_error), response.errorMessage)
                }
                else -> {
                    showToast(getString(R.string.statement_error))
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.editTextName.isClickable = !isLoading
        binding.editTextName.isEnabled = !isLoading
        binding.editTextEmail.isClickable = !isLoading
        binding.editTextEmail.isEnabled = !isLoading
        binding.editTextPassword.isClickable = !isLoading
        binding.editTextPassword.isEnabled = !isLoading
        binding.buttonSignUp.isClickable = !isLoading
    }
}