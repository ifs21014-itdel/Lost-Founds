package com.ifs21014.lostfounds.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.ifs18005.delcomtodo.data.remote.response.DataUserResponse
import com.ifs21014.lostfounds.BuildConfig
import com.ifs21014.lostfounds.R
import com.ifs21014.lostfounds.data.remote.MyResult
import com.ifs21014.lostfounds.data.repository.AuthRepository
import com.ifs21014.lostfounds.data.repository.UserRepository
import com.ifs21014.lostfounds.databinding.ActivityProfileBinding
import com.ifs21014.lostfounds.helper.Utils.Companion.observeOnce
import com.ifs21014.lostfounds.presentation.ViewModelFactory
import com.ifs21014.lostfounds.presentation.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView(){
        showLoading(true)
        observeGetMe()
    }

    private fun setupAction(){
        binding.apply {
            ivProfileBack.setOnClickListener {
                finish()
            }
            btnEditProfile.setOnClickListener {
                // Memulai ProfileManageActivity
                startActivity(Intent(this@ProfileActivity, ProfileManageActivity::class.java))
            }


        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.llProfile.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun observeGetMe(){
        viewModel.getMe().observe(this){ result ->
            if (result != null) {
                when (result) {
                    is MyResult.Loading -> {
                        showLoading(true)
                    }

                    is MyResult.Success -> {
                        showLoading(false)
                        loadProfileData(result.data)
                    }

                    is MyResult.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            applicationContext, result.error, Toast.LENGTH_LONG
                        ).show()
                        viewModel.logout()
                        openLoginActivity()
                    }
                }
            }
        }
    }

    private fun loadProfileData(profile: DataUserResponse){
        binding.apply {

//            Log.i("dedi","https://public-api.delcom.org/" + profile.user.photo )


            if(profile.user.photo != null){
                ivProfile.visibility = View.VISIBLE

                Glide.with(this@ProfileActivity)
                    .load("https://public-api.delcom.org/" + profile.user.photo)
                    .placeholder(R.drawable.ic_person)
                    .into(ivProfile)
            }else{
                ivProfile.visibility = View.GONE
            }


            tvProfileName.text = profile.user.name
            tvProfileEmail.text = profile.user.email
        }
    }

    private fun openLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}