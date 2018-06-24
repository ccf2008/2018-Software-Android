package com.example.myapplication;

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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Hashtable;

public class Password extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String TAG = "MainActivity";
    ProgressBar pbLogin;
    DatabaseReference myRef;
    long lastPressed;

    String stEmail;
    String stPassword;
    String strePassword;
    String stschoolnumber;
    String stname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Intent in = getIntent();
        final String auth_email = in.getStringExtra("auth_email");


        pbLogin = (ProgressBar)findViewById(R.id.pbLogin);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        mAuth = FirebaseAuth.getInstance();

        Button btnRegister = (Button)findViewById(R.id.btnfinalregister);

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText etschoolnumber = (EditText)findViewById(R.id.etregist_schoolnum);
                EditText etname = (EditText)findViewById(R.id.etregist_name);
                EditText etPassword = (EditText)findViewById(R.id.etregist_password);
                EditText etrePassword = (EditText)findViewById(R.id.etregist_repassword);

                if(etschoolnumber.getText().toString().isEmpty()||etschoolnumber.getText().toString().equals("")) Toast.makeText(Password.this, "학번을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                else if(etname.getText().toString().isEmpty()||etname.getText().toString().equals("")) Toast.makeText(Password.this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                else if(etrePassword.getText().toString().isEmpty()||etrePassword.getText().toString().equals("")||etPassword.getText().toString().isEmpty()||etPassword.getText().toString().equals("")){
                    Toast.makeText(Password.this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (etPassword.getText().toString().equals(etrePassword.getText().toString())) {
                        stEmail = auth_email;
                        stschoolnumber = etschoolnumber.getText().toString();
                        stname = etname.getText().toString();
                        stPassword = etPassword.getText().toString();
                        strePassword = etrePassword.getText().toString();
                        registerUser(stEmail, stPassword, stschoolnumber, stname);

                    } else {
                        Toast.makeText(Password.this, "비밀번호를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }


    public void registerUser(String email, String password, final String stschoolnumber, final String stname){
        pbLogin.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        pbLogin.setVisibility(View.GONE);
                        if(!task.isSuccessful()){
                            Log.d(TAG,"Sign Up Failed");
                            Toast.makeText(Password.this, "회원가입이 실패했습니다.",
                                    Toast.LENGTH_SHORT).show();
                        } else{
                            Log.d(TAG,"Sign Up Success");
                            FirebaseUser user = task.getResult().getUser();
                            if(user != null) {
                                Hashtable<String, String> profile = new Hashtable<String, String>();
                                profile.put("email", user.getEmail());
                                profile.put("photo", "");
                                profile.put("key",user.getUid());
                                profile.put("schoolid",stschoolnumber);
                                profile.put("name",stname);
                                FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).setValue(profile);

                                Intent in = new Intent(Password.this,MainActivity.class);
                                startActivity(in);

                                Toast.makeText(Password.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - lastPressed < 1500){
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastPressed = System.currentTimeMillis();
    }
}
