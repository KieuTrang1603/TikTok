package com.example.tiktok.service;

import androidx.annotation.Nullable;

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
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
//    @GET("/admin/api/users")
//    Call<Root<Data<User>>> getUserByUserName();

//    @GET("/admin/api/users")
//    Call<Root<Data<User>>> getUserByEmail();

    //api check trung email (true: trung, false: khong trung)

    //search danh sach user
    @GET("/api/user/search")
    Call<Root<Data<User>>> search(
            @Query("keyword") String keyword,
            @Query("pageIndex") String pageIndex,
            @Query("pageSize") String pageSize
    );

    @GET("/api/user/search")
    Call<Root<Data<User>>> getByIdUser(
            @Query("user_id") String user_id,
            @Query("active_id") String active_id
    );

    @FormUrlEncoded
    @POST("/api/users") //check trung email, username : isEmail: true or isUsername : true , true tuc la trung
    Call<Root<User>> signin(
            @Field("email") String email,
            @Field("fullName") String fullName,
            @Field("phoneNumber") String phoneNumber,
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/api/users/login")
    Call<Root<User>> login(
            @Field("username") String username, // du lieu la username, email
            @Field("password") String password
    );

    @FormUrlEncoded
    @PUT("/api/users/{userId}") //cap nhat thong tin user
    Call<Root<User>> updateUser(
            @Field("userId") String userId,
            @Field("fullName") String fullName,
            @Field("email") String email,
            @Field("phoneNumber") String phoneNumber
    );

    @FormUrlEncoded
    @PUT("/api/users/change-password/{userId}")
    Call<Root<User>> changePassword(//loi code:400, data, message: "tai khong hoac mk cu khong dung"
            @Field("userId") String userId,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword
    );

    @FormUrlEncoded
    @POST("/api/videos")
    Call<Root<Video>> addvideo( //them moi video, fileName duoc lay khi upload video va api tra ra fileName
            @Field("content") String content,
            @Field("fileName") String fileName,
            @Field("user_id") String user_id
    );

    @PUT("api/videos/{video_id}/increment-view")
    Call<Root<Video>> addview( //tang view
            @Path("video_id") String video_id
    );

    @GET("/api/videos/search")
    Call<Root<Data<Video>>>getAllVideo(
            @Query("keyword") @Nullable String keyword,
            @Query("user_id") @Nullable String user_id,
            @Query("pageIndex") @Nullable String pageIndex,
            @Query("pageSize") @Nullable String pageSize
    );

    @POST("/user/api/follow")
    Call<Root<User>> follow(
            @Field("following_id") String following_id, //id cua user dang muon follow or unfollow
            @Field("user_id") String user_id, //id tai khoan hien tai
            @Field("isFollow") Boolean isFollow // trang thai follow, truyen true: muon follow, false: bo follow
    );

    @POST("/video/api/like")
    Call<Root<User>> like(
            @Field("video_id") String video_id, //id cua video dang muon like or unlike
            @Field("user_id") String user_id, //id tai khoan hien tai
            @Field("isLike") Boolean isLike // trang thai like, truyen true: muon like, false: bo like
    );

    @GET("/api/user/search")
    Call<Root<Data<User>>>getAllUser(
            @Query("keyword") String keyword,
            @Query("pageIndex") String pageIndex,
            @Query("pageSize") String pageSize
    );

    @Multipart
    @POST("/api/file/image/single")
    Call<UploadResponse> uploadImage(@Part MultipartBody.Part file);

    @Multipart
    @POST("api/file/video/single")
    Call<UploadResponse> uploadVideo(@Part MultipartBody.Part video);
}
