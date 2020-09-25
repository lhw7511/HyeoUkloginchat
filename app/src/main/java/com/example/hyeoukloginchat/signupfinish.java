package com.example.hyeoukloginchat;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;



public class signupfinish extends AppCompatActivity {
  public  static  String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupfinish);
        TextView textView  =(TextView)findViewById(R.id.signupfinish_textview_username);
        textView.setText(getIntent().getStringExtra("id"));//signup에서 넘겨준 사용자이름을 받아와서 텍스트로설정








    }
}
