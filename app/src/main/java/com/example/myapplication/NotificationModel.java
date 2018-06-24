package com.example.myapplication;

import android.app.Notification;

/**
 * Created by 이진재 on 2018-05-17.
 */

public class NotificationModel {

    public String to;
    public Data data = new Data();

    public static class Data{
        public String title;
        public String text;
    }
}
