package com.loopwiki.paytmcheckoutexample.API;

import com.loopwiki.paytmcheckoutexample.Model.ChecksumResponse;
import com.loopwiki.paytmcheckoutexample.Model.User;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {
    @GET("/account/login")
    Call<User> loginUser();

    @POST("/account/register")
    Call<User> registerUser(@Body User user);

    @POST("/payment/getCheckSum")
    Call<ChecksumResponse> getCheckSum(@Body HashMap order);
}
