package com.example.tiktok.service;

import androidx.annotation.Nullable;

import com.example.tiktok.models.Comment;
import com.example.tiktok.models.Data;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.UploadResponse;
import com.example.tiktok.models.User;
import com.example.tiktok.models.Video;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
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
    @GET("/api/users/search")
    Call<Root<Data<User>>> search(
            @Query("keyword") String keyword,
            @Query("pageIndex") @Nullable String pageIndex,
            @Query("pageSize") @Nullable String pageSize
    );

    @GET("/api/users/{user_id}")
    Call<Root<User>> getByIdUser(
            @Path("user_id") String user_id
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
            @Path("userId") String userId,
            @Field("fullName") String fullName,
            @Field("email") String email,
            @Field("phoneNumber") String phoneNumber,
            @Field("avatar") String avatar
    );

    @FormUrlEncoded
    @PUT("/api/users/avatar/{userId}") //cap nhat thong tin user
    Call<Root<User>> updateUserAvatar(
            @Path("userId") String userId,
            @Field("avatar") String avatar
    );

    @FormUrlEncoded
    @PUT("/api/users/change-password/{userId}")
    Call<Root<User>> changePassword(//loi code:400, data, message: "tai khong hoac mk cu khong dung"
            @Path("userId") String userId,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword
    );

    @FormUrlEncoded
    @PUT("/api/users/follow")
    Call<Root<User>> follow(
            @Field("following_id") String following_id, //id cua user dang muon follow or unfollow
            @Field("user_id") String user_id //id tai khoan hien tai
//            @Field("isFollow") Boolean isFollow // trang thai follow, truyen true: muon follow, false: bo follow
    );

    @FormUrlEncoded
    @POST("/api/videos")
    Call<Root<Video>> addvideo( //them moi video, fileName duoc lay khi upload video va api tra ra fileName
            @Field("content") String content,
            @Field("fileName") String fileName,
            @Field("user_id") String user_id
    );

    @DELETE("/api/videos/{video_id}")
    Call<Root<Boolean>> deletevideo(
            @Path("video_id") String video_id
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

    @GET("api/videos/{video_id}")
    Call<Root<Video>>getVideoById(
            @Path("video_id") String video_id
    );

    @FormUrlEncoded
    @PUT("/api/videos/{video_id}/update-like")
    Call<Root<Video>> like(
            @Path("video_id") String video_id, //id cua video dang muon like or unlike
            @Field("user_id") String user_id //id tai khoan hien tai
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
    @POST("api/file/video/single-hls")
    Call<UploadResponse> uploadVideo(@Part MultipartBody.Part video);

    @FormUrlEncoded
    @POST("/api/comments")
    Call<Root<Comment>> addcomment(
            @Field("user_id") String user_id, // du lieu la username, email
            @Field("content") String content,
            @Field("video_id") String video_id
    );

    @GET("/api/comments/search")
    Call<Root<Data<Comment>>>getAllComment(
            @Query("keyword") @Nullable String keyword,
            @Query("video_id") @Nullable String video_id,
            @Query("pageIndex") @Nullable String pageIndex,
            @Query("pageSize") @Nullable String pageSize
    );
}
