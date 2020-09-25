package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hyeoukloginchat.loginmainfragment.boardfragment;
import com.example.hyeoukloginchat.loginmainfragment.chatfragment;
import com.example.hyeoukloginchat.loginmainfragment.homefragment;
import com.example.hyeoukloginchat.loginmainfragment.lecturefragment;
import com.example.hyeoukloginchat.loginmainfragment.peoplefragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class loginmain extends AppCompatActivity {
    private long backbtn=0;
 public static   TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {    //메인화면을 프레임아웃으로함
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginmain);
      textView =(TextView)findViewById(R.id.toolbar_title);
        Toolbar toolbar =(Toolbar)findViewById(R.id.hometoolbar);
       setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.loginmain_bottomnavigationview);
        getSupportFragmentManager().beginTransaction().replace(R.id.loginmain_framelayout,new homefragment()).commitAllowingStateLoss();
       bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               switch (menuItem.getItemId()){
                   case  R.id.action_people:
                        textView.setText("친구목록");
                       getSupportFragmentManager().beginTransaction().replace(R.id.loginmain_framelayout,new peoplefragment()).commitAllowingStateLoss();
                       return true;
                   case R.id.action_chat:
                        textView.setText("대화목록");
                       getSupportFragmentManager().beginTransaction().replace(R.id.loginmain_framelayout,new chatfragment()).commitAllowingStateLoss();
                        return true;
                   case R.id.action_home:
                       textView.setText("공지사항");
                       getSupportFragmentManager().beginTransaction().replace(R.id.loginmain_framelayout,new homefragment()).commitAllowingStateLoss();
                       return true;
                   case R.id.action_board:
                       textView.setText("게시판");
                       getSupportFragmentManager().beginTransaction().replace(R.id.loginmain_framelayout,new boardfragment()).commitAllowingStateLoss();
                       return true;
                   case R.id.action_lecture:
                       textView.setText("강좌 서비스");
                       getSupportFragmentManager().beginTransaction().replace(R.id.loginmain_framelayout,new lecturefragment()).commitAllowingStateLoss();
                       return true;
               }
               return false;
           }
       });

    }
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backbtn;
        if (0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        } else {
            backbtn = curTime;
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }


}
