package com.example.tiktok.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User implements Serializable {
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    // Tag
    public static final String TAG = "User";

    private String user_id,username, fullName, phoneNumber, email,password, avatar, role;
    private int num_followers, num_following, num_like;
    private List<String> followings, followers;

    public User(String username, String fullName, String phoneNumber, String email,String password, String avatar, String user_id, int num_followers, int num_following, int num_like, List<String> followings, List<String> followers) {
        this.username = username;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.user_id = user_id;
        this.num_followers = num_followers;
        this.num_following = num_following;
        this.num_like = num_like;
        this.followings = followings;
        this.followers = followers;
    }

    public User() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String uid) {
        if (this.user_id == null) this.user_id = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (this.username == null) this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber == null ? "" : phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getNum_followers() {
        return num_followers;
    }

    public void setNum_followers(int num_followers) {
        this.num_followers = num_followers;
    }

    public int getNum_following() {
        return num_following;
    }

    public void setNum_following(int num_following) {
        this.num_following = num_following;
    }

    public int getNum_like() {
        return num_like;
    }

    public void setNum_like(int num_like) {
        this.num_like = num_like;
    }

    public List<String> getFollowings() {
        return followings;
    }

    public void setFollowings(List<String> followings) {
        this.followings = followings;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public boolean isAdmin() {
        return role.equals(ROLE_ADMIN);
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public boolean isFollowing(String user_id) {
        return followings != null && followings.contains(user_id);
    }

    public void follow(String user_id) {
        // Add to followings
        if (followings == null) followings = new ArrayList<>();
        followings.add(user_id);

        // Update numFollowing
        num_following++;
    }
}
