package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class SplashActivity extends Activity { // 어플리케이션 구동 시 로딩 액티비티 
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    String stname;
    String key;
    String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String loginId = auto.getString("inputId","");
        String loginPwd = auto.getString("inputPwd","");

        if(!loginId.equals("") && !loginPwd.equals("")) { // 이미 로그인 경력이 있는 경우 자동 로그인
            userLogin(loginId,loginPwd);
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void userLogin(final String email,final String password){ // 로그인 함수
        Log.d(TAG, email + password);
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            DatabaseReference myRef;
                            database = FirebaseDatabase.getInstance();
                            myRef = database.getReference("users");
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot item : dataSnapshot.getChildren()) {
                                        UserModel userModel = item.getValue(UserModel.class);
                                        if (userModel.email.equals(email)) {
                                            stname = userModel.name;
                                            key = userModel.key;
                                            photo = userModel.photo;

                                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor autoLogin = auto.edit();
                                            autoLogin.putString("inputId", email);
                                            autoLogin.putString("inputPwd", password);
                                            autoLogin.putString("name",stname);
                                            autoLogin.putString("uid",key);
                                            autoLogin.putString("photo",photo);
                                            autoLogin.commit();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    throw error.toException();
                                }
                            });


                            Intent in = new Intent(SplashActivity.this, TabActivity.class);
                            startActivity(in);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SplashActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(in);
                        }

                    }
                });
    }
}
