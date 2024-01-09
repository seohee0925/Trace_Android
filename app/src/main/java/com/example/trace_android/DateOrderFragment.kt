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
import com.example.trace_android.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DateOrderFragment : Fragment() {
    private var userEmail: String? = null
    private var userName: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_date_order, container, false)

        // 리사이클러뷰 초기화
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 어댑터 초기화
        val postDataList = ArrayList<PostData>()
        postAdapter = PostAdapter(postDataList)
        recyclerView.adapter = postAdapter

        val postData1 = PostData("hi", "place")
        postDataList.add(postData1)

        val postData2 = PostData("hi", "Content 2")
        postDataList.add(postData2)

        postAdapter.notifyDataSetChanged()
        return view
    }
}