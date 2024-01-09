package com.example.trace_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trace_android.API.LoginAPI
import com.example.trace_android.databinding.ActivityEmailLoginBinding
import com.example.trace_android.model.Member
import com.example.trace_android.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailLoginBinding
    private val retrofitService = RetrofitService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinBtn.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Please enter e-mail and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        setupBackButton()
    }

    private fun setupBackButton() {
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun login(email: String, password: String) {
        val call = retrofitService.retrofit.create(LoginAPI::class.java).login(email, password)
        call.enqueue(object : Callback<Member> {
            override fun onResponse(call: Call<Member>, response: Response<Member>) {
                if (response.isSuccessful) {
                    val member = response.body()
                    // 로그인 성공 시
                    member?.let {
                        val intent = Intent(this@EmailLoginActivity, MainActivity::class.java)
                        intent.putExtra("user_email", it.email)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this@EmailLoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Member>, t: Throwable) {
                // Handle network errors
                Toast.makeText(this@EmailLoginActivity, "Network error, please try again", Toast.LENGTH_SHORT).show()
            }
        })
    }

}