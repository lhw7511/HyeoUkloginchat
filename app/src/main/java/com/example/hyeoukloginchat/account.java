package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hyeoukloginchat.model.AccountModel;
import com.example.hyeoukloginchat.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class account extends AppCompatActivity {
  final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static Uri imageuri;
    private TextView textView;
    private ImageView imageView;
   private  TextView comments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Button button = (Button) findViewById(R.id.account_dialong);
        Button profile =(Button)findViewById(R.id.account_button_frofilechange);
        imageView = (ImageView) findViewById(R.id.account_profile);
       textView=(TextView)findViewById(R.id.account_name);
       comments=(TextView)findViewById(R.id.account_comments) ;
    FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            AccountModel accountModel = dataSnapshot.getValue(AccountModel.class);
            Glide.with(getApplicationContext())
                    .load(accountModel.profileImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(imageView);
                 textView.setText(accountModel.UserName);
                comments.setText(accountModel.comment);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 11);

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder alert_confirm = new androidx.appcompat.app.AlertDialog.Builder(account.this);
                alert_confirm.setMessage("프로필 사진을 변경하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseStorage.getInstance().getReference().child(("userImages")).child(uid).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                                final String imageUrl =task.getResult().getDownloadUrl().toString();   //프로필사진url을 가져옴

                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("profileImageUrl").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                     Toast.makeText(getApplicationContext(),"프로필 사진이 변경되었습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }
                });
                alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert_confirm.show();




            } });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdialog(view.getContext());
            }
        });



    }


    void showdialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_comment, null);
        //상태메세지를 가져옴.
        final EditText editText = (EditText) view.findViewById(R.id.dialog_comment_edittext_message);
        editText.setText(comments.getText().toString());
        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            //상태메세지를 comment를 uid를 통해 디비에 저장 users에 업데이트시킴
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, Object> stringObjectMap = new HashMap<>();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                stringObjectMap.put("comment", editText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(stringObjectMap);

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 11 && resultCode == RESULT_OK) {
            imageView.setImageURI(data.getData()); //가운데 뷰를 바꿈
            imageuri = data.getData();  // 이미지경로원본
        }

    }



}
