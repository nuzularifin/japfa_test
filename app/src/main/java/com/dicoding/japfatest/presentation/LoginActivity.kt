package com.dicoding.japfatest.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.japfatest.databinding.ActivityLoginBinding
import com.dicoding.japfatest.utils.show
import com.dicoding.japfatest.utils.showToastMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLoginBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (mainViewModel.getLoginStatus()){
            openMainActivity()
        }

        binding.apply {
            btnLogin.setOnClickListener {
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()
                mainViewModel.login(username, password)
            }
        }
        mainViewModel.message.observe(this){
            showToastMessage(this, it)
        }
        mainViewModel.isLoggedIn.observe(this){
            if (it){
                openMainActivity()
            }
        }
        mainViewModel.isLoading.observe(this){
            if(it){
                binding.pbLoading.show(true)
                binding.btnLogin.show(false)
            } else {
                binding.pbLoading.show(false)
                binding.btnLogin.show(true)
            }
        }
    }

    private fun openMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}