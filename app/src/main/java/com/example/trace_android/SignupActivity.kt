package com.example.trace_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import com.example.trace_android.databinding.ActivitySignupBinding
import com.example.trace_android.model.Member
import com.example.trace_android.retrofit.MemberAPI
import com.example.trace_android.retrofit.RetrofitService
import java.util.logging.Level
import java.util.logging.Logger

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeComponents();
    }

    private fun initializeComponents() {
        val retrofitService = RetrofitService()
        val memberAPI = retrofitService.retrofit.create(MemberAPI::class.java)

        binding.signupBtn.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            val member = Member().apply {
                this.name = name
                this.email = email
                this.password = password
            }

            memberAPI.save(member)
                .enqueue(object : Callback<Member> {
                    override fun onResponse(call: Call<Member>, response: Response<Member>) {
                        this@SignupActivity.let {
                            Toast.makeText(it, "Save successful", Toast.LENGTH_LONG).show()
                            val intent = Intent(it, EmailLoginActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<Member>, t: Throwable) {
                        this@SignupActivity.let {
                            Toast.makeText(it, "Network error", Toast.LENGTH_LONG).show()
                            Logger.getLogger(MainActivity::class.java.name).log(Level.SEVERE, "Network error occurred", t)
                            // 나중에 지울 것
                            val intent = Intent(it, EmailLoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                })
        }
    }
}
