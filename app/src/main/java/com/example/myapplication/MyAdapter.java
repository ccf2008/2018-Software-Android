package com.example.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> { // ItemAdapter, OcItemAdapter에 사용하기 위해 지정된 Adapter
    private String[] mDataset;                                              // RecyclerView의 전체적인 틀을 
    List<ChatModel.Comment> mChat;
    String stUid;
    String friendname;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public TextView timeview;
        public TextView nameview;
        public ViewHolder(View itemview) {
            super(itemview);
            mTextView = (TextView)itemview.findViewById(R.id.mTextView);
            timeview = (TextView)itemview.findViewById(R.id.timeview);
            nameview = (TextView)itemview.findViewById(R.id.nameview);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<ChatModel.Comment> mChat, String Uid, String friendname) {
        this.mChat = mChat;
        this.stUid = Uid;
        this.friendname = friendname;
    }

    @Override
    public int getItemViewType(int position) {
        if(mChat.get(position).uid.equals(stUid)){
            return 1;
        } else {
            return 2;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v;
        if(viewType == 1){
             v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.right_text_view, parent, false);
        } else {
             v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_text_view, parent, false);
        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mTextView.setText(mChat.get(position).message);
        holder.timeview.setText(mChat.get(position).time);
        holder.nameview.setText(friendname);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mChat.size();
    }
}
