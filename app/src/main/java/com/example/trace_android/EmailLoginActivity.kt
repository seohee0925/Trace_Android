package com.example.trace_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import com.example.trace_android.databinding.ActivityEmailLoginBinding
import com.example.trace_android.model.Member
import com.example.trace_android.retrofit.MemberAPI
import java.util.logging.Level
import java.util.logging.Logger

class EmailLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailLoginBinding.inflate(layoutInflater)
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
                        Toast.makeText(this@EmailLoginActivity, "Save successful", Toast.LENGTH_LONG).show()
                    }
                    override fun onFailure(call: Call<Member>, t: Throwable) {
                        Toast.makeText(this@EmailLoginActivity, "Network error", Toast.LENGTH_LONG).show()
                        Logger.getLogger(MainActivity::class.java.name).log(Level.SEVERE, "Network error occurred", t)
                    }
            })
        }
    }
}