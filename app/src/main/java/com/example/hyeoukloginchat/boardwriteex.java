package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hyeoukloginchat.loginmainfragment.homefragment;
import com.example.hyeoukloginchat.model.BoardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class boardwriteex extends AppCompatActivity {
    private static Uri imageuri=null;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");   //시간표현 포멧방식

                Button image;
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                private static final String TAG = "boardwriteex";


                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_boardwriteex);

                    image =(Button) findViewById(R.id.boardwriteex_image);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            startActivityForResult(intent, 13);
                        }
                    });

                    Button button = (Button) findViewById(R.id.boardwriteex_button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                upload();


            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 13 && resultCode == RESULT_OK) {

            ImageView imageView  =  (ImageView)findViewById(R.id.boardwriteex_imageview);
            imageView.setImageURI(data.getData()); //가운데 뷰를 바꿈
            imageuri= data.getData();  // 이미지경로원본






        }

    }
    private  void upload(){

        final String name = ((EditText) findViewById(R.id.boardwriteex_name)).getText().toString();
        final String title = ((EditText) findViewById(R.id.boardwriteex_title)).getText().toString();
        final String contents = ((EditText) findViewById(R.id.boardwriteex_contents)).getText().toString();
        final String id = FirebaseFirestore.getInstance().collection("notice").document().getId();
        Date date = new Date();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        final String time = simpleDateFormat.format(date);
                if (name.length() > 0 && title.length() > 0 &&imageuri==null) {
                    final   RelativeLayout loderlayout =(RelativeLayout)findViewById(R.id.loaderLyaout);
                    loderlayout.setVisibility(View.VISIBLE);
                    final String imageUrl = null;
                    BoardModel boardModel = new BoardModel(title, contents, name, uid,imageUrl,time,id);
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    firebaseFirestore.collection("notice").document(id).set(boardModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loderlayout.setVisibility(View.GONE);
                            Toast.makeText(boardwriteex.this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                            boardwriteex.this.finish();

                           // startActivity(new Intent(boardwriteex.this,loginmain.class));
                        }
                    })
                      .addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {


                              Toast.makeText(boardwriteex.this, "게시글이 작성되지않았습니다.", Toast.LENGTH_SHORT).show();
                              boardwriteex.this.finish();
                             // startActivity(new Intent(boardwriteex.this,loginmain.class));
                          }
                      });





                }else  if (name.length() > 0 && title.length() > 0 &&imageuri!=null){
                    final   RelativeLayout loderlayout =(RelativeLayout)findViewById(R.id.loaderLyaout);
                    loderlayout.setVisibility(View.VISIBLE);
                    FirebaseStorage.getInstance().getReference().child(("noticeImages")).child(id).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            final String imageUrl =task.getResult().getDownloadUrl().toString();
                            BoardModel boardModel = new BoardModel(title, contents, name, uid,imageUrl,time,id);
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            firebaseFirestore.collection("notice").document(id).set(boardModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loderlayout.setVisibility(View.GONE);
                                    Toast.makeText(boardwriteex.this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                                    boardwriteex.this.finish();
                                   // startActivity(new Intent(boardwriteex.this,loginmain.class));
                                }
                            })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {


                                   loderlayout.setVisibility(View.GONE);
                                   Toast.makeText(boardwriteex.this, "게시글이 작성되지않았습니다.", Toast.LENGTH_SHORT).show();
                                   boardwriteex.this.finish();
                                  // startActivity(new Intent(boardwriteex.this,loginmain.class));
                               }
                           });




                        }
                    });

                }



    }





}
