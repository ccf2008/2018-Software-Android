package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    EditText etEmail;
    EditText etPassword;
    ProgressBar pbLogin;
    String stEmail;
    String stPassword;
    long lastPressed;
    FirebaseDatabase database;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference myRef;
    FirebaseUser user;
    String stname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pbLogin = (ProgressBar)findViewById(R.id.pbLogin);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    SharedPreferences sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("uid", user.getUid());
                    editor.putString("email", user.getEmail());
                    editor.apply();
                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        Button btnRegister = (Button)findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent in = new Intent(MainActivity.this, Register.class);
                startActivity(in);
            }
        });

        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                stEmail = etEmail.getText().toString();
                stPassword = etPassword.getText().toString();
                if(stEmail.isEmpty()||stEmail.equals("")||stPassword.isEmpty()||stPassword.equals("")){
                    Toast.makeText(MainActivity.this, "회원정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                userLogin(stEmail, stPassword);
            }   }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    private void userLogin(final String email,final String password){
        Log.d(TAG, email + password);
        pbLogin.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        pbLogin.setVisibility(View.GONE);

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
                                        if (userModel.email.equals(stEmail)) {
                                            stname = userModel.name;
                                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor autoLogin = auto.edit();
                                            autoLogin.putString("inputId", email);
                                            autoLogin.putString("inputPwd", password);
                                            autoLogin.putString("name",stname);
                                            autoLogin.putString("uid",userModel.key);
                                            autoLogin.commit();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    throw error.toException();
                                }
                            });


                            Intent in = new Intent(MainActivity.this, TabActivity.class);
                            startActivity(in);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - lastPressed < 1500){
            finish();
        }
        Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastPressed = System.currentTimeMillis();
    }
}
