package com.example.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Mybid extends AppCompatActivity {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    boolean check;
    FirebaseDatabase database;
    List<OcItem> mItem = null;
    List<OcItem> mItem2 = null;
    String myitem_uid;
    BidAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybid);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_item);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(Mybid.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mItem = new ArrayList<>();
        mItem2 = new ArrayList<>();

        final Button btn_1 = (Button)findViewById(R.id.firstbtn);
        btn_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_1.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new BidAdapter(mItem, mItem2,Mybid.this,"1");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("octionitems");
                myRef.orderByChild("bidmankey").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            if(check == true) {
                                OcItem item = dataSnapshot2.getValue(OcItem.class);
                                mItem.add(item);
                                mAdapter.notifyItemInserted(mItem.size() - 1);
                                check = false;
                            } else{
                                OcItem item = dataSnapshot2.getValue(OcItem.class);
                                mItem2.add(item);
                                check = true;
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            OcItem item = new OcItem();
                            mItem2.add(item);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "0");
                            autoedit.commit();
                        }

                        btn_1.setEnabled(true);
                    }


                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                        btn_1.setEnabled(true);
                    }
                });
            }
        });
        final Button btn_2 = (Button)findViewById(R.id.secondbtn);
        btn_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_2.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new BidAdapter(mItem, mItem2,Mybid.this,"1");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("octionitems");
                myRef.orderByChild("bidmankey").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                                for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                                    OcItem item = dataSnapshot2.getValue(OcItem.class);
                                    if(item.getCategory().equals("의류")){
                                        if(check == true) {
                                            mItem.add(item);
                                            mAdapter.notifyItemInserted(mItem.size() - 1);
                                            check = false;
                                        } else{
                                            mItem2.add(item);
                                            check = true;
                                        }
                                    }
                                }
                                if(check == false){
                                    Collections.reverse(mItem);
                                    Collections.reverse(mItem2);
                                    OcItem item = new OcItem();
                                    mItem2.add(item);
                                    SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor autoedit = auto.edit();
                                    autoedit.putString("check", "1");
                                    autoedit.commit();
                                } else{
                                    Collections.reverse(mItem);
                                    Collections.reverse(mItem2);
                                    SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor autoedit = auto.edit();
                                    autoedit.putString("check", "0");
                                    autoedit.commit();
                                }

                                btn_2.setEnabled(true);
                            }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                        btn_2.setEnabled(true);
                    }
                });
            }
        });
        final Button btn_3 = (Button)findViewById(R.id.thirdbtn);
        btn_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_3.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new BidAdapter(mItem, mItem2,Mybid.this,"1");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("octionitems");
                myRef.orderByChild("bidmankey").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            OcItem item = dataSnapshot2.getValue(OcItem.class);
                            if(item.getCategory().equals("디지털")){
                                if(check == true) {
                                    mItem.add(item);
                                    mAdapter.notifyItemInserted(mItem.size() - 1);
                                    check = false;
                                } else{
                                    mItem2.add(item);
                                    check = true;
                                }
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            OcItem item = new OcItem();
                            mItem2.add(item);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "0");
                            autoedit.commit();
                        }

                        btn_3.setEnabled(true);
                    }


                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                        btn_3.setEnabled(true);
                    }
                });
            }
        });
        final Button btn_4 = (Button)findViewById(R.id.fourthbtn);
        btn_4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_4.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new BidAdapter(mItem, mItem2,Mybid.this,"1");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("octionitems");
                myRef.orderByChild("bidmankey").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            OcItem item = dataSnapshot2.getValue(OcItem.class);
                            if(item.getCategory().equals("도서")){
                                if(check == true) {
                                    mItem.add(item);
                                    mAdapter.notifyItemInserted(mItem.size() - 1);
                                    check = false;
                                } else{
                                    mItem2.add(item);
                                    check = true;
                                }
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            OcItem item = new OcItem();
                            mItem2.add(item);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "0");
                            autoedit.commit();
                        }

                        btn_4.setEnabled(true);
                    }


                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                        btn_4.setEnabled(true);
                    }
                });
            }
        });
        final Button btn_5 = (Button)findViewById(R.id.fifthbtn);
        btn_5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_5.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new BidAdapter(mItem, mItem2,Mybid.this,"1");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("octionitems");
                myRef.orderByChild("bidmankey").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            OcItem item = dataSnapshot2.getValue(OcItem.class);
                            if(item.getCategory().equals("완구")){
                                if(check == true) {
                                    mItem.add(item);
                                    mAdapter.notifyItemInserted(mItem.size() - 1);
                                    check = false;
                                } else{
                                    mItem2.add(item);
                                    check = true;
                                }
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            OcItem item = new OcItem();
                            mItem2.add(item);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "0");
                            autoedit.commit();
                        }

                        btn_5.setEnabled(true);
                    }


                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                        btn_5.setEnabled(true);
                    }
                });
            }
        });
        final Button btn_6 = (Button)findViewById(R.id.sixthbtn);
        btn_6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_6.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new BidAdapter(mItem, mItem2,Mybid.this,"1");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("octionitems");
                myRef.orderByChild("bidmankey").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            OcItem item = dataSnapshot2.getValue(OcItem.class);
                            if(item.getCategory().equals("스포츠")){
                                if(check == true) {
                                    mItem.add(item);
                                    mAdapter.notifyItemInserted(mItem.size() - 1);
                                    check = false;
                                } else{
                                    mItem2.add(item);
                                    check = true;
                                }
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            OcItem item = new OcItem();
                            mItem2.add(item);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "0");
                            autoedit.commit();
                        }

                        btn_6.setEnabled(true);
                    }


                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                        btn_6.setEnabled(true);
                    }
                });
            }
        });
        final Button btn_7 = (Button)findViewById(R.id.seventhbtn);
        btn_7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_7.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new BidAdapter(mItem, mItem2,Mybid.this,"1");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("octionitems");
                myRef.orderByChild("bidmankey").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            OcItem item = dataSnapshot2.getValue(OcItem.class);
                            if(item.getCategory().equals("오피스")){
                                if(check == true) {
                                    mItem.add(item);
                                    mAdapter.notifyItemInserted(mItem.size() - 1);
                                    check = false;
                                } else{
                                    mItem2.add(item);
                                    check = true;
                                }
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            OcItem item = new OcItem();
                            mItem2.add(item);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "0");
                            autoedit.commit();
                        }

                        btn_7.setEnabled(true);
                    }


                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                        btn_7.setEnabled(true);
                    }
                });
            }
        });

        check = true;
        mItem.clear();
        mItem2.clear();
        mAdapter = new BidAdapter(mItem, mItem2,Mybid.this,"1");
        mRecyclerView.setAdapter(mAdapter);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("octionitems");
        myRef.orderByChild("bidmankey").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                                if(check == true) {
                                    OcItem item = dataSnapshot2.getValue(OcItem.class);
                                    mItem.add(item);
                                    mAdapter.notifyItemInserted(mItem.size() - 1);
                                    check = false;
                                } else{
                                    OcItem item = dataSnapshot2.getValue(OcItem.class);
                                    mItem2.add(item);
                                    check = true;
                                }
                            }
                            if(check == false){
                                Collections.reverse(mItem);
                                Collections.reverse(mItem2);
                                OcItem item = new OcItem();
                                mItem2.add(item);
                                SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoedit = auto.edit();
                                autoedit.putString("check", "1");
                                autoedit.commit();
                            } else{
                                Collections.reverse(mItem);
                                Collections.reverse(mItem2);
                                SharedPreferences auto = getSharedPreferences("biditem", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoedit = auto.edit();
                                autoedit.putString("check", "0");
                                autoedit.commit();
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                }
}
