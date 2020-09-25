package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hyeoukloginchat.model.BoardModel;
import com.example.hyeoukloginchat.model.freeboardmodel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Freeboard_write extends AppCompatActivity {

    private static Uri imageuri=null;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");   //시간표현 포멧방식

    Button image;
    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "freeboardwrite";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeboard_write);

        image =(Button) findViewById(R.id.freeboardwrite_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 13);
            }
        });

        Button button = (Button) findViewById(R.id.freeboardwrite_button);
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

            ImageView imageView  =  (ImageView)findViewById(R.id.freeboardwrite_imageview);
            imageView.setImageURI(data.getData()); //가운데 뷰를 바꿈
            imageuri= data.getData();  // 이미지경로원본






        }

    }
    private  void upload(){

        final String name = ((EditText) findViewById(R.id.freeboardwrite_name)).getText().toString();
        final String title = ((EditText) findViewById(R.id.freeboardwrite_title)).getText().toString();
        final String contents = ((EditText) findViewById(R.id.freeboardwrite_contents)).getText().toString();
        final String id = FirebaseFirestore.getInstance().collection("freeboard").document().getId();
        Date date = new Date();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        final String time = simpleDateFormat.format(date);
        if (name.length() > 0 && title.length() > 0 &&imageuri==null) {
            final RelativeLayout loderlayout =(RelativeLayout)findViewById(R.id.loaderLyaout);
            loderlayout.setVisibility(View.VISIBLE);
            final String imageUrl = null;
            freeboardmodel freeboardmodel = new freeboardmodel(title, contents, name, uid,imageUrl,time,id);
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("freeboard").document(id).set(freeboardmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loderlayout.setVisibility(View.GONE);
                    Toast.makeText(Freeboard_write.this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                    Freeboard_write.this.finish();
                    startActivity(new Intent(Freeboard_write.this,freeboard.class));
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            Toast.makeText(Freeboard_write.this, "게시글이 작성되지않았습니다.", Toast.LENGTH_SHORT).show();
                            Freeboard_write.this.finish();
                            startActivity(new Intent(Freeboard_write.this,freeboard.class));
                        }
                    });





        }else  if (name.length() > 0 && title.length() > 0 &&imageuri!=null){
            final   RelativeLayout loderlayout =(RelativeLayout)findViewById(R.id.loaderLyaout);
            loderlayout.setVisibility(View.VISIBLE);
            FirebaseStorage.getInstance().getReference().child(("freeboardImages")).child(id).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    final String imageUrl =task.getResult().getDownloadUrl().toString();
                    freeboardmodel freeboardmodel = new freeboardmodel(title, contents, name, uid,imageUrl,time,id);
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    firebaseFirestore.collection("freeboard").document(id).set(freeboardmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loderlayout.setVisibility(View.GONE);
                            Toast.makeText(Freeboard_write.this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                            Freeboard_write.this.finish();
                            startActivity(new Intent(Freeboard_write.this,freeboard.class));
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {


                                    loderlayout.setVisibility(View.GONE);
                                    Toast.makeText(Freeboard_write.this, "게시글이 작성되지않았습니다.", Toast.LENGTH_SHORT).show();
                                    Freeboard_write.this.finish();
                                    startActivity(new Intent(Freeboard_write.this,freeboard.class));
                                }
                            });




                }
            });

        }



    }





}
