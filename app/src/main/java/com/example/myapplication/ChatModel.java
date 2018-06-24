package com.example.myapplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 이진재 on 2018-05-07.
 */

public class ChatModel { // 데이터베이스에서 채팅방 및 채팅글을 받아오는 양식

    public Map<String, Boolean> users = new HashMap<>();
    public Map<String, Comment> comments = new HashMap<>();


    public  static class Mychat{
        public String Uid;
        public String chatRoomUid;
    }


    public static class Comment{
        public String uid;
        public String message;
        public String time;
        public String name;
        public String daytime;


        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDaytime() {
            return daytime;
        }

        public void setDaytime(String daytime) {
            this.daytime = daytime;
        }
    }




}
