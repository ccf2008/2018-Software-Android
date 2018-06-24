package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Bid extends AppCompatActivity {

    String title;
    String startprice;
    String recentprice;
    String endprice;
    String itemkey;
    String bidmankey;
    String recentmankey;
    String useruid;
    String chatRoomUid;
    String friendname;
    String soldout;
    String time;
    String endtime;
    String detail;
    String myuid;
    String destination_pushToken;

    TextView tvtitle;
    TextView tvtime;
    TextView tvendtime;
    TextView tvstartprice;
    TextView tvrecentprice;
    TextView tvendprice;
    TextView tvdetail;
    TextView tvremaintime;
    Button btnbid;
    Button btnchat;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid);

        tvtitle = (TextView)findViewById(R.id.tvtitle);
        tvstartprice = (TextView)findViewById(R.id.tvstartprice);
        tvrecentprice = (TextView)findViewById(R.id.tvrecentprice);
        tvendprice = (TextView)findViewById(R.id.tvendprice);
        btnbid = (Button)findViewById(R.id.btnbid);
        btnchat = (Button)findViewById(R.id.btnchat);
        tvtime = (TextView)findViewById(R.id.tvtime);
        tvendtime = (TextView)findViewById(R.id.tvendtime);
        tvdetail = (TextView)findViewById(R.id.tvdetail);

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String loginId = auto.getString("inputId","");
        String loginPwd = auto.getString("inputPwd","");


        Intent in = getIntent();
        itemkey = in.getStringExtra("itemkey");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("octionitems").child(itemkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                title = dataSnapshot.child("title").getValue().toString();
                startprice = dataSnapshot.child("startprice").getValue().toString();
                recentprice = dataSnapshot.child("recentprice").getValue().toString();
                endprice = dataSnapshot.child("endprice").getValue().toString();
                time = dataSnapshot.child("time").getValue().toString();
                endtime = dataSnapshot.child("endtime").getValue().toString();
                recentmankey = dataSnapshot.child("bidmankey").getValue().toString();
                useruid = dataSnapshot.child("uid").getValue().toString();
                soldout = dataSnapshot.child("soldout").getValue().toString();
                detail = dataSnapshot.child("detail").getValue().toString();
                tvtitle.setText(title);
                tvstartprice.setText(startprice);
                tvrecentprice.setText(recentprice);
                tvendprice.setText(endprice);
                tvtime.setText(time);
                tvendtime.setText(endtime);
                tvdetail.setText(detail);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                myRef.child("users").child(useruid).addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        friendname = dataSnapshot.child("name").getValue().toString();
                        destination_pushToken = dataSnapshot.child("pushToken").getValue().toString();
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


        Button btnbid = (Button)findViewById(R.id.btnbid);
        btnbid.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                bidmankey = user.getUid();
                EditText etbidprice = (EditText)findViewById(R.id.etbidprice);

                int startpricen = Integer.parseInt(startprice);
                int recentpricen = Integer.parseInt(recentprice);
                int endpricen = Integer.parseInt(endprice);

                if(etbidprice.getText().toString().isEmpty()||etbidprice.getText().toString().equals("")) {
                    Toast.makeText(Bid.this, "입찰 가격을 입력해 주세요..", Toast.LENGTH_SHORT).show();
                } else{
                    int bidpricen = Integer.parseInt(etbidprice.getText().toString());
                    if (soldout.equals("0"))
                        Toast.makeText(Bid.this, "경매가 종료된 상품입니다.", Toast.LENGTH_SHORT).show();
                    else if (startpricen >= bidpricen)
                        Toast.makeText(Bid.this, "입찰 가격은 시작 가격보다 높아야 합니다.", Toast.LENGTH_SHORT).show();
                    else if (recentpricen >= bidpricen)
                        Toast.makeText(Bid.this, "입찰 가격은 현재 가격보다 높아야 합니다.", Toast.LENGTH_SHORT).show();
                    else if (endpricen < bidpricen)
                        Toast.makeText(Bid.this, "입찰 가격은 즉시구매 가격보다 낮아야 합니다.", Toast.LENGTH_SHORT).show();
                    else if ((endpricen == bidpricen) || bidmankey.equals(recentmankey)) {

                        recentprice = etbidprice.getText().toString();
                        FirebaseDatabase.getInstance().getReference("users").child(recentmankey).child("myBid").child(itemkey).setValue(null);
                        FirebaseDatabase.getInstance().getReference("octionitems").child(itemkey).child("recentprice").setValue(recentprice);
                        FirebaseDatabase.getInstance().getReference("octionitems").child(itemkey).child("bidmankey").setValue(bidmankey);
                        FirebaseDatabase.getInstance().getReference("users").child(bidmankey).child("myBid").child(itemkey).setValue(recentprice);
                        FirebaseDatabase.getInstance().getReference("octionitems").child(itemkey).child("soldout").setValue("0");

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
                                        sendGcm();
                                        Intent in = new Intent(Bid.this,ChatActivity.class);
                                        in.putExtra("friendUid", useruid);
                                        in.putExtra("chatRoomUid", chatRoomUid );
                                        in.putExtra("friendname",friendname);
                                        startActivity(in);

                                        Toast.makeText(Bid.this, "낙찰되셨습니다. 축하합니다! 프로필 창의 '내가 낙찰한 상품' 버튼으로 재확인 가능합니다.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                            }
                        });
                        sendGcm();
                        Intent in = new Intent(Bid.this,ChatActivity.class);
                        in.putExtra("friendUid", useruid);
                        in.putExtra("chatRoomUid", chatRoomUid );
                        in.putExtra("friendname",friendname);
                        startActivity(in);

                        Toast.makeText(Bid.this, "낙찰되셨습니다. 축하합니다! 프로필 창의 '내가 낙찰한 상품' 버튼으로 재확인 가능합니다.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        recentprice = etbidprice.getText().toString();

                        FirebaseDatabase.getInstance().getReference("octionitems").child(itemkey).child("recentprice").setValue(recentprice);
                        FirebaseDatabase.getInstance().getReference("octionitems").child(itemkey).child("bidmankey").setValue(bidmankey);
                        FirebaseDatabase.getInstance().getReference("users").child(bidmankey).child("myBid").child(itemkey).setValue(recentprice);

                        Intent in = new Intent(Bid.this, TabActivity.class);
                        startActivity(in);

                        Toast.makeText(Bid.this, "입찰이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    void sendGcm(){
        Gson gson = new Gson();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destination_pushToken;
        notificationModel.data.title = "경매가 낙찰되었습니다.";
        notificationModel.data.text = title;


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder().header("Content-Type", "application/json")
                .addHeader("Authorization","key=AIzaSyBRZ8nZ3lnrXc0UoBdqne4np6ZIsj2P0h0")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e){

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{

            }
        });
    }
}
