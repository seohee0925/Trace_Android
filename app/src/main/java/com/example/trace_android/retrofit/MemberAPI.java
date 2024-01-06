package com.example.trace_android.retrofit;

import com.example.trace_android.model.Member;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MemberAPI {
    @POST("/members/register")
    Call<Member> save(@Body Member member);
}
