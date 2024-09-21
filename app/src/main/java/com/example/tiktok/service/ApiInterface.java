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
//    @GET("/admin/api/users")
//    Call<Root<Data<User>>> getUserByUserName();

//    @GET("/admin/api/users")
//    Call<Root<Data<User>>> getUserByEmail();

    //api check trung email (true: trung, false: khong trung)

    //search danh sach user
    @GET("/admin/api/user/search")
    Call<Root<Data<User>>> search(
            @Query("keyword") String keyword,
            @Query("pageIndex") String pageIndex,
            @Query("pageSize") String pageSize
    );

    @GET("/admin/api/user/search")
    Call<Root<Data<User>>> getByIdUser(
            @Query("user_id") String user_id,
            @Query("active_id") String active_id
    );

    @FormUrlEncoded
    @POST("/admin/api/users") //check trung email, username : isEmail: true or isUsername : true , true tuc la trung
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
            @Field("username") String username, // du lieu la username, email
            @Field("password") String password
    );

    @FormUrlEncoded
<<<<<<< HEAD
    @PUT("/admin/api/users/{userId}") //cap nhat thong tin user
=======
    @PUT("/admin/api/users/{userId}")
>>>>>>> 4941626fbe9ef82cf6b0013c8719f39c9fbe436a
    Call<Root<User>> updateUser(
            @Field("userId") String userId,
            @Field("fullName") String fullName,
            @Field("email") String email,
            @Field("phoneNumber") String phoneNumber
    );

    @FormUrlEncoded
<<<<<<< HEAD
    @PUT("/admin/api/users/change-password/{userId}")
    Call<Root<User>> changePassword(//loi code:400, data, message: "tai khong hoac mk cu khong dung"
=======
    @PUT("/admin/api/users/{userId}")
    Call<Root<User>> changePassword(
>>>>>>> 4941626fbe9ef82cf6b0013c8719f39c9fbe436a
            @Field("userId") String userId,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword
    );

    @POST("/api/video")
    Call<Root<Video>> addvideo( //them moi video, fileName duoc lay khi upload video va api tra ra fileName
            @Field("content") String content,
            @Field("fileName") String fileName,
            @Field("user_id") String useID
    );

    @POST("/user/api/follow")
    Call<Root<User>> follow(
            @Field("following_id") String following_id, //id cua user dang muon follow or unfollow
            @Field("user_id") String user_id, //id tai khoan hien tai
            @Field("isFollow") String isFollow // trang thai follow, truyen true: muon follow, false: bo follow
    );
<<<<<<< HEAD
    @Multipart
    @POST("api/file/video/single")
    Call<UploadResponse> uploadVideo(@Part MultipartBody.Part video);

    @GET("/admin/api/user/searchs")
    Call<Root<Data<User>>>getAllVideo(
            @Query("keyword") String keyword,
            @Query("pageIndex") String pageIndex,
            @Query("pageSize") String pageSize
    );

//    @GET("/user/api/video/view") // prama fileName = filename lay tu video https://nodejs-mysql-1sml.onrender.com/api/file/video/view?fileName=da9c9c41-83f8-4854-a052-ea4f1b44582b.mp4
//    Call<Root<Data<Video>>> viewvideo(
//            @Query("fileName") String fileName
//    );?piadsf/asdf/a/dsf/
//    url="https://nodejs-mysql-1sml.onrender.com/api/file/video/view?fileName=da9c9c41-83f8-4854-a052-ea4f1b44582b.mp4"
=======

    @Multipart
    @POST("api/file/video/single")
    Call<UploadResponse> uploadVideo(@Part MultipartBody.Part video);
>>>>>>> 4941626fbe9ef82cf6b0013c8719f39c9fbe436a
}
