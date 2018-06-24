package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import static android.view.View.GONE;

public class RegistItem extends AppCompatActivity{ // 장터 상품등록 액티비티


    String title;
    String price;
    String detail;
    String photo;
    ImageView ivUser;
    String key;
    String photoUri;
    String small_photoUri;
    String itemuid;
    String category;
    int exifDegree;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("items");
    DatabaseReference Ref = myRef.push();

    private StorageReference mStorageRef;
    Bitmap bitmap;
    ProgressBar pbLogin;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_item);
        pbLogin = (ProgressBar)findViewById(R.id.pbLogin);
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        key = auto.getString("uid", "");
        Intent in = getIntent();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        if(ContextCompat.checkSelfPermission(RegistItem.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegistItem.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)){

            } else{
                ActivityCompat.requestPermissions(RegistItem.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

        Button btnfianlregister = (Button)findViewById(R.id.btnfinalregister);
        btnfianlregister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                EditText ettitle = (EditText)findViewById(R.id.ettitle);
                EditText etprice = (EditText)findViewById(R.id.etprice);
                EditText etdetail = (EditText)findViewById(R.id.etdetail);
                ImageView etimage = (ImageView)findViewById(R.id.ivImage);
                Spinner spinner = (Spinner)findViewById(R.id.spcategori);


                if(ettitle.getText().toString().isEmpty()||ettitle.getText().toString().equals("")) Toast.makeText(RegistItem.this, "상품명을 입력해 주세요..", Toast.LENGTH_SHORT).show();
                else if(etprice.getText().toString().isEmpty()||etprice.getText().toString().equals("")) Toast.makeText(RegistItem.this, "가격을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                else if(etdetail.getText().toString().isEmpty()||etdetail.getText().toString().equals("")) Toast.makeText(RegistItem.this, "상세설명을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                else if(photoUri==null) Toast.makeText(RegistItem.this, "사진을 등록해 주세요.", Toast.LENGTH_SHORT).show();
                else {
                    title = ettitle.getText().toString();
                    price = etprice.getText().toString();
                    detail = etdetail.getText().toString();
                    photo = etimage.toString();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar c = Calendar.getInstance();
                    String formattedDate = df.format(c.getTime());
                    c.add(Calendar.HOUR,48);
                    String formattedDate2 = df.format(c.getTime());
                    category = spinner.getSelectedItem().toString();



                    Hashtable<String, String> item = new Hashtable<String, String>();
                    item.put("title", title);
                    item.put("price", price);
                    item.put("detail", detail);
                    item.put("uid", key);
                    item.put("photo",photoUri);
                    item.put("small_photo",small_photoUri);
                    item.put("time",formattedDate);
                    item.put("itemkey", itemuid);
                    item.put("category",category);
                    FirebaseDatabase.getInstance().getReference("items").child(itemuid).setValue(item);

                    Hashtable<String, String> itemid = new Hashtable<String, String>();
                    itemid.put("Uid",itemuid);
                    FirebaseDatabase.getInstance().getReference("users").child(key).child("myitem").child(itemuid).setValue(itemid);


                    Intent in = new Intent(RegistItem.this, TabActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);

                    Toast.makeText(RegistItem.this, "상품 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
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

    }

    public void uploadimage(){ // 이미지 업로드용 함수
        itemuid = Ref.getKey().toString();
        StorageReference mountainsRef = mStorageRef.child("items").child(itemuid+".jpg");
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
                Toast.makeText(RegistItem.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
                pbLogin.setVisibility(GONE);
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                photoUri = String.valueOf(downloadUrl);
                Log.d("url", photoUri);
            }
        });
        mountainsRef = mStorageRef.child("items").child("small"+itemuid+".jpg");
        uploadTask = mountainsRef.putBytes(small_byteArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(RegistItem.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
                pbLogin.setVisibility(GONE);
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                small_photoUri = String.valueOf(downloadUrl);
                Log.d("url", small_photoUri);
                Toast.makeText(RegistItem.this, "사진 업로드 성공", Toast.LENGTH_SHORT).show();
                pbLogin.setVisibility(GONE);
                dialog.dismiss();
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
                Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            try {
                bitmap = MediaStore.Images.Media.getBitmap(RegistItem.this.getContentResolver(), image);
                Glide.with(RegistItem.this).load(image).centerCrop().into(ivUser);
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
        dialog = new ProgressDialog(RegistItem.this);
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
    public void onBackPressed(){ // 뒤로가기 처리
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(RegistItem.this);
        alert_confirm.setMessage("작성을 취소하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES'
                        if(itemuid != null) {
                            StorageReference mStorageRef;
                            mStorageRef = FirebaseStorage.getInstance().getReference();
                            StorageReference mountainsRef = mStorageRef.child("items").child(itemuid + ".jpg");
                            mountainsRef.delete();
                            mountainsRef = mStorageRef.child("items").child("small" +itemuid + ".jpg");
                            mountainsRef.delete();
                            finish();
                        } else{
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

}
