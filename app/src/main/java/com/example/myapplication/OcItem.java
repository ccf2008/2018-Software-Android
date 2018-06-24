package com.example.myapplication;

/**
 * Created by 이진재 on 2018-04-24.
 */

public class OcItem {

    public String title;
    public String recentprice;
    public String endprice;
    public String photo;
    public String detail;
    public String uid;
    public String time;
    public String endtime;
    public String itemkey;
    public String category;
    public String soldout;
    public String small_photo;

    public OcItem(){
        title = "";
        recentprice = "";
        endprice = "";
        photo = "";
        detail = "";
        uid = "";
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecentprice() {
        return recentprice;
    }

    public void setRecentprice(String recentprice) {
        this.recentprice = recentprice;
    }

    public String getEndprice() { return endprice; }

    public void setEndprice(String endprice) { this.endprice = endprice; }

    public String getDetail() {return detail;}

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getItemkey() {
        return itemkey;
    }

    public void setItemkey(String itemkey) {
        this.itemkey = itemkey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSoldout() {
        return soldout;
    }

    public void setSoldout(String soldout) {
        this.soldout = soldout;
    }

    public String getSmall_photo() {
        return small_photo;
    }

    public void setSmall_photo(String small_photo) {
        this.small_photo = small_photo;
    }
}
