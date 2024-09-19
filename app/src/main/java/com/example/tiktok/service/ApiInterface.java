package com.example.tiktok.service;

import com.example.tiktok.models.Data;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.UploadResponse;
import com.example.tiktok.models.User;
import com.example.tiktok.models.Video;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("/admin/api/users")
    Call<Root<Data<User>>> getUserByUserName();

    @GET("/admin/api/users")
    Call<Root<Data<User>>> getUserByEmail();

    @GET("/admin/api/search")
    Call<Root<Data<User>>> search(
            @Query("keyword") String keyWord
    );

    @FormUrlEncoded
    @POST("/admin/api/users")
    Call<Root<User>> signin(
            @Field("email") String email,
            @Field("fullName") String fullName,
            @Field("phoneNumber") String phoneNumber,
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/admin/api/users/login")
    Call<Root<User>> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @PUT("/admin/api/users/{userId}")
    Call<Root<User>> updateUser(
            @Field("userId") String userId,
            @Field("fullName") String fullName,
            @Field("email") String email,
            @Field("phoneNumber") String phoneNumber
    );

    @FormUrlEncoded
    @PUT("/admin/api/users/{userId}")
    Call<Root<User>> changePassword(
            @Field("userId") String userId,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword
    );

    @POST("/user/api/video")
    Call<Root<Video>> addvideo(
            @Field("videoId") String videoId,
            @Field("linkvideo") String linkvideo,
            @Field("dateuploaded") String dateupload,
            @Field("userID") String useID
    );
    @POST("/user/api/follow")
    Call<Root<User>> updatefollow(
            @Field("userID") String useID,
            @Field("follow") Boolean follow
    );
    @Multipart
    @POST("api/file/video/single")
    Call<UploadResponse> uploadVideo(@Part MultipartBody.Part video);
}
