package com.example.trace_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.trace_android.API.MemberAPI
import com.example.trace_android.model.Member
import com.example.trace_android.retrofit.RetrofitService.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageFragment : Fragment() {
    private var userEmail: String? = null
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userEmail = activity?.intent?.getStringExtra("user_email")
        Log.d("MypageFragment", "User Email: $userEmail")

        // MemberAPI를 사용하여 사용자 이름 가져오기
        userEmail?.let {
            val memberAPI = retrofit.create(MemberAPI::class.java)
            memberAPI.getMemberByEmail(it).enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        userName = user?.name // 사용자 이름 설정

                        userName?.let {
                            view?.findViewById<TextView>(R.id.userName)?.text = it
                        }
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // userEmail 값이 있다면 화면에 표시
        userEmail?.let {
            // 이메일을 userId TextView에 표시
            val userIdTextView: TextView = view.findViewById(R.id.userId) // userId TextView 찾기
            userIdTextView.text = it // 이메일 값 설정
        }
    }
}
