package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.PasswordAuthentication;
import java.util.Properties;
import java.util.Random;

import javax.sql.DataSource;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.view.View.GONE;

public class Selectitem extends AppCompatActivity{

    String title;
    String price;
    String detail;
    String photo;
    String useruid;
    String chatRoomUid;
    String friendname;
    String friendemail;
    String friendphoto;
    String friendschoolnumber;
    String myuid;

    TextView tvtitle;
    TextView tvprice;
    TextView tvdetail;
    ImageView ivphoto;
    Button btnchat;
    TextView profile_name;
    TextView profile_email;
    TextView profile_schoolnumber;
    ImageView ivprofile;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_item);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        tvprice = (TextView)findViewById(R.id.tvprice);
        tvdetail = (TextView)findViewById(R.id.tvdetail);
        ivphoto = (ImageView)findViewById(R.id.ivImage);
        btnchat = (Button)findViewById(R.id.btnchat);
        ivprofile = (ImageView)findViewById(R.id.image);
        profile_name = (TextView)findViewById(R.id.name);
        profile_email = (TextView)findViewById(R.id.email);
        profile_schoolnumber = (TextView)findViewById(R.id.schoolnumber);

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String loginId = auto.getString("inputId","");
        String loginPwd = auto.getString("inputPwd","");

        String itemkey="";
        Intent in = getIntent();
        itemkey = in.getStringExtra("itemkey");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("items").child(itemkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                title = dataSnapshot.child("title").getValue().toString();
                price = dataSnapshot.child("price").getValue().toString();
                detail = dataSnapshot.child("detail").getValue().toString();
                photo = dataSnapshot.child("photo").getValue().toString();
                useruid = dataSnapshot.child("uid").getValue().toString();
                tvtitle.setText(title);
                tvprice.setText(price +"원");
                tvdetail.setText(detail);
                Glide.with(Selectitem.this).load(photo).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
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
                            Glide.with(Selectitem.this).load(R.mipmap.ic_nophoto_round).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                    .into(ivprofile);
                        } else {
                            Glide.with(Selectitem.this).load(friendphoto).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
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



        btnchat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef;

                myuid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                myRef = database.getReference("users").child(myuid).child("mychat");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot item : dataSnapshot.getChildren()) {
                            ChatModel.Mychat chatModel = item.getValue(ChatModel.Mychat.class);
                            if (chatModel.Uid.equals(useruid)) {
                                chatRoomUid = chatModel.chatRoomUid;
                                Intent in = new Intent(Selectitem.this,ChatActivity.class);
                                in.putExtra("friendUid", useruid);
                                in.putExtra("chatRoomUid", chatRoomUid );
                                in.putExtra("friendname",friendname);
                                startActivity(in);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });
                Intent in = new Intent(Selectitem.this,ChatActivity.class);
                in.putExtra("friendUid", useruid);
                in.putExtra("chatRoomUid", chatRoomUid );
                in.putExtra("friendname",friendname);
                startActivity(in);
            }
        });

    }

}

