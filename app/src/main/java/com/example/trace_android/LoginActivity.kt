package com.example.trace_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.trace_android.databinding.ActivityLoginBinding
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clientId: String = BuildConfig.NAVER_CLIENT_ID
        val clientSecret: String = BuildConfig.NAVER_CLIENT_SECRET
        val clientName: String = BuildConfig.NAVER_CLIENT_NAME

        // 네이버 아이디 로그인 SDK 초기화
        NaverIdLoginSDK.initialize(
            this,
            clientId,
            clientSecret,
            clientName
        )

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                Log.d("test", "AccessToken : " + NaverIdLoginSDK.getAccessToken())
                Log.d("test", "client id : " + clientId)
                Log.d("test", "ReFreshToken : " + NaverIdLoginSDK.getRefreshToken())
                Log.d("test", "Expires : " + NaverIdLoginSDK.getExpiresAt().toString())
                Log.d("test", "TokenType : " + NaverIdLoginSDK.getTokenType())
                Log.d("test", "State : " + NaverIdLoginSDK.getState().toString())

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Log.e("test", "$errorCode $errorDescription")
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        binding.buttonOAuthLoginImg.setOnClickListener {
            NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
        }
    }
}
