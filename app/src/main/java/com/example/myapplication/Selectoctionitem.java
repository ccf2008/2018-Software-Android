package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class Selectoctionitem extends AppCompatActivity { // 다른 사용자의 경매 상품 선택 액티비티

    String title;
    String startprice;
    String recentprice;
    String endprice;
    String detail;
    String photo;
    String useruid;
    String itemkey;
    String endtime;
    String time;
    String friendname;
    String friendemail;
    String friendphoto;
    String friendschoolnumber;
    String soldout;

    TextView tvendtime;
    TextView tvtime;
    TextView tvtitle;
    TextView tvstartprice;
    TextView tvrecentprice;
    TextView tvendprice;
    TextView tvdetail;
    ImageView ivphoto;
    Button btnbid;
    TextView profile_name;
    TextView profile_email;
    TextView profile_schoolnumber;
    ImageView ivprofile;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oc_select_item);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        tvstartprice = (TextView)findViewById(R.id.tvstartprice);
        tvrecentprice = (TextView)findViewById(R.id.tvrecentprice);
        tvendprice = (TextView)findViewById(R.id.tvendprice);
        tvdetail = (TextView)findViewById(R.id.tvdetail);
        tvtime =  (TextView)findViewById(R.id.tvtime);
        tvendtime =  (TextView)findViewById(R.id.tvendtime);
        ivphoto = (ImageView)findViewById(R.id.ivImage);
        btnbid = (Button)findViewById(R.id.btnbid);
        ivprofile = (ImageView)findViewById(R.id.image);
        profile_name = (TextView)findViewById(R.id.name);
        profile_email = (TextView)findViewById(R.id.email);
        profile_schoolnumber = (TextView)findViewById(R.id.schoolnumber);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();




        Intent in = getIntent();
        itemkey = in.getStringExtra("itemkey");

        myRef.child("octionitems").child(itemkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                soldout = dataSnapshot.child("soldout").getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        myRef.child("octionitems").child(itemkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                title = dataSnapshot.child("title").getValue().toString();
                startprice = dataSnapshot.child("startprice").getValue().toString();
                recentprice = dataSnapshot.child("recentprice").getValue().toString();
                endprice = dataSnapshot.child("endprice").getValue().toString();
                detail = dataSnapshot.child("detail").getValue().toString();
                photo = dataSnapshot.child("photo").getValue().toString();
                useruid = dataSnapshot.child("uid").getValue().toString();
                time = dataSnapshot.child("time").getValue().toString();
                endtime = dataSnapshot.child("endtime").getValue().toString();
                tvtitle.setText(title);
                tvstartprice.setText(startprice+"원");
                tvrecentprice.setText(recentprice+"원");
                tvendprice.setText(endprice+"원");
                tvdetail.setText(detail);
                tvtime.setText(time);
                tvendtime.setText(endtime);
                Glide.with(Selectoctionitem.this).load(photo).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .centerCrop().into(ivphoto);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                myRef.child("users").child(useruid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        friendname = dataSnapshot.child("name").getValue().toString();
                        friendemail = dataSnapshot.child("email").getValue().toString();
                        friendphoto = dataSnapshot.child("photo").getValue().toString();
                        friendschoolnumber = dataSnapshot.child("schoolid").getValue().toString();

                        profile_name.setText(friendname);
                        profile_email.setText(friendemail);
                        profile_schoolnumber.setText(friendschoolnumber);

                        if(friendphoto == ""|| TextUtils.isEmpty(friendphoto)){
                            Glide.with(Selectoctionitem.this).load(R.mipmap.ic_nophoto_round).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                    .into(ivprofile);
                        } else {
                            Glide.with(Selectoctionitem.this).load(friendphoto).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                    .thumbnail(0.1f).placeholder(R.mipmap.ic_nophoto_round).error(R.mipmap.ic_nophoto_round).listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(ivprofile);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });



        btnbid.setOnClickListener(new View.OnClickListener(){ // 입찰 버튼
            @Override
            public void onClick(View view){
                if(soldout.equals("0")){
                    Toast.makeText(Selectoctionitem.this, "이미 낙찰 된 상품입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent in = new Intent(Selectoctionitem.this, Bid.class);
                    in.putExtra("itemkey", itemkey);
                    startActivity(in);
                    finish();
                }
            }
        });

    }
    @Override
    public void onBackPressed(){ // 뒤로가기 처리
            setResult(1);
            finish();
    }
}

