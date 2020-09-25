package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.example.hyeoukloginchat.model.UserModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;



import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class signup extends AppCompatActivity {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private static final int PICK_FROM_ALBUM = 10;
    private EditText email;
    private EditText name;
    private EditText password;
    private Button signup;
    public ImageView profile;
    public static Uri imageuri;
    private static final String TAG = "signup";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); final RelativeLayout loderlayout =(RelativeLayout)findViewById(R.id.loaderLyaout);
       //프로필 이미지사진 갤러리로이동하여 설정
        profile = (ImageView)findViewById(R.id.signup_imageview_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });
        email= (EditText)findViewById(R.id.signup_edittext_email);
        name =(EditText)findViewById(R.id.signup_edittext_name);
        password = (EditText)findViewById(R.id.signup_edittext_password);
        signup = (Button)findViewById(R.id.signup_button_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  //이메일 비밀번호형식이 맞지않거나 사진이 없으면 회원가입불가능
                if (email.getText().toString() == null || name.getText().toString() ==null || password.getText().toString() == null||imageuri==null){
                    return;
                }
                loderlayout.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final String uid = task.getResult().getUser().getUid(); //사용자의 고유한 값 주민번호같은개념  사용자의 uid가져옴

                                FirebaseStorage.getInstance().getReference().child(("userImages")).child(uid).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        @SuppressWarnings("VisibleForTests") final

                                        String imageUrl =task.getResult().getDownloadUrl().toString();   // 프로필사진url을 가져옴
                                        final UserModel userModel = new UserModel();

                                        Date date = new Date();
                                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                                       userModel.time = simpleDateFormat.format(date);
                                        userModel.UserName = name.getText().toString();
                                        userModel.profileImageUrl = imageUrl;
                                        userModel.uid =FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                loderlayout.setVisibility(View.GONE);
                                                signup.this.finish();
                                                Intent intent = new Intent(signup.this,signupfinish.class);
                                                intent.putExtra("id",name.getText().toString()); //회원가입 사용자이름을 회원가입완료화면에 넘겨줌
                                                startActivity(intent);


                                            }
                                        });
                                    }
                                });


                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode ==PICK_FROM_ALBUM&& resultCode==RESULT_OK){
            profile.setImageURI(data.getData()); //가운데 뷰를 바꿈
            imageuri = data.getData();  // 이미지경로원본
        }
    }
}
//https://firebasestorage.googleapis.com/v0/b/hyeoukloginchat.appspot.com/o/userImages%2FbESBCVWM4YUfTgvXLcaHgFVzhOs2?alt=media&token=565c6127-d1c4-4407-8df1-4a492532d188