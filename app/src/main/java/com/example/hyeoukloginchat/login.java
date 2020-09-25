package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
   private long backbtn=0;
    private EditText id;
    private EditText password;
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
  private Button login;
  private Button signup2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       final  RelativeLayout loderlayout =(RelativeLayout)findViewById(R.id.loaderLyaout);
      login =(Button)findViewById(R.id.login_button_login);
      signup2= (Button)findViewById(R.id.login_button_signup);
      id=(EditText)findViewById(R.id.login_edittext_id);
      password=(EditText)findViewById(R.id.login_edittext_password);
        firebaseAuth =FirebaseAuth.getInstance();
        firebaseAuth.signOut();
      login.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              loderlayout.setVisibility(View.VISIBLE);
              loginEvent();
          }
      });

      signup2.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              startActivity(new Intent(login.this,signup.class));
          }
      });

        //로그인 인터페이스 리스너  //로그인이됬는지 확인
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){
                    //로그인
                    loderlayout.setVisibility(View.GONE);
                    login.this.finish();
                    Intent intent = new Intent(login.this,loginmain.class);
                    startActivity(intent);

                }else{
                    //로그아웃
                }
            }
        };


    }
    void loginEvent(){ //로그인 성공여부 메소드
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if(!task.isSuccessful()){
                   //로그인 실패
                   Toast.makeText(login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
               }
            }
        });


    }
    //로그인 인터페이스를 붙임
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        long curTime =System.currentTimeMillis();
        long gapTime = curTime - backbtn;
        if(0<=gapTime && 2000>=gapTime){
            super.onBackPressed();
        }else{
            backbtn =curTime;
            Toast.makeText(this,"뒤로 버튼을 한번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show();
        }

    }

}
