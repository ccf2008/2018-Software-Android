package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.PasswordAuthentication;
import java.util.Properties;
import java.util.Random;

import javax.sql.DataSource;

public class Register extends AppCompatActivity implements View.OnClickListener{

    Intent in = getIntent();


        ProgressDialog dialog;
        EditText et;
        GMailSender sender;
        long lastPressed;




        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            Button bt = (Button) this.findViewById(R.id.btnSendEmail);
            bt.setOnClickListener(this);

        }

        @Override
        public void onClick(View arg0) {
            StringBuffer temp = new StringBuffer();
            Random rnd = new Random();
            for (int i = 0; i < 6; i++) {
                int rIndex = rnd.nextInt(3);
                switch (rIndex) {
                    case 0:
                        // a-z
                        temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                        break;
                    case 1:
                        // A-Z
                        temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                        break;
                    case 2:
                        // 0-9
                        temp.append((rnd.nextInt(10)));
                        break;
                }
            }

            String auth_string = temp.toString();
            // TODO Auto-generated method stub
            et = (EditText) this.findViewById(R.id.etschoolnumber);
            String email = et.getText().toString();
            if(email.isEmpty()||email.equals("")||!email.contains("@")||!email.contains(".")){
                Toast.makeText(Register.this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }
            else if(email.substring(email.indexOf("@")).equals("@pusan.ac.kr")) {
                sender = new GMailSender("wlswo3149", "tmonnqijbemqzabf"); // SUBSTITUTE ID PASSWORD
                timeThread(auth_string);

                Intent in = new Intent(Register.this, EmailLink.class);
                in.putExtra("auth_string", auth_string);
                in.putExtra("auth_email", et.getText().toString());
                startActivity(in);
            }
            else{
                Toast.makeText(Register.this, "부산대학교 이메일을 입력해 주세요.( ex) ABC.pusan.ac.kr)", Toast.LENGTH_LONG).show();
            }
        }

        public void timeThread(final String auth_string) {
            dialog = new ProgressDialog(this);
            dialog.setTitle("기다려주세요.");
            dialog.setMessage("인증메일을 보내는 중입니다.");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            new Thread(new Runnable() {

                public void run() {
                    // TODO Auto-generated method stub
                    try {

                        sender.sendMail("PNUM 어플리케이션 인증 이메일입니다.", // subject.getText().toString(),
                                "인증번호 : " + auth_string + "\n" +
                                        "인증번호를 PNUM 어플리케이션에 입력하여 인증을 완료해주세요.", // body.getText().toString(),
                                "wlswo3149@gmail.com", // from.getText().toString(),
                                et.getText().toString() // to.getText().toString()
                        );
                        sleep(3000);


                    } catch (Exception e) {
                        Log.e("SendMail", e.getMessage(), e);
                        Toast.makeText(Register.this, "전송 실패", Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                }

                private void sleep(int i) {
                    // TODO Auto-generated method stub
                }

            }).start();

        }

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - lastPressed < 1500){
            finish();
        }
        lastPressed = System.currentTimeMillis();
    }

    }

