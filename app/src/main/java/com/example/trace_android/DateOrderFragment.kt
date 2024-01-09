package com.example.trace_android

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trace_android.API.MemberAPI
import com.example.trace_android.model.Member
import com.example.trace_android.model.Post
import com.example.trace_android.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DateOrderFragment : Fragment() {
    private var userEmail: String? = null
    private var content: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    val postDataList = ArrayList<PostData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_date_order, container, false)

        // 리사이클러뷰 초기화
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 어댑터 초기화
        postAdapter = PostAdapter(postDataList)
        recyclerView.adapter = postAdapter

        userEmail = activity?.intent?.getStringExtra("user_email")
        Log.d("DateOrderFragment", "User Email: $userEmail")

        userEmail?.let {
            val memberAPI = RetrofitService.retrofit.create(MemberAPI::class.java)
            memberAPI.getPostsByEmail(it).enqueue(object : Callback<List<Post>> {
                override fun onResponse(
                    call: Call<List<Post>>,
                    response: Response<List<Post>>
                ) {
                    val userList = response.body()
                    userList?.let {
                        for (member in it) {
                            val memberContent = member.content
                            Log.d("seohee", "Member Content: $memberContent")
                            postDataList.add(PostData(memberContent, "서울"))
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {

                }
            })
        }

        return view
    }
}