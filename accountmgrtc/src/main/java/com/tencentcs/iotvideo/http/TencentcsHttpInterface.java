package com.tencentcs.iotvideo.http;

import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

interface TencentcsHttpInterface {

    @POST("/")
    Observable<JsonObject> tencentcsApi(@Header("X-TC-Action") String action,
                                        @Header("X-TC-Version") String version,
                                        @Body JsonObject jsonObject);
}