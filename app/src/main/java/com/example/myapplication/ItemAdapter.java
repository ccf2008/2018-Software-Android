package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    List<Item> mItem;
    List<Item> mItem2;
    String stEmail;
    Context context;
    FirebaseDatabase database;
    String stPhoto;
    String check;
    String itemkey1;
    String itemkey2;
    String writeruid;
    String myUid;
    String number;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public ImageView ivItem;
        public ImageView ivItem2;
        public TextView title;
        public TextView title2;
        public TextView price;
        public TextView price2;
        public Button btnselect1;
        public Button btnselect2;


        public ViewHolder(View itemview) {
            super(itemview);
            ivItem = (ImageView) itemview.findViewById(R.id.ivItem);
            ivItem2 = (ImageView) itemview.findViewById(R.id.ivItem2);

            title = (TextView) itemview.findViewById(R.id.title);
            title2 = (TextView) itemview.findViewById(R.id.title2);

            price = (TextView) itemview.findViewById(R.id.price);
            price2 = (TextView) itemview.findViewById(R.id.price2);

            btnselect1 = (Button) itemview.findViewById(R.id.btnselect1);
            btnselect2 = (Button) itemview.findViewById(R.id.btnselect2);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ItemAdapter(List<Item> mItem, List<Item> mItem2, Context context, String number) {
        this.mItem = mItem;
        this.mItem2 = mItem2;
        this.context = context;
        this.number = number;
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
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v;
        if(viewType==1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_title_item, parent, false);
        } else{
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if(number == "1"){
            SharedPreferences auto = context.getSharedPreferences("myitem", Activity.MODE_PRIVATE);
            check = auto.getString("check", "");
        } else {
            SharedPreferences auto = context.getSharedPreferences("item", Activity.MODE_PRIVATE);
            check = auto.getString("check", "");
        }
        if (check.equals("1")) {
            stPhoto = mItem.get(position).getSmall_photo();
            holder.title.setText(mItem.get(position).getTitle());
            holder.price.setText("가격 : " +mItem.get(position).getPrice()+"원");
            Glide.with(context).load(stPhoto).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .thumbnail(0.1f)
                    .centerCrop().into(holder.ivItem);


            stPhoto = mItem2.get(position).getSmall_photo();
            holder.title2.setText(mItem2.get(position).getTitle());
            if(mItem2.get(position).getPrice().equals("")){
                holder.price2.setText(mItem2.get(position).getPrice());
            }else{
                holder.price2.setText("가격 : " +mItem2.get(position).getPrice()+"원");
            }

            Glide.with(context).load(stPhoto).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .thumbnail(0.1f)
                    .centerCrop().into(holder.ivItem2);
        } else{
            stPhoto = mItem2.get(position).getSmall_photo();
            holder.title.setText(mItem2.get(position).getTitle());
            holder.price.setText("가격 : " +mItem2.get(position).getPrice()+"원");
            Glide.with(context).load(stPhoto).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .thumbnail(0.1f)
                    .centerCrop().into(holder.ivItem);

            stPhoto = mItem.get(position).getSmall_photo();
            holder.title2.setText(mItem.get(position).getTitle());
            holder.price2.setText("가격 : " +mItem.get(position).getPrice()+"원");
            Glide.with(context).load(stPhoto).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .thumbnail(0.1f)
                    .centerCrop().into(holder.ivItem2);
        }

        holder.btnselect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(check.equals("1")){
                    itemkey1 = mItem.get(position).getItemkey();
                    writeruid = mItem.get(position).getUid();
                } else{
                    itemkey1 = mItem2.get(position).getItemkey();
                    writeruid = mItem2.get(position).getUid();
                }

                if(writeruid.equals(myUid)) {
                    Intent in = new Intent(context, Selectmyitem.class);
                    in.putExtra("itemkey", itemkey1);
                    in.putExtra("activity", number);
                    context.startActivity(in);
                } else{
                    Intent in = new Intent(context, Selectitem.class);
                    in.putExtra("itemkey", itemkey1);
                    context.startActivity(in);
                }
            }
        });

        holder.btnselect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(check.equals("1")){
                    itemkey2 = mItem2.get(position).getItemkey();
                    writeruid = mItem2.get(position).getUid();
                } else{
                    itemkey2 = mItem.get(position).getItemkey();
                    writeruid = mItem.get(position).getUid();
                }
                if(itemkey2 != null && !(itemkey2.equals("")) && !(itemkey2.isEmpty())) {
                    if (writeruid.equals(myUid)) {
                        Intent in = new Intent(context, Selectmyitem.class);
                        in.putExtra("itemkey", itemkey2);
                        in.putExtra("activity", number);
                        context.startActivity(in);
                    } else {
                        Intent in = new Intent(context, Selectitem.class);
                        in.putExtra("itemkey", itemkey2);
                        context.startActivity(in);
                    }
                }
            }
        });

    }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount () {
            return mItem.size();
        }
    }
