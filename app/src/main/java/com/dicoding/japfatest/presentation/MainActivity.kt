package com.dicoding.japfatest.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.japfatest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.apply {
            btnLogOut.setOnClickListener {
                mainViewModel.saveLoginStatus(isLoggedIn = false)
                openLoginActivity()
            }

            btnInputData.setOnClickListener {
                openInputDataActivity()
            }

            btnDisplayData.setOnClickListener {
                openDisplayDataActivity()
            }
        }
    }

    private fun openDisplayDataActivity() {
        val intent = Intent(this, DisplayDataActivity::class.java)
        startActivity(intent)
    }

    private fun openLoginActivity(){
        val Intent = Intent(this, LoginActivity::class.java)
        startActivity(Intent)
        finish()
    }

    private fun openInputDataActivity(){
        val intent = Intent(this, InputDataActivity::class.java)
        startActivity(intent)
    }

}