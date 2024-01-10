package com.example.trace_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trace_android.API.MemberAPI
import com.example.trace_android.model.Post
import com.example.trace_android.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CalendarFragment : Fragment() {
    private var userEmail: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val postDataList = ArrayList<PostData>()
    private var selectedDate: Calendar = Calendar.getInstance() // 기본적으로 현재 날짜로 초기화

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        // 리사이클러뷰 초기화
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 어댑터 초기화
        postAdapter = PostAdapter(postDataList)
        recyclerView.adapter = postAdapter

        // DatePicker 초기화 및 날짜 선택 이벤트 처리
        val datePicker = view.findViewById<DatePicker>(R.id.datePicker)
        datePicker.init(
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->
            selectedDate.set(year, monthOfYear, dayOfMonth)
            fetchDataForSelectedDate()
        }

        userEmail = activity?.intent?.getStringExtra("user_email")

        // 초기 데이터 가져오기
        fetchDataForSelectedDate()

        return view
    }

    // 선택된 날짜에 따른 데이터 가져오기
    private fun fetchDataForSelectedDate() {
        userEmail?.let {
            val memberAPI = RetrofitService.retrofit.create(MemberAPI::class.java)
            memberAPI.getPostsByEmail(it).enqueue(object : Callback<List<Post>> {
                override fun onResponse(
                    call: Call<List<Post>>,
                    response: Response<List<Post>>
                ) {
                    val userList = response.body()

                    userList?.let {
                        postDataList.clear() // 기존 데이터 클리어
                        for (member in it) {
                            val memberContent = member.content
                            val combinedImage = "${member.image}${member.imageExtra}"
                            val place = member.address
                            val date = member.createdDate

                            val postDate = Calendar.getInstance()
                            postDate.time = date // Date를 Calendar로 변환

                            // 선택된 날짜와 같은 데이터만 추가
                            if (selectedDate.get(Calendar.YEAR) == postDate.get(Calendar.YEAR) &&
                                selectedDate.get(Calendar.MONTH) == postDate.get(Calendar.MONTH) &&
                                selectedDate.get(Calendar.DAY_OF_MONTH) == postDate.get(Calendar.DAY_OF_MONTH)
                            ) {
                                postDataList.add(PostData(combinedImage, memberContent, place, date))
                            }
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    // 실패 시 처리
                }
            })
        }
    }
}