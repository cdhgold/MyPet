package com.kmetabus.mypet;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiService {
    @Headers("Accept: application/json")
    @GET("/pet/sse")
    Call<ServerResponse> getValue();
}
