package com.example.trace_android


import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
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

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, EmailLoginActivity::class.java)
            startActivity(intent)
        }

        // 화면에 모든 뷰가 그려진 후에 실행될 작업을 예약합니다.
        binding.root.post {
            val imageWidth = binding.firstlinegroup.width.toFloat()

            // 첫 번째 이미지에 대해 연속적인 애니메이션 시작
            startContinuousAnimation(binding.firstlinegroup, imageWidth, 30000)
            startContinuousAnimation(binding.secondlinegroup, imageWidth, 28000)
            startContinuousAnimation(binding.titlelinegroup, imageWidth, 18000)
        }
    }
    private fun startContinuousAnimation(view: ImageView, imageWidth: Float, duration: Long) {
        // 이미지 뷰를 복제하여 레이아웃에 추가합니다. (레이아웃에 이미지 뷰가 하나만 있다고 가정)
        val duplicateView = ImageView(this).apply {
            layoutParams = view.layoutParams
            setImageDrawable(view.drawable)
            translationX = imageWidth
        }
        (view.parent as ViewGroup).addView(duplicateView)

        // 첫 번째 이미지의 애니메이터 설정
        val animator1 = ObjectAnimator.ofFloat(view, "translationX", -imageWidth, 0f)
        animator1.repeatCount = ObjectAnimator.INFINITE
        animator1.repeatMode = ObjectAnimator.RESTART
        animator1.duration = duration

        // 두 번째 이미지(복제된 뷰)의 애니메이터 설정
        val animator2 = ObjectAnimator.ofFloat(duplicateView, "translationX", 0f, imageWidth)
        animator2.repeatCount = ObjectAnimator.INFINITE
        animator2.repeatMode = ObjectAnimator.RESTART
        animator2.duration = duration

        animator1.start()
        animator2.start()
    }



}

