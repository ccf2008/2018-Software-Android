package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {  // 채팅을  액티비티

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String chatRoomUid;

    String TAG = "ChatActivity";
    EditText etText;
    Button btnSend;
    String email;
    String key;
    FirebaseDatabase database;
    List<ChatModel.Comment> mChat;
    List<ChatModel.Mychat> mRoomUid;
    String stChatid;
    String destination_pushToken;
    String username;
    TextView tvtitle;
    DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("chatrooms").push();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            key = user.getUid();
        }

        final Intent in = getIntent();
        stChatid = in.getStringExtra("friendUid");
        chatRoomUid = in.getStringExtra("chatRoomUid");

        final String friendname = in.getStringExtra("friendname");
        tvtitle = (TextView)findViewById(R.id.title_name);
        tvtitle.setText(friendname);

        DatabaseReference myRef;
        SharedPreferences userinfo = getSharedPreferences("auto", Context.MODE_PRIVATE);
        username = userinfo.getString("name", "");


        etText = (EditText)findViewById(R.id.etText);
        btnSend = (Button)findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener(){ // 메세지 전송버튼을 눌렀을 경우

            @Override
            public void onClick(View view){
                final String stText = etText.getText().toString();
                if(stText.equals("")||stText.isEmpty()){
                    Toast.makeText(ChatActivity.this,"내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else{
                    database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users").child(stChatid).child("pushToken");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            destination_pushToken = dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd aa:hh:mm:ss");
                    SimpleDateFormat dfchat = new SimpleDateFormat("aa:hh:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    String formattedDatechat = dfchat.format(c.getTime());
                    ChatModel chatModel = new ChatModel();
                    chatModel.users.put(key,true);
                    chatModel.users.put(stChatid,true);

                    if(chatRoomUid == null){  // 상대방과의 채팅방이 없을 경우 채팅방을 새로 생성하고 대화시작
                        btnSend.setEnabled(false);
                        chatRoomUid = Ref.getKey().toString();
                        Ref.setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                checkChatRoom(stText,friendname);
                            }
                        });
                        sendGcm();
                        etText.setText("");
                    } else{ // 기존에 상대방과의 채팅방이 있을 경우 바로 대화 시작
                        ChatModel.Comment comment = new ChatModel.Comment();
                        comment.uid = key;
                        comment.message = stText;
                        comment.time = formattedDatechat;
                        comment.daytime = formattedDate;
                        FirebaseDatabase.getInstance().getReference("chatrooms").child(chatRoomUid).child("comments").child(formattedDate).setValue(comment);
                        sendGcm();
                        etText.setText("");
                    }
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mChat = new ArrayList<>();
        mRoomUid = new ArrayList<>();
        mAdapter = new MyAdapter(mChat,key,friendname);
        mRecyclerView.setAdapter(mAdapter);


            if(chatRoomUid != null) { // 채팅창이 켜졌을 때 기존의 대화 내용을 불러옴
                myRef = database.getReference("chatrooms").child(chatRoomUid).child("comments");
                myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        ChatModel.Comment comment = dataSnapshot.getValue(ChatModel.Comment.class);
                        mChat.add(comment);
                        mRecyclerView.scrollToPosition(mChat.size()-1);
                        mAdapter.notifyItemInserted(mChat.size()-1);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
    }

    void sendGcm(){  // 푸시 알림 전송 함수
        Gson gson = new Gson();


        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destination_pushToken;
        notificationModel.data.title = username;
        notificationModel.data.text = etText.getText().toString();


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

    void checkChatRoom(final String stText, final String friendname){ // 채팅방을 새로 생성했을 경우 데이터베이스를 업데이트하고 메세지를 전송하는 함수
                        ChatModel.Mychat mychat = new ChatModel.Mychat();
                        mychat.Uid = stChatid;
                        mychat.chatRoomUid = chatRoomUid;
                        FirebaseDatabase.getInstance().getReference("users").child(key).child("mychat").child(chatRoomUid).setValue(mychat);
                        mychat.Uid = key;
                        mychat.chatRoomUid = chatRoomUid;
                        FirebaseDatabase.getInstance().getReference("users").child(stChatid).child("mychat").child(chatRoomUid).setValue(mychat);

                        showRoom(friendname);
                        if(chatRoomUid != null) {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd aa:hh:mm:ss");
                            SimpleDateFormat dfchat = new SimpleDateFormat("aa:hh:mm:ss");
                            String formattedDate = df.format(c.getTime());
                            String formattedDatechat = dfchat.format(c.getTime());
                            ChatModel.Comment comment = new ChatModel.Comment();
                            comment.uid = key;
                            comment.message = stText;
                            comment.time = formattedDatechat;
                            comment.daytime = formattedDate;
                            FirebaseDatabase.getInstance().getReference("chatrooms").child(chatRoomUid).child("comments").child(formattedDate).setValue(comment);
                        }
                        btnSend.setEnabled(true);
    }

    void showRoom(final String mfriendname){ // 채팅글을 업데이트 하는 함수
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mChat = new ArrayList<>();
        mRoomUid = new ArrayList<>();
        mAdapter = new MyAdapter(mChat,key,mfriendname);
        mRecyclerView.setAdapter(mAdapter);
        DatabaseReference myRef;

        if(chatRoomUid != null) {
            myRef = database.getReference("chatrooms").child(chatRoomUid).child("comments");
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ChatModel.Comment comment = dataSnapshot.getValue(ChatModel.Comment.class);

                    mChat.add(comment);
                    mRecyclerView.scrollToPosition(mChat.size() - 1);
                    mAdapter.notifyItemInserted(mChat.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed(){ // 뒤로가기 처리 
            setResult(0);
            finish();
    }
}
