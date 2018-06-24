package com.example.myapplication;

/**
 * Created by 이진재 on 2018-04-24.
 */

public class Chat {  // 채팅 정보를 담을 클래스

    public String email;
    public String text;

    public Chat(){

    }

    public Chat(String text){

        this.text = text;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
