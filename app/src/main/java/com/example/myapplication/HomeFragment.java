package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class HomeFragment extends Fragment { // 장터용 프래그먼트
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    boolean check;
    FirebaseDatabase database;
    List<Item> mItem = null;
    List<Item> mItem2 = null;
    private Activity activity;

    ItemAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_item, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_item);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mItem = new ArrayList<>();
        mItem2 = new ArrayList<>();

        final Button btn_1 = (Button)v.findViewById(R.id.firstbtn); // 여기서부터 카테고리의 7가지 분류용 버튼, 버튼을 누르면 분류에 맞는 상품만 보여준다
        btn_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_1.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new ItemAdapter(mItem, mItem2,activity,"0");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("items");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            if(check == true) {
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem.add(item);
                                mAdapter.notifyItemInserted(mItem.size() - 1);
                                check = false;
                            } else{
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem2.add(item);
                                check = true;
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            Item item = new Item();
                            mItem2.add(item);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
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
        final Button btn_2 = (Button)v.findViewById(R.id.secondbtn);
        btn_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_2.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new ItemAdapter(mItem, mItem2,activity,"0");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("items");
                myRef.orderByChild("category").equalTo("의류").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            if(check == true) {
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem.add(item);
                                mAdapter.notifyItemInserted(mItem.size() - 1);
                                check = false;
                            } else{
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem2.add(item);
                                check = true;
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            Item item = new Item();
                            mItem2.add(item);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
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
        final Button btn_3 = (Button)v.findViewById(R.id.thirdbtn);
        btn_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_3.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new ItemAdapter(mItem, mItem2,activity,"0");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("items");
                myRef.orderByChild("category").equalTo("디지털").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            if(check == true) {
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem.add(item);
                                mAdapter.notifyItemInserted(mItem.size() - 1);
                                check = false;
                            } else{
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem2.add(item);
                                check = true;
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            Item item = new Item();
                            mItem2.add(item);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
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
        final Button btn_4 = (Button)v.findViewById(R.id.fourthbtn);
        btn_4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_4.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new ItemAdapter(mItem, mItem2,activity,"0");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("items");
                myRef.orderByChild("category").equalTo("도서").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            if(check == true) {
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem.add(item);
                                mAdapter.notifyItemInserted(mItem.size() - 1);
                                check = false;
                            } else{
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem2.add(item);
                                check = true;
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            Item item = new Item();
                            mItem2.add(item);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
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
        final Button btn_5 = (Button)v.findViewById(R.id.fifthbtn);
        btn_5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_5.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new ItemAdapter(mItem, mItem2,activity,"0");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("items");
                myRef.orderByChild("category").equalTo("완구").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            if(check == true) {
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem.add(item);
                                mAdapter.notifyItemInserted(mItem.size() - 1);
                                check = false;
                            } else{
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem2.add(item);
                                check = true;
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            Item item = new Item();
                            mItem2.add(item);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
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
        final Button btn_6 = (Button)v.findViewById(R.id.sixthbtn);
        btn_6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_6.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new ItemAdapter(mItem, mItem2,activity,"0");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("items");
                myRef.orderByChild("category").equalTo("스포츠").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            if(check == true) {
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem.add(item);
                                mAdapter.notifyItemInserted(mItem.size() - 1);
                                check = false;
                            } else{
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem2.add(item);
                                check = true;
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            Item item = new Item();
                            mItem2.add(item);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
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
        final Button btn_7 = (Button)v.findViewById(R.id.seventhbtn);
        btn_7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_7.setEnabled(false);
                check = true;
                mItem.clear();
                mItem2.clear();
                mAdapter = new ItemAdapter(mItem, mItem2,activity,"0");
                mRecyclerView.setAdapter(mAdapter);
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("items");
                myRef.orderByChild("category").equalTo("오피스").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                            if(check == true) {
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem.add(item);
                                mAdapter.notifyItemInserted(mItem.size() - 1);
                                check = false;
                            } else{
                                Item item = dataSnapshot2.getValue(Item.class);
                                mItem2.add(item);
                                check = true;
                            }
                        }
                        if(check == false){
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            Item item = new Item();
                            mItem2.add(item);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoedit = auto.edit();
                            autoedit.putString("check", "1");
                            autoedit.commit();
                        } else{
                            Collections.reverse(mItem);
                            Collections.reverse(mItem2);
                            SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
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

        Button btn_register = (Button) v.findViewById(R.id.btn_register); // 장터 상품 등록 버튼
        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent in = new Intent(activity, RegistItem.class);
                startActivity(in);
            }
        });

        check = true;
        mAdapter = new ItemAdapter(mItem, mItem2,activity,"0");
        mRecyclerView.setAdapter(mAdapter);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("items");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() { // 데이터베이스로부터 장터 상품의 정보를 읽어서 어댑터로 보내는 함수
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){ // 두개의 리스트에 번갈아 가면서 정보를 담는다
                    if(check == true) {
                        Item item = dataSnapshot2.getValue(Item.class);
                        mItem.add(item);
                        mAdapter.notifyItemInserted(mItem.size() - 1);
                        check = false;
                    } else{
                        Item item = dataSnapshot2.getValue(Item.class);
                        mItem2.add(item);
                        check = true;
                    }
                }
                if(check == false){ // 상품의 개수가 홀수일 경우
                    Collections.reverse(mItem);
                    Collections.reverse(mItem2);
                    Item item = new Item();
                    mItem2.add(item);
                        SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor autoedit = auto.edit();
                        autoedit.putString("check", "1"); 
                        autoedit.commit();
                } else{ // 상품의 개수가 짝수일 경우
                    Collections.reverse(mItem);
                    Collections.reverse(mItem2);
                        SharedPreferences auto = activity.getSharedPreferences("item", Activity.MODE_PRIVATE);
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

        return v;
    }




    @Override
    public void onAttach(Context context) { // 예외 처리
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }

    }



}
