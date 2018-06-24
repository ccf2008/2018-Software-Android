package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import static android.view.View.GONE;

public class Selectmyoctionitem extends AppCompatActivity{ // 내 경매 상품 선택 액티비티

    String title;
    String startprice;
    String endprice;
    String recentprice;
    String detail;
    String photo;
    String useruid;
    String friendname;
    String itemkey="";
    String photoUri;
    String number;
    String time;
    String endtime;

    int exifDegree;
    String small_photoUri;

    TextView tvtime;
    TextView tvendtime;
    Spinner spinner;
    TextView tvtitle;
    TextView tvstartprice;
    TextView tvendprice;
    TextView tvrecentprice;
    TextView tvdetail;
    TextView tvbidprice;
    ImageView ivphoto;
    Button btnsave;
    Button btndelete;
    ImageView ivUser;
    private StorageReference mStorageRef;
    Bitmap bitmap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("octionitems");
    ProgressBar pbLogin;
    ProgressDialog dialog;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oc_select_myitem);
        tvtitle = (EditText)findViewById(R.id.tvtitle);
        tvstartprice = (TextView) findViewById(R.id.tvstartprice);
        tvendprice = (TextView)findViewById(R.id.tvendprice);
        tvrecentprice = (TextView)findViewById(R.id.tvrecentprice);
        tvtime = (TextView)findViewById(R.id.tvtime);
        tvendtime = (TextView)findViewById(R.id.tvendtime);
        tvdetail = (EditText)findViewById(R.id.tvdetail);
        ivphoto = (ImageView)findViewById(R.id.ivImage);
        btnsave = (Button)findViewById(R.id.btnsave);
        btndelete = (Button)findViewById(R.id.btndelete);
        pbLogin = (ProgressBar)findViewById(R.id.pbLogin);
        spinner = (Spinner)findViewById(R.id.spcategori);
        tvbidprice = (TextView)findViewById(R.id.recentprice);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Intent in = getIntent();
        itemkey = in.getStringExtra("itemkey");
        number = in.getStringExtra("activity");

        if(ContextCompat.checkSelfPermission(Selectmyoctionitem.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Selectmyoctionitem.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)){

            } else{
                ActivityCompat.requestPermissions(Selectmyoctionitem.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("octionitems").child(itemkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                title = dataSnapshot.child("title").getValue().toString();
                startprice = dataSnapshot.child("startprice").getValue().toString();
                endprice = dataSnapshot.child("endprice").getValue().toString();
                recentprice = dataSnapshot.child("recentprice").getValue().toString();
                detail = dataSnapshot.child("detail").getValue().toString();
                photo = dataSnapshot.child("photo").getValue().toString();
                useruid = dataSnapshot.child("uid").getValue().toString();
                time = dataSnapshot.child("time").getValue().toString();
                endtime = dataSnapshot.child("endtime").getValue().toString();

                if(dataSnapshot.child("soldout").getValue().toString().equals("true")){
                    tvbidprice.setText("낙찰가");
                }
                tvtitle.setText(title);
                tvstartprice.setText(startprice);
                tvendprice.setText(endprice);
                tvrecentprice.setText(recentprice);
                tvdetail.setText(detail);
                tvtime.setText(time);
                tvendtime.setText(endtime);
                Glide.with(Selectmyoctionitem.this).load(photo).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .centerCrop().into(ivphoto);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                myRef.child("users").child(useruid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        friendname = dataSnapshot.child("name").getValue().toString();
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
        ivUser = (ImageView)findViewById(R.id.ivImage);
        ivUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() { // 수정 버튼
            @Override
            public void onClick(View view) {
                if(tvtitle.getText().toString().isEmpty()||tvtitle.getText().toString().equals("")) Toast.makeText(Selectmyoctionitem.this, "상품명을 입력해 주세요..", Toast.LENGTH_SHORT).show();
                else if(tvdetail.getText().toString().isEmpty()||tvdetail.getText().toString().equals("")) Toast.makeText(Selectmyoctionitem.this, "상세설명을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                else {
                    Hashtable<String, Object> item = new Hashtable<String, Object>();
                    item.put("title", tvtitle.getText().toString());
                    item.put("detail", tvdetail.getText().toString());
                    item.put("category", spinner.getSelectedItem().toString());
                    FirebaseDatabase.getInstance().getReference("octionitems").child(itemkey).updateChildren(item);
                    Toast.makeText(Selectmyoctionitem.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                    if(number.equals("0")){
                        Intent intent = new Intent(Selectmyoctionitem.this, TabActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else{
                        Intent intent = new Intent(Selectmyoctionitem.this, Mymarket.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() { // 삭제 버튼
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Selectmyoctionitem.this);
                alert_confirm.setMessage("게시글을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES'
                                FirebaseDatabase.getInstance().getReference("octionitems").child(itemkey).removeValue();
                                FirebaseDatabase.getInstance().getReference("users").child(useruid).child("myOction").child(itemkey).removeValue();

                                StorageReference mStorageRef;
                                mStorageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference mountainsRef = mStorageRef.child("octionitems").child(itemkey+".jpg");
                                mountainsRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(Selectmyoctionitem.this, "삭제 오류", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                mountainsRef = mStorageRef.child("octionitems").child("small" +itemkey+".jpg");
                                mountainsRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Selectmyoctionitem.this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(Selectmyoctionitem.this, "삭제 오류", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                if(number.equals("0")) {
                                    setResult(1);
                                    finish();
                                } else{
                                    Intent intent = new Intent(Selectmyoctionitem.this, Myoction.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }


                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });
    }

    public void uploadimage(){ // 이미지 업로드용 함수
        StorageReference mountainsRef = mStorageRef.child("octionitems").child(itemkey+".jpg");
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap small_bitmap;
        Bitmap big_bitmap;
        if(exifDegree>0){
            Matrix m = new Matrix();
            m.setRotate( exifDegree, (float) width, (float) height );
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true );
            small_bitmap = Bitmap.createScaledBitmap(bitmap, 640 * height / width, 640, true);
            big_bitmap = Bitmap.createScaledBitmap(bitmap, 1024 * height / width, 1024, true);
        } else {
            small_bitmap = Bitmap.createScaledBitmap(bitmap, 640 * width / height, 640, true);
            big_bitmap = Bitmap.createScaledBitmap(bitmap, 1024 * width / height, 1024, true);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        big_bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        ByteArrayOutputStream small_stream = new ByteArrayOutputStream() ;
        small_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, small_stream);
        byte[] small_byteArray = small_stream.toByteArray() ;

        UploadTask uploadTask = mountainsRef.putBytes(byteArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Selectmyoctionitem.this, "업로드 실패", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                photoUri = String.valueOf(downloadUrl);
                Log.d("url", photoUri);

                Hashtable<String, Object> item = new Hashtable<String, Object>();
                item.put("photo", photoUri);


                myRef.child(itemkey).updateChildren(item);
            }
        });
        mountainsRef = mStorageRef.child("octionitems").child("small"+itemkey+".jpg");

        uploadTask = mountainsRef.putBytes(small_byteArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Selectmyoctionitem.this, "업로드 실패", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                small_photoUri = String.valueOf(downloadUrl);
                Log.d("url", small_photoUri);

                Hashtable<String, Object> item = new Hashtable<String, Object>();
                item.put("small_photo", small_photoUri);


                myRef.child(itemkey).updateChildren(item);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String s= dataSnapshot.getValue().toString();
                        Log.d("item", s);
                        if(dataSnapshot != null){
                            pbLogin.setVisibility(GONE);
                            dialog.dismiss();
                            Toast.makeText(Selectmyoctionitem.this, "사진이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Selectmyoctionitem.this, "업로드 실패", Toast.LENGTH_SHORT).show();
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
            final Cursor imageCursor = this.getContentResolver().query(image, filePathColumn, null, null, null);
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
                bitmap = MediaStore.Images.Media.getBitmap(Selectmyoctionitem.this.getContentResolver(), image);
                Glide.with(Selectmyoctionitem.this).load(image).centerCrop().into(ivUser);
                timeThread();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
        }
    }

    public int exifOrientationToDegrees(int exifOrientation) // 이미지 회전 보정용 함수
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
        dialog = new ProgressDialog(Selectmyoctionitem.this);
        dialog.setTitle("기다려주세요.");
        dialog.setMessage("사진을 변경 중입니다.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        pbLogin.setVisibility(View.VISIBLE);

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
    public void onBackPressed(){ // 뒤로가기 
        if(number.equals("0")) {
            setResult(1);
            finish();
        } else{
            Intent intent = new Intent(Selectmyoctionitem.this, Myoction.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}

