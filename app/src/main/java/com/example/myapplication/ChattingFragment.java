package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class ChattingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;

    FirebaseDatabase database;
    List<Friend> mFriend;
    FriendAdapter mAdapter;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chatting, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_Friend);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFriend = new ArrayList<>();
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // specify an adapter (see also next example)
        mAdapter = new FriendAdapter(mFriend,activity);
        mRecyclerView.setAdapter(mAdapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(myUid).child("mychat");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                    final String Uid = dataSnapshot2.child("Uid").getValue().toString();
                    database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users").child(Uid);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                Friend friend = dataSnapshot.getValue(Friend.class);

                                mFriend.add(friend);
                                mAdapter.notifyItemInserted(mFriend.size()-1);

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
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
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }

    }
}
