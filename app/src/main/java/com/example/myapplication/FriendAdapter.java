package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    public List<Friend> mFriend;
    public String myuid;
    public Context context;

    public FirebaseDatabase database;
    public String lastmessage;
    public String lasttime;
    public List<String> mRoomuid = new ArrayList<>();



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvEmail;
        public ImageView ivUser;
        public Button btnChat;
        public TextView tvlastmessage;
        public TextView tvlasttime;

        public ViewHolder(View itemview) {
            super(itemview);
            tvEmail = (TextView)itemview.findViewById(R.id.tvEmail);
            ivUser = (ImageView)itemview.findViewById(R.id.ivUser);
            btnChat = (Button)itemview.findViewById(R.id.btnChat);
            tvlastmessage = (TextView)itemview.findViewById(R.id.tvlastmessage);
            tvlasttime = (TextView)itemview.findViewById(R.id.tvlasttime);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendAdapter(List<Friend> mFriend, Context context) {
        this.mFriend = mFriend;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 1;
        } else {
            return 2;
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v;
        if(viewType==1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_title_friend, parent, false);
        } else{
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_friend, parent, false);
        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String stFriendid = mFriend.get(position).key;
        final String friendname = mFriend.get(position).name;
        mRoomuid.clear();
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;

        myRef = database.getReference("users").child(myuid).child("mychat");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                    ChatModel.Mychat item = dataSnapshot2.getValue(ChatModel.Mychat.class);
                    if (item.Uid.equals(stFriendid)) {
                        mRoomuid.add(item.chatRoomUid);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef;
                        myRef = database.getReference("chatrooms").child(mRoomuid.get(position)).child("comments");
                        myRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                    ChatModel.Comment item = dataSnapshot2.getValue(ChatModel.Comment.class);
                                    lastmessage = item.message.toString();
                                    lasttime = item.daytime.toString();
                                }
                                String stPhoto = mFriend.get(position).photo;
                                if(stPhoto == ""|| TextUtils.isEmpty(stPhoto)){
                                    Glide.with(context).load(R.mipmap.ic_nophoto_round).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                            .into(holder.ivUser);
                                } else{
                                    Glide.with(context).load(stPhoto).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                            .thumbnail(0.1f).placeholder(R.mipmap.ic_nophoto_round).error(R.mipmap.ic_nophoto_round).into(holder.ivUser);
                                }
                                holder.tvEmail.setText(friendname);
                                holder.tvlastmessage.setText(lastmessage);
                                holder.tvlasttime.setText(lasttime);
                                lasttime = "";
                                lastmessage = "";


                            }


                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                            }
                        });
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });





        holder.btnChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent in = new Intent(context,ChatActivity.class);
                in.putExtra("friendUid", stFriendid);
                in.putExtra("chatRoomUid", mRoomuid.get(position) );
                in.putExtra("friendname",friendname);
                ((Activity)context).startActivityForResult(in,1);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mFriend.size();
    }
}