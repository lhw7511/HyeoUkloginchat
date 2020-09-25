package com.example.hyeoukloginchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class freeboardcontents extends AppCompatActivity {

    private TextView name, title, time, contents;
    private ImageView imageView;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private  String id;

    private static final String TAG = "boardex";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticecontents);
        contents =(TextView)findViewById(R.id.notice_contents);
        time = (TextView)findViewById(R.id.notice_time);
        title = (TextView)findViewById(R.id.notice_title);
        name =(TextView)findViewById(R.id.notice_username);
        imageView=(ImageView)findViewById(R.id.notice_image);
        final Intent getintent = getIntent();
        id = getintent.getStringExtra("freeboarduid");

        firebaseFirestore.collection("freeboard").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult()!=null){
                        Map<String,Object> snap  =task.getResult().getData();
                        String mtitle  =String.valueOf(snap.get("title"));
                        String mtime  =String.valueOf(snap.get("time"));
                        String mcontents  =String.valueOf(snap.get("contents"));
                        String mname  =String.valueOf(snap.get("name"));
                        String mimageurl  =String.valueOf(snap.get("imageurl"));
                        name.setText(mname);
                        title.setText(mtitle);
                        time.setText(mtime);
                        contents.setText(mcontents);
                        Glide.with(getApplicationContext())
                                .load(mimageurl)
                                .into(imageView);
                    }else{
                        Toast.makeText(freeboardcontents.this, "삭제된 글입니다.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }
}
