package com.example.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class TabActivity extends AppCompatActivity { // 프레그먼트들이 실행되는 바탕 액티비티 

        Fragment fragment;
        long lastPressed;

        private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) { // 프레그먼트 이동
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        switchFragment(fragment);
                        return true;
                    case R.id.navigation_oction:
                        fragment = new OctionFragment();
                        switchFragment(fragment);
                        return true;
                    case R.id.navigation_dashboard:
                        fragment = new ChattingFragment();
                        switchFragment(fragment);
                        return true;
                    case R.id.navigation_notifications:
                        fragment = new ProfileFragment();
                        switchFragment(fragment);
                        return true;
                }
                return false;

            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tab);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            HomeFragment fragment = new HomeFragment();
            fragmentTransaction.add(R.id.content, fragment);
            fragmentTransaction.commit();

            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            passPushTokenToServer();
        }

        public void switchFragment(Fragment fragment){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.content,fragment);

            transaction.commit();
        }

    @Override
    public void onBackPressed(){ // 뒤로가기 처리
        if(System.currentTimeMillis() - lastPressed < 1500){
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastPressed = System.currentTimeMillis();
    }

    void passPushTokenToServer(){ // 사용자의 푸쉬 토큰을 데이터베이스에 업로드 하는 함수
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("pushToken",token);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==0){
            ChattingFragment chattingFragment = new ChattingFragment();
            switchFragment(chattingFragment);
        } else if(resultCode==1){
            Intent intent = new Intent(TabActivity.this, TabActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            OctionFragment octionFragment = new OctionFragment();
            switchFragment(octionFragment);
        }
    }
}
