package com.dicoding.capstone_ch2ps254

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.capstone_ch2ps254.databinding.ActivityHomeBinding
import com.dicoding.capstone_ch2ps254.utils.Session
import com.dicoding.capstone_ch2ps254.R.string
import com.dicoding.capstone_ch2ps254.camera.CameraActivity
import com.dicoding.capstone_ch2ps254.data.remote.ApiResponse
import com.dicoding.capstone_ch2ps254.login.LoginActivity
import com.dicoding.capstone_ch2ps254.pose.ListViewModel
import com.dicoding.capstone_ch2ps254.pose.PoseAdapter
import com.dicoding.capstone_ch2ps254.user.UserActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val listViewModel: ListViewModel by viewModels()
    private var _activityHomeBinding: ActivityHomeBinding? = null
    private val binding get() = _activityHomeBinding!!
    private lateinit var pref: Session
    private var token: String? = null

    companion object {

        fun begin(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = Session(this)
        token = pref.getToken

        if (!pref.isLogin) {
            LoginActivity.begin(this)
            finish()
            return
        }

        if (token.isNullOrEmpty() || !pref.isLogin) {
            LoginActivity.begin(this)
            finish()
            return
        }

        _activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(_activityHomeBinding?.root)

        initAct()
        initUI()

        getPose("Bearer $token")
    }

    private fun initAct() {
        binding.btnProfile.setOnClickListener {
            UserActivity.begin(this)
        }
    }

    private fun initUI() {
        binding.rvItem.layoutManager = LinearLayoutManager(this)
        binding.tvName.text = getString(string.welcome_message, pref.getUserName)
    }

    private fun getPose(token: String){
        listViewModel.getPose(token).observe(this) {response ->
            when (response) {
                is ApiResponse.Loading -> isLoading(true)
                is ApiResponse.Success -> {
                    isLoading(false)
                    val adapter = PoseAdapter(response.data.data.poses)
                    binding.rvItem.adapter = adapter

                    adapter.setOnItemClickListener(object : PoseAdapter.OnItemClickListener {
                        override fun onItemClick(id: Int, name: String) {
                            navigateToCameraActivity(id, name)
                        }
                    })
                }
                is ApiResponse.Error -> isLoading(false)
                else -> {
                    Timber.e(getString(string.statement_error))
                }
            }
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.apply {
                flShimmer.visibility = View.VISIBLE
                flShimmer.startShimmer()
                rvItem.visibility = View.INVISIBLE
            }
        } else {
            binding.apply {
                rvItem.visibility = View.VISIBLE
                flShimmer.stopShimmer()
                flShimmer.visibility = View.INVISIBLE
            }
        }
    }

    private fun navigateToCameraActivity(id: Int, name: String) {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("name", name)

        val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            binding.rvItem, "rvItemTransition"
        )

        startActivity(intent, optionsCompat.toBundle())
    }

    override fun onResume() {
        super.onResume()
        getPose("Bearer $token")
    }
}