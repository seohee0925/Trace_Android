package com.example.trace_android
import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.trace_android.API.LogoutAPI
import com.example.trace_android.API.MemberAPI
import com.example.trace_android.R
import com.example.trace_android.model.Member
import com.example.trace_android.retrofit.RetrofitService.retrofit
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageFragment : Fragment() {
    private var userEmail: String? = null
    private var userName: String? = null
    private lateinit var mypageAdapter: MypageAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private var tabCurrentIdx = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        viewPager.isSaveEnabled = false;

        mypageAdapter = MypageAdapter(childFragmentManager, tabLayout.tabCount)
        viewPager.adapter = mypageAdapter
        viewPager.currentItem = tabCurrentIdx

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                tabCurrentIdx = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val logoutButton: Button = view.findViewById(R.id.logout_btn)
        logoutButton.setOnClickListener {
            logoutUser()
        }

        return view
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // userEmail 값이 있다면 화면에 표시
        userEmail?.let {
            val userIdTextView: TextView = view.findViewById(R.id.userId) // userId TextView 찾기
            userIdTextView.text = it // 이메일 값 설정
        }
    }

    private fun logoutUser() {
        val logoutAPI = retrofit.create(LogoutAPI::class.java)
        logoutAPI.logout().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 로그아웃이 성공하면 로그인 화면으로 이동
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                } else {

                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })
    }
}
