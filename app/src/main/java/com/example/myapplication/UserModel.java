package com.example.myapplication;

/**
 * Created by 이진재 on 2018-04-24.
 */

public class UserModel {

    public String email;
    public String key;
    public String name;
    public String photo;
    public String pushToken;
    public String schoolid;

    public UserModel(){

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }
}
