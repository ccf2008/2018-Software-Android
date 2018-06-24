package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import static java.lang.Thread.sleep;

public class EmailLink extends AppCompatActivity {

    EditText et;
    long lastPressed;
    ProgressDialog dialog;
    GMailSender sender;
    String auth_string;
    String auth_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_link);

        Intent in = getIntent();
        auth_string = in.getStringExtra("auth_string");
        auth_email = in.getStringExtra("auth_email");


        Button btnRegister = (Button)findViewById(R.id.btncheck_auth);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                et = (EditText)findViewById(R.id.etauth_number);

                if(et.getText().toString().equals("")||et.getText().toString().isEmpty()){
                    Toast.makeText(EmailLink.this, "인증문자를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else {

                    if(et.getText().toString().equals(auth_string)){
                        Toast.makeText(EmailLink.this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        et.setText("");
                        Intent in = new Intent(EmailLink.this, Password.class);
                        in.putExtra("auth_email", auth_email);
                        startActivity(in);
                    } else{
                        Toast.makeText(EmailLink.this, "인증 문자가 잘못되었습니다. 다시 확인해 주세요.", Toast.LENGTH_LONG).show();
                    }
                }



            }
        });

        final Button btnreRegister = (Button)findViewById(R.id.btnrecheck);

        btnreRegister.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                btnreRegister.setEnabled(false);
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

                auth_string = temp.toString();
                sender = new GMailSender("wlswo3149", "tmonnqijbemqzabf"); // SUBSTITUTE ID PASSWORD
                timeThread(auth_string, auth_email);
                btnreRegister.setEnabled(true);
                }

        });
    }

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - lastPressed < 1500){
            finish();
        }
        lastPressed = System.currentTimeMillis();
    }

    public void timeThread(final String auth_string, final String auth_email) {

        dialog = new ProgressDialog(EmailLink.this);
        dialog.setTitle("기다려주세요.");
        dialog.setMessage("인증메일을 보내는 중입니다.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                try {

                    sender.sendMail("PNUM 어플리케이션 인증 이메일입니다.", // subject.getText().toString(),
                            "인증번호 : " + auth_string + "\n" +
                                    "인증번호를 PNUM 어플리케이션에서 입력하여 인증을 완료해주세요.", // body.getText().toString(),
                            "wlswo3149@gmail.com", // from.getText().toString(),
                            auth_email// to.getText().toString()
                    );
                    sleep(3000);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                    Toast.makeText(EmailLink.this, "전송 실패", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            private void sleep(int i) {

            }

        }).start();
    }

}
