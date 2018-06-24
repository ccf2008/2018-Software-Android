package com.example.myapplication;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.view.View.GONE;


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    String TAG = getClass().getSimpleName();
    ImageView ivUser;
    private StorageReference mStorageRef;
    Bitmap bitmap;
    String stUid;
    String stEmail;
    ProgressBar pbLogin;
    TextView name;
    TextView email;
    String stname;
    ProgressDialog dialog;
    int exifDegree;
    private Activity activity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile,container,false);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        name = (TextView) v.findViewById(R.id.tvname);
        email = (TextView) v.findViewById(R.id.tvemail);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("email", Context.MODE_PRIVATE);
        stUid = sharedPreferences.getString("uid", "");
        stEmail = sharedPreferences.getString("email", "");
        email.setText(stEmail);
        SharedPreferences userinfo = getActivity().getSharedPreferences("auto", Context.MODE_PRIVATE);
        stname = userinfo.getString("name", "");
        name.setText(stname);

        pbLogin = (ProgressBar)v.findViewById(R.id.pbLogin);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("users").child(stUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue().toString();
                String stPhoto = dataSnapshot.child("photo").getValue().toString();

                if(stPhoto == ""||TextUtils.isEmpty(stPhoto)){
                    pbLogin.setVisibility(GONE);
                } else{
                    pbLogin.setVisibility(View.VISIBLE);
                    Glide.with(activity).load(stPhoto).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        pbLogin.setVisibility(GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        pbLogin.setVisibility(GONE);
                                        return false;
                                    }
                                })
                            .into(ivUser);
                    Log.d(TAG, "Value is: " + value);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                pbLogin.setVisibility(GONE);
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)){

            } else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

        ivUser = (ImageView)v.findViewById(R.id.ivUser);
        ivUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
            }
        });
        Button btnMymarket = (Button)v.findViewById(R.id.btnMymarket);
        btnMymarket.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent in = new Intent(getActivity(), Mymarket.class);
                startActivity(in);
            }
        });

        Button btnMyoction = (Button)v.findViewById(R.id.btnMyoction);
        btnMyoction.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent in = new Intent(getActivity(), Myoction.class);
                startActivity(in);
            }
        });

        Button btnLogout = (Button)v.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SharedPreferences auto = getActivity().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                editor.clear();
                editor.commit();

                FirebaseAuth.getInstance().signOut();
                Intent in = new Intent(getActivity(), MainActivity.class);
                startActivity(in);
            }
        });

        Button btnmybid = (Button)v.findViewById(R.id.btnMybid);
        btnmybid.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent in = new Intent(getActivity(), Mybid.class);
                startActivity(in);
            }
        });

        return v;
    }

    public void uploadimage(){
        StorageReference mountainsRef = mStorageRef.child("users").child(stUid+".jpg");
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(exifDegree>0){
            Matrix m = new Matrix();
            m.setRotate( exifDegree, (float) width, (float) height );
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true );
            bitmap = Bitmap.createScaledBitmap(bitmap, 640 * height / width, 640, true);
        } else {
            bitmap = Bitmap.createScaledBitmap(bitmap, 640 * width / height, 640, true);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;


        UploadTask uploadTask = mountainsRef.putBytes(byteArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "업로드 실패", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String photoUri = String.valueOf(downloadUrl);
                Log.d("url", photoUri);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");

                Hashtable<String, Object> profile = new Hashtable<String, Object>();
                profile.put("photo", photoUri);

                myRef.child(stUid).updateChildren(profile);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String s= dataSnapshot.getValue().toString();
                        Log.d("Profile", s);
                        if(dataSnapshot != null){
                            pbLogin.setVisibility(GONE);
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "업로드 완료", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode==1) {
            Uri image = data.getData();
            final String[] filePathColumn = {MediaStore.Images.Media.DATA};
            final Cursor imageCursor = getActivity().getContentResolver().query(image, filePathColumn, null, null, null);
            imageCursor.moveToFirst();
            final int columnIndex = imageCursor.getColumnIndex(filePathColumn[0]);
            final String imagePath = imageCursor.getString(columnIndex);
            imageCursor.close();

            try{
                ExifInterface exif = new ExifInterface(imagePath);
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
                Glide.with(getActivity()).load(image).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).into(ivUser);
                timeThread();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
        }
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case 1: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                } else{

                }
                return;
            }
        }
    }

    public void timeThread() {

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("기다려주세요.");
        dialog.setMessage("사진을 업로드 중입니다.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                uploadimage();
            }

            private void sleep(int i) {

            }

        }).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }

    }
}
