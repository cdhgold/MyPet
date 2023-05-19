package com.kmetabus.mypet;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiService {
    // hospital
    @Headers("Accept: application/json")
    @GET("/pet/chkdt")
    Call<ServerResponse> getValueForMonday();
    // cemetery
    @Headers("Accept: application/json")
    @GET("/pet/chkdt1")
    Call<ServerResponse> getValueForTuesday();
    // beauty
    @Headers("Accept: application/json")
    @GET("/pet/chkdt2")
    Call<ServerResponse> getValueForWednesday();
    // cafe
    @Headers("Accept: application/json")
    @GET("/pet/chkdt3")
    Call<ServerResponse> getValueForThursday();

}
