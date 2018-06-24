package com.example.myapplication;

/**
 * Created by 이진재 on 2018-04-24.
 */

public class Item { // 장터 상품용 데이터베이스 

    public String title;
    public String price;
    public String photo;
    public String small_photo;
    public String detail;
    public String uid;
    public String time;
    public String itemkey;
    public String category;

    public Item(){
        title = "";
        price = "";
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetail() {return detail;}

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

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

    public String getSmall_photo() {
        return small_photo;
    }

    public void setSmall_photo(String small_photo) {
        this.small_photo = small_photo;
    }
}
