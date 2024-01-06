package com.example.madcamp_week2_kjy_peb

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetroInterface{
    @POST("/register")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun register(
        @Body jsonparams: RegisterModel
    ) : Call<RegisterResult>

    @POST("/login")
    fun login(
        @Body jsonparams: LoginModel
    ) : Call<LoginResult>

    @GET("/users_info")
    fun allUser(): Call<ArrayList<User>>

    @GET("/my_info")
    fun getMyInfo(@Header("Authorization") token: String): Call<User>


    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "http://143.248.232.211:3000" //

        fun create(): RetroInterface {
            val gson : Gson =   GsonBuilder().setLenient().create();

            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetroInterface::class.java)
        }
    }


}