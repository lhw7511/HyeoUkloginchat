package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

private LinearLayout linearLayout;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //앱네임을 숨김
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        linearLayout =(LinearLayout)findViewById(R.id.splashlinearlayout);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.default_config);

        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {

                        }
                        displayMessage();
                    }
                });


    }



    void displayMessage() {   //매개변수가 트루가되면 서버가 닫힘 원격으로 콘솔에서 조정가능 또는  xml파일 default_congfig에서 가능
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        boolean caps = mFirebaseRemoteConfig.getBoolean("splash_message_caps");
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");

        linearLayout.setBackgroundColor(Color.parseColor(splash_background));

        if(caps){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(splash_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            builder.create().show();

        }else{
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try{
                            sleep(1000);
                            MainActivity.this.finish();
                            startActivity(new Intent(getApplicationContext(),loginmain.class));
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                };

               thread.start();



            }
            else{
                Thread thread2 = new Thread(){
                    @Override
                    public void run() {
                        try{
                            sleep(2000);
                            MainActivity.this.finish();
                            startActivity(new Intent(getApplicationContext(),login.class));
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                };

                thread2.start();


            }


        }



    }


}

